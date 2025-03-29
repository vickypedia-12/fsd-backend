# Backend for Hotel Management System

This project is a Spring Boot-based backend for the Hotel Management system. It is configured with Maven and currently supports a couple of simple REST endpoints for testing.

## Prerequisites

- Java 21
- Maven (or use the provided Maven wrapper)

## Build and Run

### Using Maven Wrapper

To build the project:

```
./mvnw clean install
```

To run the application:

```
./mvnw spring-boot:run

or Search for HotelMgmtApplication.java and do Run Java
```

### Verify the Server

Once the application is running, verify the endpoints:

- Access the default endpoint:

  ```
  curl http://localhost:8080/
  ```

  Expected response:

  ```
  Server is Up and Running!
  ```

- Access the hello endpoint:

  ```
  curl http://localhost:8080/hello/YourName
  ```

  Expected response:

  ```
  Hello YourName
  ```

## Current Configuration

- **Spring Boot:** Currently configured without an active datasource. The `DataSourceAutoConfiguration` is disabled in `HotelMgmtApplication.java` as the database is not in use for now.

## Additional Information

- **Swagger/OpenAPI:** The project includes the `springdoc-openapi-starter-webmvc-ui` dependency for API documentation. Access the Swagger UI at:
  
  ```
  http://localhost:8080/swagger-ui/index.html
  ```

Happy coding!