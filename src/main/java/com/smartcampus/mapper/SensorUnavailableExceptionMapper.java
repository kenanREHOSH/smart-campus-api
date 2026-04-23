package com.smartcampus.mapper;

import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.ErrorMessage;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class SensorUnavailableExceptionMapper implements ExceptionMapper<SensorUnavailableException> {
    @Override
    public Response toResponse(SensorUnavailableException exception) {
        // Return 403 Forbidden
        ErrorMessage errorMessage = new ErrorMessage(exception.getMessage(), 403);
        return Response.status(Response.Status.FORBIDDEN)
                .entity(errorMessage)
                .type("application/json")
                .build();
    }
}
