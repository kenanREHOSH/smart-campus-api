# Smart Campus API

## Overview
The Smart Campus API is a robust, scalable, and highly available RESTful web service built using JAX-RS (Jakarta RESTful Web Services) and the Jersey implementation. It manages rooms and various sensors (such as CO2, temperature, and occupancy monitors) deployed across the university campus. The API follows RESTful architectural patterns, employing proper resource nesting, correct HTTP status codes, and meaningful JSON payloads.

## Build and Launch Instructions
1. Ensure you have Java 17+ and Apache Maven installed on your machine.
2. Clone or extract this repository and navigate to the project root directory.
3. Build the project using Maven:
   ```bash
   mvn clean install
   ```
4. Start the embedded Grizzly server:
   ```bash
   mvn exec:java
   ```
5. The API will be available at `http://localhost:8080/api/v1/`.

## Sample API Interactions (Curl Commands)

1. **Discovery Endpoint**: Retrieve API metadata.
   ```bash
   curl -X GET http://localhost:8080/api/v1/
   ```

2. **Create a Room**: Register a new room.
   ```bash
   curl -X POST http://localhost:8080/api/v1/rooms \
   -H "Content-Type: application/json" \
   -d '{"name": "Library Study Area", "capacity": 50}'
   ```

3. **Register a Sensor**: Add a sensor to an existing room.
   ```bash
   curl -X POST http://localhost:8080/api/v1/sensors \
   -H "Content-Type: application/json" \
   -d '{"roomId": "REPLACE_WITH_ROOM_ID", "type": "CO2", "status": "ACTIVE"}'
   ```

4. **Add a Sensor Reading**: Append a new reading to a sensor's history.
   ```bash
   curl -X POST http://localhost:8080/api/v1/sensors/REPLACE_WITH_SENSOR_ID/readings \
   -H "Content-Type: application/json" \
   -d '{"value": 415.5}'
   ```

5. **Fetch Sensor Readings**: Retrieve historical readings for a specific sensor.
   ```bash
   curl -X GET http://localhost:8080/api/v1/sensors/REPLACE_WITH_SENSOR_ID/readings
   ```

---


---

# REPORT

## PART 1: SERVICE ARCHITECTURE & SETUP

### Question 1.1: Resource Lifecycle in JAX-RS
Resource classes in JAX-RS are request-scoped by default. A new instance is created for each HTTP request and destroyed after the response.  
This prevents shared state issues at object level. However, shared data structures like HashMaps require synchronization to avoid race conditions.

---

### Question 1.2: Importance of Hypermedia (HATEOAS)
HATEOAS allows APIs to include links in responses, making them self-descriptive.  
Clients can navigate dynamically without hardcoding URLs, improving flexibility and scalability.

---

## PART 2: ROOM MANAGEMENT

### Question 2.1: Returning IDs vs Full Objects
Returning only IDs reduces payload size and improves performance in low-bandwidth situations.  
However, it requires additional requests. Returning full objects increases payload size but reduces the number of requests.

---

### Question 2.2: Idempotency of DELETE Operation
DELETE is idempotent because repeating the request does not change the system state.  
After deletion, further requests may return 404, but no additional changes occur.

---

## PART 3: SENSOR OPERATIONS

### Question 3.1: Handling Unsupported Media Types
Using `@Consumes(MediaType.APPLICATION_JSON)` ensures the API only accepts JSON.  
If another format is used, JAX-RS returns HTTP 415 Unsupported Media Type.

---

### Question 3.2: @QueryParam vs @PathParam
@PathParam is used for accessing a specific resource.  
@QueryParam is used for filtering or searching collections.  
Query parameters are more flexible for multiple filtering conditions.

---

## PART 4: DEEP NESTING WITH SUB-RESOURCES

### Question 4.1: Sub Resource Locator Pattern
This pattern delegates nested routes (like `/sensors/{id}/readings`) to separate classes.  
It improves code structure, maintainability, and avoids large controller classes.

---

## PART 5: ADVANCED ERROR HANDLING & LOGGING

### Question 5.1: HTTP 422 vs 404
404 indicates a missing endpoint.  
422 is used when the request is valid but contains invalid data (e.g., wrong roomId).

---

### Question 5.2: Risks of Exposing Stack Traces
Stack traces expose internal details like file paths and class names, which can be exploited.  
The API uses exception mappers to return safe responses.

---

### Question 5.3: Advantages of Using Filters for Logging
Filters handle logging centrally for all requests and responses.  
This avoids code duplication and separates logging from business logic.
