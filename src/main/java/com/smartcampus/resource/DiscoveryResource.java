package com.smartcampus.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/")
public class DiscoveryResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response discoverApi() {
        Map<String, Object> apiInfo = new HashMap<>();
        apiInfo.put("title", "Smart Campus API");
        apiInfo.put("version", "1.0");
        apiInfo.put("contact", "admin@smartcampus.westminster.ac.uk");
        
        Map<String, String> links = new HashMap<>();
        links.put("discovery", "/api/v1");
        links.put("rooms", "/api/v1/rooms");
        links.put("sensors", "/api/v1/sensors");
        
        apiInfo.put("_links", links);
        
        return Response.ok(apiInfo).build();
    }
}
