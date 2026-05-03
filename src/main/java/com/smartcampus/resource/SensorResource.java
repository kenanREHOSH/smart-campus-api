package com.smartcampus.resource;

import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
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
        
        // Add sensor ID to the parent room's sensorIds list
        Room room = dataStore.getRoom(sensor.getRoomId());
        if (room != null) {
            room.getSensorIds().add(sensor.getId());
        }
        
        return Response.status(Response.Status.CREATED).entity(sensor).build();
    }

    @Path("/{id}/readings")
    public SensorReadingResource getSensorReadingResource(@PathParam("id") String id) {
        return new SensorReadingResource(id);
    }
}
