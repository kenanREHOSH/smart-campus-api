package com.smartcampus.resource;

import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import com.smartcampus.store.DataStore;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final DataStore dataStore = DataStore.getInstance();
    private final String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    public Response getSensorReadings() {
        Sensor sensor = dataStore.getSensor(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        List<SensorReading> readings = dataStore.getReadingsForSensor(sensorId);
        return Response.ok(readings).build();
    }

    @POST
    public Response addSensorReading(SensorReading reading) {
        Sensor sensor = dataStore.getSensor(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException("Cannot add reading: Sensor " + sensorId + " is in MAINTENANCE state.");
        }
        
        if (reading.getId() == null) {
            reading = new SensorReading(sensorId, reading.getValue());
        } else {
            reading.setSensorId(sensorId);
        }
        
        dataStore.addReading(reading);
        
        // Update sensor current value
        sensor.setCurrentValue(reading.getValue());
        
        return Response.status(Response.Status.CREATED).entity(reading).build();
    }
}
