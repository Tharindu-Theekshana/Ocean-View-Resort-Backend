package org.example.controller;


import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.dto.UserDto;
import org.example.model.User;
import org.example.service.AuthService;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthController {

    private AuthService authService = new AuthService();

    @POST
    public Response register(UserDto userDto) {
        try{
            return Response.status(Response.Status.OK).entity(authService.register(userDto)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("cant register user: "+ e.getMessage()).build();
        }
    }

    @POST
    @Path("/login")
    public Response login(User user) {
        try{
            return Response.status(Response.Status.OK).entity(authService.login(user)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("cant login user: "+ e.getMessage()).build();
        }
    }

}
