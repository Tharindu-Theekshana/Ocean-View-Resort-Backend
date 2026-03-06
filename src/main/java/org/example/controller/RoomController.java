package org.example.controller;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.utils.JwtUtil;
import org.example.model.Room;
import org.example.service.RoomService;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import java.io.InputStream;

@Path("/room")
@Produces(MediaType.APPLICATION_JSON)
public class RoomController {

    private RoomService roomService = new RoomService();

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response createRoom(@FormDataParam("roomDetails") Room room, @FormDataParam("image") InputStream imageStream,
                               @FormDataParam("image") FormDataContentDisposition fileDetail, @Context HttpServletRequest httpRequest){
        String authHeader = httpRequest.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Missing token\"}")
                    .build();
        }
        try {
            JwtUtil.validateToken(authHeader.substring(7).trim());
        } catch (ExpiredJwtException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Token expired\"}")
                    .build();
        } catch (JwtException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Invalid token\"}")
                    .build();
        }

        try {
            return Response.status(Response.Status.OK).entity(roomService.createRoom(room,imageStream, fileDetail)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("cant create room: "+ e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getRoomById(@PathParam("id") Long id) {
        try {
            return Response.status(Response.Status.OK).entity(roomService.getRoomById(id)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("cant get room: "+ e.getMessage()).build();
        }
    }

    @GET()
    @Path("/type")
    public Response getRoomsByType(@QueryParam("type") String type) {
        try {
            return Response.status(Response.Status.OK).entity(roomService.getRoomsByType(type)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid room type: " + type).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("cant get rooms: " + e.getMessage()).build();
        }
    }

    @GET()
    public Response getAllRooms() {
        try {
            return Response.status(Response.Status.OK).entity(roomService.getAllRooms()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("cant get all rooms: "+ e.getMessage()).build();
        }
    }

}
