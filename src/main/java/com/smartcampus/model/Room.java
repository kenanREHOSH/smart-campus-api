package com.smartcampus.model;

import java.util.UUID;

public class Room {
    private String id;
    private String name;
    private int capacity;

    public Room() {
        this.id = UUID.randomUUID().toString();
    }

    public Room(String name, int capacity) {
        this();
        this.name = name;
        this.capacity = capacity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
