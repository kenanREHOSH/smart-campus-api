package com.smartcampus.resource;

import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.store.DataStore;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {
    private final DataStore dataStore = DataStore.getInstance();

    @GET
    public Response getAllRooms() {
        List<Room> rooms = dataStore.getAllRooms();
        return Response.ok(rooms).build();
    }

    @POST
    public Response createRoom(Room room) {
        if (room.getId() == null) {
            room = new Room(room.getName(), room.getCapacity());
        }
        dataStore.addRoom(room);
        return Response.status(Response.Status.CREATED).entity(room).build();
    }

    @GET
    @Path("/{id}")
    public Response getRoom(@PathParam("id") String id) {
        Room room = dataStore.getRoom(id);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(room).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteRoom(@PathParam("id") String id) {
        Room room = dataStore.getRoom(id);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // Check if there are any sensors in the room
        boolean hasSensors = dataStore.getAllSensors().stream()
                .anyMatch(sensor -> id.equals(sensor.getRoomId()));
                
        if (hasSensors) {
            throw new RoomNotEmptyException("Cannot delete room: sensors are assigned to it.");
        }

        dataStore.deleteRoom(id);
        return Response.noContent().build();
    }
}
