package com.rsostream.tcp.resources;

import com.google.gson.Gson;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.logs.cdi.Log;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rsostream.tcp.services.ServiceRabbitMQ;
import com.rsostream.tcp.util.InvalidMessageException;
import com.rsostream.tcp.services.ServiceReadingConverter;
import com.rsostream.tcp.models.SensorReading;
import com.rsostream.tcp.util.RabbitMQException;
import com.sun.org.apache.regexp.internal.RE;
import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.annotation.Metric;
import org.eclipse.microprofile.metrics.annotation.Timed;
import com.rsostream.tcp.properties.PropertiesRabbitMQ;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Path("/tcp")
@ApplicationScoped
public class TCPResource {

    private static final Logger log = LogManager.getLogger(TCPResource.class.getName());

    @Inject
    @Metric(name = "req_counter")
    private Counter reqCounter;

    @Inject
    private ServiceReadingConverter converter;

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
        reqCounter.inc();
        return Response.ok().build();
    }

}
