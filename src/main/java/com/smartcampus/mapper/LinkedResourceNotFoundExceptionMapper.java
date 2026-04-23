package com.smartcampus.mapper;

import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.model.ErrorMessage;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException> {
    @Override
    public Response toResponse(LinkedResourceNotFoundException exception) {
        // Return 422 Unprocessable Entity
        ErrorMessage errorMessage = new ErrorMessage(exception.getMessage(), 422);
        return Response.status(422)
                .entity(errorMessage)
                .type("application/json")
                .build();
    }
}
