package com.rsostream.tcp.services;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rsostream.tcp.properties.PropertiesRabbitMQ;
import com.rsostream.tcp.util.RabbitMQException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

@ApplicationScoped
public class ServiceRabbitMQPublish {

    private static final Logger log = LogManager.getLogger(ServiceRabbitMQPublish.class.getName());

    @Inject
    private PropertiesRabbitMQ propertiesRabbitMQ;

    private Connection connection;
    private Channel channel;

    @PostConstruct
    private void init(){
        ConnectionFactory factory = new ConnectionFactory();
        try {
            factory.setUri(propertiesRabbitMQ.getUri());
            factory.setHost(propertiesRabbitMQ.getHost());
            // not required for the basic setup!
            factory.setUsername(propertiesRabbitMQ.getUsername());
            factory.setPassword(propertiesRabbitMQ.getPassword());
            factory.setVirtualHost(propertiesRabbitMQ.getUsername());
            factory.setRequestedHeartbeat(30);
            factory.setConnectionTimeout(30000);
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare(propertiesRabbitMQ.getRoutingKey(), true, false, false, null);
            log.info("Channel '" + propertiesRabbitMQ.getRoutingKey() + "' created.");
        } catch (IOException | TimeoutException | KeyManagementException
                | URISyntaxException | NoSuchAlgorithmException e) {
            log.error("Failed to create channel.");
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

    public boolean publish(String message) throws RabbitMQException{
        try {
            channel.basicPublish(
                    propertiesRabbitMQ.getExchangeName(),
                    propertiesRabbitMQ.getRoutingKey(),
                    null,
                    message.getBytes());
        } catch (IOException | IllegalStateException e) {
            throw new RabbitMQException("Failed to send message" + e.getMessage());
        }
        return true;
    }
}
