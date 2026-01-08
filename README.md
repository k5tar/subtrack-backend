# Project README

## Overview

This project is a Java-based Command Line Interface (CLI) application designed to search for the cheapest travel or
service plan and complete reservations. It integrates with external APIs (GET/POST), processes user-provided JSON input,
applies filtering logic based on search criteria, and outputs a clean, structured response.  
The application serves as a foundation for building more complex booking or search engines.

## Key Features

- Fully interactive command-line interface for easy execution
- GET and POST API communication using HttpClient or RestTemplate
- Robust JSON argument parsing (using Jackson)
- Filtering logic based on search conditions (e.g., destination, date, price)
- End-to-end reservation workflow including validation and response handling
- Modular and extensible architecture with clear separation of concerns
- Error handling for network issues, invalid input, and API failures
- Ready to expand with unit tests and additional feature modules

## Requirements

- Java 17 or later
- Maven or Gradle
- Internet connection (if external APIs are used)

## How to Build (Maven)

```
mvn clean package
```

## How to Run

```
java -jar target/app.jar --from TOKYO --to OSAKA --date 2025-01-10
```

Example arguments:

- `--from` : Departure location
- `--to` : Destination
- `--date` : Date of reservation
- `--limit` : (Optional) Max price or filtering threshold

## Directory Structure

```
/src
  /main
    /java
      (Core application code including client, service, util packages)
    /resources
      (Configuration files such as application.yml)
  /test
    /java
      (Unit and integration tests)
README.md
```

## Architecture Overview

The project follows a layered design:

- **Client Layer:** Handles GET/POST calls to external APIs
- **Service Layer:** Business logic including search, filtering, and reservation workflows
- **Model Layer:** Request/Response objects, domain models
- **Utility Layer:** JSON utilities, date formatter, argument parser

This separation makes the code maintainable and scalable.

## How to Contribute

1. Fork the repository
2. Create your feature branch
3. Submit a pull request
4. Ensure all tests pass and code follows formatting guidelines

## License

This project is released under the MIT License.