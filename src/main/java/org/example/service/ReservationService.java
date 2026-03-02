package org.example.service;

import org.example.dto.ReservationDto;
import org.example.model.Reservation;
import org.example.model.Room;
import org.example.model.UserDetail;
import org.example.repository.ReservationRepository;
import org.example.repository.RoomRepository;
import org.example.repository.UserDetailRepository;
import org.jvnet.hk2.annotations.Service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ReservationService {

    private ReservationRepository reservationRepository = new ReservationRepository();
    private RoomRepository roomRepository = new RoomRepository();
    private UserDetailRepository userDetailRepository = new UserDetailRepository();

    public Object createReservation(ReservationDto reservationDto) {
        UserDetail userDetail = userDetailRepository.findByUserId(reservationDto.getUserId());
        if(userDetail == null) {
            throw new RuntimeException("no user found");
        }
        Room room = roomRepository.findById(reservationDto.getRoomId());
        Reservation reservation = new Reservation();
        reservation.setCheckInDate(reservationDto.getCheckInDate());
        reservation.setCheckOutDate(reservationDto.getCheckOutDate());
        reservation.setUserDetail(userDetail);
        reservation.setNetTotal(calculateDays(reservationDto.getCheckInDate(), reservationDto.getCheckOutDate()) * room.getPrice());
        reservation.setRoom(room);
        return reservationRepository.save(reservation);
    }

    public List<Reservation> getReservationsByUserId(Long userId) {
        UserDetail userDetail = userDetailRepository.findByUserId(userId);
        if(userDetail == null) {
            throw new RuntimeException("no user found");
        }
        return reservationRepository.findByUserDetailId(userDetail.getId());
    }
    public byte[] generateBillPdf(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId);
        if (reservation == null) {
            throw new RuntimeException("Reservation not found with id: " + reservationId);
        }

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);

            Paragraph title = new Paragraph("Ocean View Resort", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph subtitle = new Paragraph("Reservation Bill", headerFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(20);
            document.add(subtitle);


            document.add(new Paragraph("Guest Details", headerFont));
            document.add(new Paragraph("Name: " + reservation.getUserDetail().getName(), normalFont));
            document.add(new Paragraph("Phone: " + reservation.getUserDetail().getPhoneNumber(), normalFont));
            document.add(new Paragraph("Address: " + reservation.getUserDetail().getAddress(), normalFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Reservation Details", headerFont));
            document.add(new Paragraph("Reservation ID: " + reservation.getId(), normalFont));
            document.add(new Paragraph("Room Type: " + reservation.getRoom().getType(), normalFont));
            document.add(new Paragraph("Check-in Date: " + reservation.getCheckInDate(), normalFont));
            document.add(new Paragraph("Check-out Date: " + reservation.getCheckOutDate(), normalFont));
            document.add(new Paragraph("Total nights stayed: " + calculateDays(reservation.getCheckInDate(), reservation.getCheckOutDate()), normalFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Bill Summary", headerFont));
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);

            table.addCell(new PdfPCell(new Phrase("Description", headerFont)));
            table.addCell(new PdfPCell(new Phrase("Amount (Rs.)", headerFont)));

            table.addCell(new PdfPCell(new Phrase("Room price per night", normalFont)));
            table.addCell(new PdfPCell(new Phrase("" + reservation.getRoom().getPrice(), normalFont)));

            table.addCell(new PdfPCell(new Phrase("Net Total", headerFont)));
            table.addCell(new PdfPCell(new Phrase("" + reservation.getNetTotal(), headerFont)));

            document.add(table);

            document.add(new Paragraph(" "));
            Paragraph footer = new Paragraph("Thank you for staying with us!", normalFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            return baos.toByteArray();

        } catch (DocumentException e) {
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage(), e);
        }
    }


    private long calculateDays(String checkInDate, String checkOutDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate checkIn = LocalDate.parse(checkInDate, formatter);
        LocalDate checkOut = LocalDate.parse(checkOutDate, formatter);
        long daysBetween = ChronoUnit.DAYS.between(checkIn, checkOut);
        return daysBetween;
    }
}
