package com.rsostream.tcp.server;

import com.google.gson.Gson;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.logs.cdi.Log;
import com.rsostream.tcp.converter.InvalidMessage;
import com.rsostream.tcp.converter.ReadingConverter;
import com.rsostream.tcp.models.SensorReading;
import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.annotation.Metric;
import org.eclipse.microprofile.metrics.annotation.Timed;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/tcp")
@ApplicationScoped
public class TCPResource {

    private static final Logger log = LogManager.getLogger(TCPResource.class.getName());

    @Inject
    @Metric(name = "req_counter")
    private Counter reqCounter;

    @Log
    @POST
    @Path("/receive")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    @Metric(name = "requests")
    @Timed(name = "measure_transformation")
    public Response receiveData(String message) {
        log.info("Received data:" + message);
        Gson gson = new Gson();
        try {
            SensorReading currentReading = ReadingConverter.convert(message);
            String response = gson.toJson(currentReading);
            log.info("Obtained: " + response);
            return Response.ok(response).build();
        } catch (InvalidMessage invalidMessage) {
            invalidMessage.printStackTrace();
            return Response.status(400).build();
        }
    }

    @Log
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Metric(name = "no-requests")
    @Timed(name = "exec-time")
    public Response empty() {
        reqCounter.inc();
        return Response.ok().build();
    }

}
