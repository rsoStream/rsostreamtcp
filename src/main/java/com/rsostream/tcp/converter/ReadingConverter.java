package com.rsostream.tcp.converter;

import com.rsostream.tcp.models.*;

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
public class ReadingConverter {

    public static SensorReading convert(String rawMessage) throws InvalidMessage {
        if (rawMessage.charAt(0) != '#') throw new InvalidMessage();
        if (rawMessage.charAt(rawMessage.length() - 1) != '|') throw new InvalidMessage();
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
                throw new InvalidMessageType();
        }
        return currentReading;
    }
}
