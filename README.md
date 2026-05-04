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

## Conceptual Report

### 1.1 Service Architecture & Setup
**Question:** Explain the default lifecycle of a JAX-RS Resource class. Is a new instance instantiated for every incoming request, or does the runtime treat it as a singleton? Elaborate on how this architectural decision impacts the way you manage and synchronize your in-memory data structures (maps/lists) to prevent data loss or race conditions.

**Answer:**
By default, the lifecycle of a JAX-RS Resource class is Request-Scoped. This means the JAX-RS runtime (such as Jersey) creates a new instance of the resource class for every single incoming HTTP request, and the instance is immediately destroyed once the response is sent.
Because a new instance is created for each request, storing state in instance variables inside the resource class itself is ineffective. To maintain persistent data, we rely on a Singleton pattern for our in-memory data store (`DataStore.java`). Since multiple requests might access and modify this single, shared data store simultaneously across different threads, we must use thread-safe data structures like `ConcurrentHashMap` or explicit synchronization blocks. Without proper synchronization, concurrent writes to standard structures like `HashMap` or `ArrayList` could lead to data loss or race conditions.

### 1.2 The "Discovery" Endpoint
**Question:** Why is the provision of "Hypermedia" (links and navigation within responses) considered a hallmark of advanced RESTful design (HATEOAS)? How does this approach benefit client developers compared to static documentation?

**Answer:**
HATEOAS (Hypermedia As The Engine Of Application State) allows the API to become self-documenting and dynamically discoverable. By returning hypermedia links alongside the data, the API tells the client exactly what state transitions (or endpoints) are currently valid and available. 
This benefits client developers significantly because they don't have to hardcode URLs or constantly refer to static documentation to understand the API structure. If the server changes a route, the client dynamically adapts by following the updated links provided in the JSON responses, reducing client-server tight coupling and brittleness.

### 2.1 Room Resource Implementation
**Question:** When returning a list of rooms, what are the implications of returning only IDs versus returning the full room objects? Consider network bandwidth and client side processing.

**Answer:**
Returning only IDs minimizes the payload size, which drastically reduces network bandwidth consumption and improves response speed, especially for large collections. However, it shifts the processing burden to the client, which must make subsequent GET requests to fetch the details of each specific ID, leading to the "N+1 query problem" over HTTP.
Returning the full room objects increases bandwidth usage and parsing overhead, but allows the client to immediately render the full dataset without issuing further network requests.

### 2.2 Room Deletion & Safety Logic
**Question:** Is the DELETE operation idempotent in your implementation? Provide a detailed justification by describing what happens if a client mistakenly sends the exact same DELETE request for a room multiple times.

**Answer:**
Yes, the DELETE operation is idempotent. An operation is idempotent if executing it once has the same effect on the server state as executing it multiple times. 
In our implementation, the first DELETE request for a specific room (assuming it has no active sensors) successfully removes the room from the data store and returns a `204 No Content`. If the client mistakenly sends the exact same DELETE request again, the server simply checks the data store, sees the room no longer exists, and safely returns a `404 Not Found`. While the HTTP status codes differ, the underlying state of the server remains identical—the room is deleted—meaning idempotency is maintained.

### 3.1 Sensor Resource & Integrity
**Question:** We explicitly use the `@Consumes(MediaType.APPLICATION_JSON)` annotation on the POST method. Explain the technical consequences if a client attempts to send data in a different format, such as text/plain or application/xml. How does JAX-RS handle this mismatch?

**Answer:**
If a client attempts to send data with a `Content-Type` header that does not match `application/json` (such as `text/plain` or `application/xml`), JAX-RS immediately intercepts the request before it reaches the resource method. Since no method can satisfy the requested media type, the JAX-RS runtime will automatically reject the request and return an HTTP `415 Unsupported Media Type` response to the client. This ensures type safety and offloads format validation from the business logic.

### 3.2 Filtered Retrieval & Search
**Question:** You implemented this filtering using `@QueryParam`. Contrast this with an alternative design where the type is part of the URL path (e.g., `/api/v1/sensors/type/CO2`). Why is the query parameter approach generally considered superior for filtering and searching collections?

**Answer:**
Path parameters (`@PathParam`) are best used to identify a specific, singular resource within a hierarchy (e.g., `/sensors/{id}`). 
Query parameters (`@QueryParam`) are generally superior for filtering, sorting, or searching because they represent optional modifiers to a broader collection rather than a distinct hierarchical identity. Using `/api/v1/sensors?type=CO2` conceptually means "give me the collection of sensors, but narrow it down to CO2 types." This approach is much more flexible, as multiple query parameters can be easily combined (e.g., `?type=CO2&status=ACTIVE`), whereas deeply nesting optional path segments quickly becomes rigid, confusing, and difficult to route dynamically.

### 4.1 The Sub-Resource Locator Pattern
**Question:** Discuss the architectural benefits of the Sub-Resource Locator pattern. How does delegating logic to separate classes help manage complexity in large APIs compared to defining every nested path (e.g., `sensors/{id}/readings/{rid}`) in one massive controller class?

**Answer:**
The Sub-Resource Locator pattern allows a parent resource (like `SensorResource`) to route an incoming request to a completely separate class (like `SensorReadingResource`) to handle the sub-path. 
This dramatically improves modularity and separation of concerns. In a large API, placing all nested routes inside a single massive controller leads to "god classes" that are difficult to read, maintain, and test. By delegating the logic, `SensorReadingResource` is strictly responsible for reading operations, reducing code clutter. Additionally, it allows the sub-resource to be instantiated with context (like the parent's `sensorId`), cleanly passing state down the hierarchy.

### 5.1 Dependency Validation
**Question:** Why is HTTP 422 often considered more semantically accurate than a standard 404 when the issue is a missing reference inside a valid JSON payload?

**Answer:**
An HTTP `404 Not Found` typically implies that the actual URL/endpoint the client requested does not exist. 
If a client POSTs valid JSON to `/api/v1/sensors`, the endpoint itself *does* exist. However, if the payload contains a `roomId` that references a non-existent room, the server is unable to process the instructions contained within the payload. HTTP `422 Unprocessable Entity` is semantically perfect for this scenario: it tells the client, "The content type is correct, the JSON is syntactically valid, and the endpoint exists, but the semantic instructions (the missing foreign key) cannot be processed."

### 5.4 The Global Safety Net
**Question:** From a cybersecurity standpoint, explain the risks associated with exposing internal Java stack traces to external API consumers. What specific information could an attacker gather from such a trace?

**Answer:**
Exposing raw Java stack traces to external consumers is a severe security vulnerability known as "Information Disclosure." 
An attacker analyzing a stack trace can gather critical details about the internal architecture, including:
- Exact versions of frameworks and libraries used (e.g., Jersey, Jackson), allowing them to cross-reference known CVEs (Common Vulnerabilities and Exposures).
- Internal server file paths and package structures (`com.smartcampus...`).
- The specific line of code or logic flaw that caused the crash (e.g., a missing null check on an authentication token).
This intelligence can be used to construct highly targeted exploits against the system.

### 5.5 API Request & Response Logging Filters
**Question:** Why is it advantageous to use JAX-RS filters for cross-cutting concerns like logging, rather than manually inserting `Logger.info()` statements inside every single resource method?

**Answer:**
Using JAX-RS filters implements Aspect-Oriented Programming (AOP), cleanly separating cross-cutting concerns (like logging, authentication, and CORS headers) from core business logic. 
If you manually insert `Logger.info()` inside every resource method, it leads to massive code duplication and clutters the controllers. Furthermore, if you need to change the logging format or add timing metrics, you would have to modify every single method in the project. By using a `ContainerRequestFilter` and `ContainerResponseFilter`, the logic is centralized in one place. It guarantees consistent, automatic execution for every API request, reducing boilerplate and human error.
