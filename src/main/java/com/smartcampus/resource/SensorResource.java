package com.smartcampus.resource;

import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import com.smartcampus.store.DataStore;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {
    private final DataStore dataStore = DataStore.getInstance();

    @GET
    public Response getSensors(@QueryParam("type") String type) {
        List<Sensor> sensors = dataStore.getAllSensors();
        if (type != null && !type.trim().isEmpty()) {
            sensors = sensors.stream()
                .filter(s -> type.equalsIgnoreCase(s.getType()))
                .collect(Collectors.toList());
        }
        return Response.ok(sensors).build();
    }

    @POST
    public Response createSensor(Sensor sensor) {
        // Validate room exists
        if (sensor.getRoomId() == null || dataStore.getRoom(sensor.getRoomId()) == null) {
            throw new LinkedResourceNotFoundException("Room ID not found: " + sensor.getRoomId());
        }
        
        if (sensor.getId() == null) {
            sensor = new Sensor(sensor.getRoomId(), sensor.getType());
        }
        dataStore.addSensor(sensor);
        return Response.status(Response.Status.CREATED).entity(sensor).build();
    }

    @GET
    @Path("/{id}/readings")
    public Response getSensorReadings(@PathParam("id") String id) {
        Sensor sensor = dataStore.getSensor(id);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        List<SensorReading> readings = dataStore.getReadingsForSensor(id);
        return Response.ok(readings).build();
    }

    @POST
    @Path("/{id}/readings")
    public Response addSensorReading(@PathParam("id") String id, SensorReading reading) {
        Sensor sensor = dataStore.getSensor(id);
        if (sensor == null) {
            throw new SensorUnavailableException("Cannot add reading: Sensor " + id + " does not exist.");
        }
        
        if (reading.getId() == null) {
            reading = new SensorReading(id, reading.getValue());
        } else {
            reading.setSensorId(id);
        }
        
        dataStore.addReading(reading);
        
        // Update sensor current value
        sensor.setCurrentValue(reading.getValue());
        
        return Response.status(Response.Status.CREATED).entity(reading).build();
    }
}
