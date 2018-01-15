package com.rsostream.tcp.resources;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.logs.cdi.Log;
import com.rsostream.tcp.properties.PropertiesRabbitMQ;
import com.rsostream.tcp.services.ServiceReadingConverter;
import com.rsostream.tcp.util.RabbitMQException;
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

    @Inject
    private ServiceReadingConverter converter;

    @Inject
    private PropertiesRabbitMQ properties;

    @Log
    @POST
    @Path("/receive")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    @Metric(name = "requests")
    @Timed(name = "measure_transformation")
    public Response receiveData(String message) {
        log.info("Received data:" + message);
        try {
            if(converter.receiveReading(message)) {
                return Response.ok().build();
            } else {
                return Response.status(400).build();
            }
        } catch (RabbitMQException e) {
            return Response.status(500).build();
        }
    }

    @Log
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Metric(name = "no-requests")
    @Timed(name = "exec-time")
    public Response empty() {
        String response =
                "{" +
                        "\"host\": \"%s\"," +
                        "\"routingKey\": \"%s\"," +
                        "\"exchangeName\": %s" +
                        "}";

        response = String.format(
                response,
                properties.getHost(),
                properties.getRoutingKey(),
                properties.getExchangeName());
        reqCounter.inc();
        return Response.ok(response).build();
    }

}
