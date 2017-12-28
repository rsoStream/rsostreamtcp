package com.rsostream.tcp.models;

import com.rsostream.tcp.converter.InvalidMessage;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement
public class AltitudeReading extends SensorReading {
    private float altitude;
    public static int numberOfAttributes = 4;

    AltitudeReading(Date dateObtained, String IMEI, int signalQuality, float altitude) {
        super(dateObtained, IMEI, signalQuality);
        this.altitude = altitude;
    }

    public float getAltitude() {
        return altitude;
    }

    public void setAltitude(float altitude) {
        this.altitude = altitude;
    }

    public static AltitudeReading createReading(String[] data) throws InvalidMessage {
        if (data.length != AltitudeReading.numberOfAttributes) {
            throw new InvalidMessage();
        }
        Date dateObtained = new Date(Long.parseLong(data[0]));
        String IMEI = data[1];
        int signalQuality = Integer.parseInt(data[2]);
        float altitude = Float.parseFloat(data[3]);
        return new AltitudeReading(dateObtained, IMEI, signalQuality, altitude);
    }
}
