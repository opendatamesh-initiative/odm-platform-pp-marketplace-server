# ODM Platform Marketplace Server

The Open Data Mesh Platform's Marketplace service server is a Spring Boot application that provides marketplace
functionality for the Open Data Mesh Platform.

## Overview

This service is part of the Open Data Mesh Platform initiative, providing marketplace capabilities for data products and
services.

## Core Functionalities

### Access Request Management

- Submit new access requests to the marketplace
- Handle executor responses for access requests

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- PostgreSQL (for production) or H2 (for development)

## Setup Instructions

### 1. Database Configuration

#### PostgreSQL (Production)

```properties
# application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/marketplace
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

#### H2 (Development)

```properties
# application.properties
spring.datasource.url=jdbc:h2:mem:marketplace
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
```

### 2. Building the Project

```bash
# Clone the repository
git clone https://github.com/opendatamesh-initiative/odm-platform-pp-marketplace-server.git

# Navigate to project directory
cd odm-platform-pp-marketplace-server

# Build the project
mvn clean install
```

### 3. Running the Application

#### Local Development

```bash
# Run with default configuration
mvn spring-boot:run

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

#### Docker Deployment

```bash
# Build the Docker image
docker build -t odm-marketplace-server .

# Run the container
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/marketplace \
  -e SPRING_DATASOURCE_USERNAME=your_username \
  -e SPRING_DATASOURCE_PASSWORD=your_password \
  odm-marketplace-server
```

## Configuration Options

### Application Properties

The application can be configured using the following properties in `application.yml` or `application.properties`:

#### Server Configuration
```yaml
server:
  port: 8080  # The port on which the application will run
```

#### Spring Configuration
```yaml
spring:
  application:
    name: odm-platform-pp-marketplace-server
  banner:
    charset: UTF-8
    mode: console
```

#### Database Configuration
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/marketplace  # Database connection URL
    username: your_username                            # Database username
    password: your_password                            # Database password
  jpa:
    properties:
      hibernate:
        default_schema: marketplace_data              # Default schema for database tables
```

#### Flyway Database Migration
```yaml
spring:
  flyway:
    baselineOnMigrate: true                           # Allow baseline migrations
    locations: classpath:db/migration/postgresql      # Location of migration scripts
    schemas: public                                   # Target schema for migrations
    validateOnMigrate: false                          # Skip validation during migration
    outOfOrder: true                                  # Allow out-of-order migrations
```

#### ODM Platform Configuration
```yaml
odm:
  product-plane:
    notification-service:
      address: http://localhost:8083                  # Notification service URL
      active: true                                    # Enable/disable notification service
  utility-plane:
    marketplace-executors:                            # List of marketplace executors
      - name: EXEC-001                               # Executor name
        address: http://localhost:8080               # Executor URL
        active: true                                 # Enable/disable executor
```

#### Logging Configuration
```yaml
logging:
  pattern:
    console: "%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} %clr(%5p) %clr(${PID:- }){magenta} %clr(---){faint} %clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n%wEx"
    file: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
  level:
    org.springframework.web.filter.CommonsRequestLoggingFilter: DEBUG
```

These properties can be overridden using environment variables or command-line arguments when running the application.

### Docker Spring JSON Configuration

When running the application in Docker, you can pass the Spring configuration as a JSON string using the `SPRING_PROPS` environment variable. Here's an example:

```bash
docker run -p 8080:8080 \
  -e SPRING_PROPS='{"spring":{"datasource":{"url":"jdbc:postgresql://db:5432/marketplace","username":"your_username","password":"your_password"},"jpa":{"properties":{"hibernate":{"default_schema":"marketplace_data"}}}},"odm":{"product-plane":{"notification-service":{"address":"http://notification-service:8083","active":true}},"utility-plane":{"marketplace-executors":[{"name":"EXEC-001","address":"http://executor:8080","active":true}]}}}' \
  odm-marketplace-server
```

The JSON structure follows the same hierarchy as the YAML configuration:

```json
{
  "spring": {
    "datasource": {
      "url": "jdbc:postgresql://db:5432/marketplace",
      "username": "your_username",
      "password": "your_password"
    },
    "jpa": {
      "properties": {
        "hibernate": {
          "default_schema": "marketplace_data"
        }
      }
    }
  },
  "odm": {
    "product-plane": {
      "notification-service": {
        "address": "http://notification-service:8083",
        "active": true
      }
    },
    "utility-plane": {
      "marketplace-executors": [
        {
          "name": "EXEC-001",
          "address": "http://executor:8080",
          "active": true
        }
      ]
    }
  }
}
```

This approach is particularly useful when you need to configure multiple properties at once in a Docker environment, as it allows you to pass all configuration in a single environment variable.

### Environment Variables

The application can be configured using environment variables:

- `SPRING_PROFILES_ACTIVE`: Set active profile (dev, prod)
- `SPRING_DATASOURCE_URL`: Database connection URL
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password
- `SERVER_PORT`: Application port
- `SERVER_SERVLET_CONTEXT_PATH`: API context path

## API Documentation

Once the application is running, you can access:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI Specification: `http://localhost:8080/api-docs`

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Support

For support, please open an issue in the GitHub repository.

## Acknowledgments

- Open Data Mesh Initiative
- All contributors to this project 