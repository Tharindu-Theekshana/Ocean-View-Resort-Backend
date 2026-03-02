package org.example.service;

import org.example.model.Room;
import org.example.repository.RoomRepository;
import org.jvnet.hk2.annotations.Service;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Properties;
import java.util.UUID;

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

}
