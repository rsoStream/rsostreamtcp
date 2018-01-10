package com.rsostream.tcp.services;

import com.google.gson.Gson;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.rsostream.tcp.models.*;
import com.rsostream.tcp.util.InvalidMessageException;
import com.rsostream.tcp.util.InvalidMessageTypeException;
import com.rsostream.tcp.util.RabbitMQException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

/**
 * Message received by the device is in the following format:
 * #</messageType>=</Attribute#1>,</Attribute#2>,</Attribute#3>, ..., </Attribute#N>|
 * where allowed message types are:
 *  - gps reading, includes the following attributes:
 *      - time of obtaining the reading
 *      - device IMEI code
 *      - signal quality, int 0-99
 *      - latitude
 *      - longitude
 *      - speed (km/h)
 *    sample message: #gps=1514412395,990000862471854,23,46.049982,14.469495,122|
 *  - device battery reading, includes the following attributes:
 *      - time of obtaining the reading
 *      - current voltage
 *    sample message: #battery=1514412395,990000862471854,23,1234|
 *  - humidity reading, includes the following attributes:
 *      - time of obtaining the reading
 *      - temperature level (celsius)
 *      - humidity level (percent)
 *    sample message: #humidity=1514412395,990000862471854,23,20,32|
 *  - altitude reading, includes the following attributes:
 *      - time of obtaining the reading
 *      - altitude level (meters)
 *    sample message: #altitude=1514412395,990000862471854,23,520|
 *  - lux reading, includes the following attributes:
 *      - time of obtaining the reading
 *      - lux level (meters)
 *    sample message: #lux=1514412395,990000862471854,23,120|
 *  -
 */
@ApplicationScoped
public class ServiceReadingConverter {

    private static final Logger log = LogManager.getLogger(ServiceReadingConverter.class.getName());

    @Inject
    private ServiceRabbitMQ rabbitMQ;

    private SensorReading convert(String rawMessage) throws InvalidMessageException {
        if (rawMessage.charAt(0) != '#') throw new InvalidMessageException();
        if (rawMessage.charAt(rawMessage.length() - 1) != '|') throw new InvalidMessageException();
        String[] msgData = rawMessage.substring(1, rawMessage.length() - 2).split("=");
        String type = msgData[0];
        msgData = msgData[1].split(",");
        SensorReading currentReading;
        switch (type) {
            case "gps":
                currentReading = GPSReading.createReading(msgData);
                break;
            case "battery":
                currentReading = BatteryReading.createReading(msgData);
                break;
            case "humidity":
                currentReading = HumidityReading.createReading(msgData);
                break;
            case "altitude":
                currentReading = AltitudeReading.createReading(msgData);
                break;
            case "lux":
                currentReading = LuxReading.createReading(msgData);
                break;
            default:
                throw new InvalidMessageTypeException();
        }
        return currentReading;
    }

    public boolean receiveReading(String message) throws RabbitMQException {
        Gson gson = new Gson();
        try {
            SensorReading currentReading = convert(message);
            String response = gson.toJson(currentReading);
            log.info("Obtained: " + response);
            return rabbitMQ.publish(response);
        } catch (InvalidMessageException e) {
            return false;
        }
    }
}
