package org.example.controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.dto.ReservationDto;
import org.example.service.ReservationService;

@Path("/protected/reservation")
@Produces(MediaType.APPLICATION_JSON)
public class ReservationController {

    private ReservationService reservationService = new ReservationService();

    @POST
    public Response createReservation(ReservationDto reservationDto) {
        try{
            return Response.status(Response.Status.OK).entity(reservationService.createReservation(reservationDto)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("cant create reservation: "+ e.getMessage()).build();
        }
    }

    @GET
    public Response getAllReservations(){
        try{
            return Response.status(Response.Status.OK).entity(reservationService.getAllReservations()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("cant get reservations: "+ e.getMessage()).build();
        }
    }

    @GET
    @Path("/{userId}")
    public Response getReservationsByUserId(@PathParam("userId") Long userId) {
        try{
            return Response.status(Response.Status.OK).entity(reservationService.getReservationsByUserId(userId)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("cant get reservations: "+ e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}/bill")
    @Produces("application/pdf")
    public Response getReservationBill(@PathParam("id") Long id) {
        try {
            byte[] pdfBytes = reservationService.generateBillPdf(id);
            return Response.ok(pdfBytes).header("Content-Disposition", "attachment; filename=\"reservation_bill_" + id + ".pdf\"").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to generate bill: " + e.getMessage()).build();
        }
    }
}
