package com.rsostream.tcp.server.resources;

import com.google.gson.Gson;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.logs.cdi.Log;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rsostream.tcp.converter.InvalidMessage;
import com.rsostream.tcp.converter.ReadingConverter;
import com.rsostream.tcp.models.SensorReading;
import com.rsostream.tcp.server.properties.PropertiesRabbitMQ;
import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.annotation.Metric;
import org.eclipse.microprofile.metrics.annotation.Timed;

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
    private PropertiesRabbitMQ propertiesRabbitMQ;

    @Inject
    @Metric(name = "req_counter")
    private Counter reqCounter;

    private Connection connection;
    private Channel channel;


    @PostConstruct
    private void init() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(propertiesRabbitMQ.getHost());
        // not required for the basic setup!
//        factory.setHost(propertiesRabbitMQ.getUsername());
//        factory.setHost(propertiesRabbitMQ.getPassword());
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare(propertiesRabbitMQ.getRoutingKey(), true, false, false, null);
            log.info("Created new channel!!!");
        } catch (IOException | TimeoutException e) {
            log.error("[connection] Fails here!");
            log.error(e.getMessage());
        }
    }

    @PreDestroy
    private void stop() {
        try {
            channel.close();
            connection.close();
        } catch (IOException | TimeoutException e) {
            log.error(e.getMessage());
        }
    }

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
            channel.basicPublish(propertiesRabbitMQ.getExchangeName(), propertiesRabbitMQ.getRoutingKey(),
                    null, response.getBytes());
            return Response.ok(response).build();
        } catch (InvalidMessage | IOException | IllegalStateException e) {
            log.error("[sending] Fails here!");
            log.error(e.getMessage());
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
