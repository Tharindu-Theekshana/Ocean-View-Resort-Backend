package org.example.service;

import org.example.model.Room;
import org.example.model.RoomType;
import org.example.repository.RoomRepository;
import org.jvnet.hk2.annotations.Service;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
public class RoomService {

    private String uploadDir;

    public RoomService() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                return;
            }
            prop.load(input);
            this.uploadDir = prop.getProperty("upload.path");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private RoomRepository roomRepository = new RoomRepository();

    public Room createRoom(Room room, InputStream imageStream, FormDataContentDisposition fileDetail) {
        if (imageStream != null && fileDetail != null && fileDetail.getFileName() != null) {
            String fileName = UUID.randomUUID() + "_" + fileDetail.getFileName();
            String filePath = uploadDir + fileName;

            try {
                Files.createDirectories(Paths.get(uploadDir));
                Files.copy(imageStream, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
                room.setImagePath(filePath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save image: " + e.getMessage(), e);
            }
        }
        return roomRepository.save(room);
    }

    public Map<String, Object> getRoomById(Long id) {
        Room room = roomRepository.findById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("id", room.getId());
        response.put("type", room.getType());
        response.put("price", room.getPrice());
        response.put("description", room.getDescription());

        if (room.getImagePath() != null) {
            try {
                byte[] imageBytes = Files.readAllBytes(Paths.get(room.getImagePath()));
                String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                response.put("image", base64Image);
            } catch (IOException e) {
                response.put("image", null);
            }
        }
        return response;
    }

    public List<Map<String, Object>> getRoomsByType(String type) {
        RoomType roomType = RoomType.valueOf(type.toUpperCase());
        List<Room> rooms = roomRepository.findByType(roomType);
        List<Map<String, Object>> response = new ArrayList<>();
        for (Room room : rooms) {
            Map<String, Object> roomMap = new HashMap<>();
            roomMap.put("id", room.getId());
            roomMap.put("type", room.getType());
            roomMap.put("price", room.getPrice());
            roomMap.put("description", room.getDescription());

            if (room.getImagePath() != null) {
                try {
                    byte[] imageBytes = Files.readAllBytes(Paths.get(room.getImagePath()));
                    roomMap.put("image", Base64.getEncoder().encodeToString(imageBytes));
                } catch (IOException e) {
                    roomMap.put("image", null);
                }
            }
            response.add(roomMap);
        }
        return response;
    }
}
