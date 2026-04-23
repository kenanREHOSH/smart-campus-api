package com.smartcampus.store;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DataStore {
    private static DataStore instance;

    // Use concurrent collections for thread-safe in-memory storage
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private final Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    private final List<SensorReading> readings = new CopyOnWriteArrayList<>();

    private DataStore() {
        // Private constructor to enforce Singleton
    }

    public static synchronized DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    // Room operations
    public void addRoom(Room room) {
        rooms.put(room.getId(), room);
    }

    public Room getRoom(String id) {
        return rooms.get(id);
    }

    public List<Room> getAllRooms() {
        return new ArrayList<>(rooms.values());
    }

    public void deleteRoom(String id) {
        rooms.remove(id);
    }

    // Sensor operations
    public void addSensor(Sensor sensor) {
        sensors.put(sensor.getId(), sensor);
    }

    public Sensor getSensor(String id) {
        return sensors.get(id);
    }

    public List<Sensor> getAllSensors() {
        return new ArrayList<>(sensors.values());
    }

    public void deleteSensor(String id) {
        sensors.remove(id);
    }

    // Reading operations
    public void addReading(SensorReading reading) {
        readings.add(reading);
    }

    public List<SensorReading> getReadingsForSensor(String sensorId) {
        List<SensorReading> result = new ArrayList<>();
        for (SensorReading reading : readings) {
            if (reading.getSensorId().equals(sensorId)) {
                result.add(reading);
            }
        }
        return result;
    }
}
