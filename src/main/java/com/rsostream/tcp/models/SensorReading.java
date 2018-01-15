package com.rsostream.tcp.models;

import com.rsostream.tcp.util.InvalidMessageException;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;


@XmlRootElement
public class SensorReading {
    public final EnumType TYPE;
    private Date timeObtained;
    private String IMEI;
    private int signalQuality;
    private static int numberOfAttributes = 3;

    SensorReading(Date timeObtained, String IMEI, int signalQuality) {
        TYPE = EnumType.SUPER;
        this.timeObtained = timeObtained;
        this.IMEI = IMEI;
        this.signalQuality = signalQuality;
    }

    SensorReading(EnumType type, Date timeObtained, String IMEI, int signalQuality) {
        TYPE = type;
        this.timeObtained = timeObtained;
        this.IMEI = IMEI;
        this.signalQuality = signalQuality;
    }

    public Date getTimeObtained() {
        return timeObtained;
    }

    public void setTimeObtained(Date timeObtained) {
        this.timeObtained = timeObtained;
    }

    public int getNumberOfAttributes() {
        return numberOfAttributes;
    }

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public int getSignalQuality() {
        return signalQuality;
    }

    public void setSignalQuality(int signalQuality) {
        this.signalQuality = signalQuality;
    }

    public static SensorReading createReading(String[] data) throws InvalidMessageException {
        if (data.length != SensorReading.numberOfAttributes) {
            throw new InvalidMessageException();
        }
        Date dateObtained = new Date(Long.parseLong(data[0]));
        String IMEI = data[1];
        int signalQuality = Integer.parseInt(data[2]);
        return new SensorReading(dateObtained, IMEI, signalQuality);
    }
}
