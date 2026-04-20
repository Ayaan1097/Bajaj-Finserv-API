# Auto SQL Solver

This Spring Boot application starts automatically and executes an end-to-end webhook workflow on startup.

## What it does

- Sends a POST request to generate a webhook and receive `webhookUrl` and `accessToken`
- Determines whether to solve Question 1 or Question 2 using the last two digits of `regNo`
- Fetches the SQL question description dynamically from the configured Google Drive link
- Builds the final SQL query as a string without executing SQL
- Sends the final query to the test webhook endpoint using the JWT access token

## Project structure

- `src/main/java/com/bajajfinserv/config` - WebClient configuration
- `src/main/java/com/bajajfinserv/service` - business logic services
- `src/main/java/com/bajajfinserv/model` - request/response DTOs
- `src/main/java/com/bajajfinserv/runner` - startup runner
- `src/main/resources/application.properties` - externalized configuration

## Run instructions

1. Open a terminal in the project root directory.
2. Build and run the project:

```bash
mvn spring-boot:run
```

The application will execute automatically on startup.

## Customize credentials

Update `src/main/resources/application.properties` with your own values:

```properties
app.credentials.name=Your Name
app.credentials.reg-no=REG12347
app.credentials.email=your@email.com
```

## Notes

- No controllers are defined; the entire flow executes in `ApplicationStartupRunner`
- `WebClient` is used for HTTP communication
- The final SQL query is constructed as a string and submitted in the request body
