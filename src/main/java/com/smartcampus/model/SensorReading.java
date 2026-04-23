package com.smartcampus.model;

import java.time.Instant;
import java.util.UUID;

public class SensorReading {
    private String id;
    private String sensorId;
    private Double value;
    private Instant timestamp;

    public SensorReading() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = Instant.now();
    }

    public SensorReading(String sensorId, Double value) {
        this();
        this.sensorId = sensorId;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
