package com.rsostream.tcp.server;

import com.google.gson.Gson;
import com.rsostream.tcp.converter.InvalidMessage;
import com.rsostream.tcp.converter.ReadingConverter;
import com.rsostream.tcp.models.SensorReading;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/tcp")
@ApplicationScoped
public class TCPResource {

    @POST
    @Path("/receive")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response receiveData(String message) {
        System.out.println("Received:" + message);
        Gson gson = new Gson();
        try {
            SensorReading currentReading = ReadingConverter.convert(message);
            String response = gson.toJson(currentReading);
            return Response.ok(response).build();
        } catch (InvalidMessage invalidMessage) {
            invalidMessage.printStackTrace();
            return Response.status(400).build();
        }
    }
}
