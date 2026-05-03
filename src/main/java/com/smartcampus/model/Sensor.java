package com.smartcampus.model;

import java.util.UUID;

public class Sensor {
    private String id;
    private String roomId;
    private String type; // e.g., CO2, TEMPERATURE, OCCUPANCY
    private String status; // "ACTIVE", "MAINTENANCE", or "OFFLINE"
    private Double currentValue;

    public Sensor() {
        this.id = UUID.randomUUID().toString();
        this.status = "ACTIVE"; // default status
    }

    public Sensor(String roomId, String type) {
        this();
        this.roomId = roomId;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(Double currentValue) {
        this.currentValue = currentValue;
    }
}
