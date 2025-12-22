# Nexia – Installed Packages & Dependencies

This document describes the frameworks, libraries, and tools currently installed and configured for the **Nexia Core** service.

---

## Core Platform

- **Java 21**
    - Language runtime and compilation target
    - Chosen for long-term support and modern language features

- **Spring Boot 3.x**
    - Primary application framework
    - Provides autoconfiguration, embedded server, and production-ready defaults

- **Maven**
    - Build and dependency management tool
    - Standard in enterprise Java environments

---

## Spring Boot Starters & Libraries

### 1. Spring Web
- Enables REST API development
- Provides:
    - Embedded Tomcat
    - `@RestController`, `@RequestMapping`, JSON serialization

### 2. Spring Data JPA
- ORM abstraction over JPA/Hibernate
- Simplifies database access using repositories
- Supports entity management and query generation

### 3. PostgreSQL Driver
- JDBC driver for PostgreSQL
- Enables connectivity between the application and the Postgres database

### 4. Spring Boot Actuator
- Provides production-ready endpoints
- Used for:
    - Health checks
    - Application info
    - Monitoring and observability

### 5. Validation (Jakarta Validation)
- Bean validation support
- Enables annotations such as:
    - `@NotNull`
    - `@Size`
    - `@Email`

### 6. Lombok
- Reduces boilerplate code
- Commonly used annotations:
    - `@Getter`, `@Setter`
    - `@Builder`
    - `@RequiredArgsConstructor`

### 7. Spring Boot DevTools
- Improves local development experience
- Provides:
    - Automatic restart
    - Live reload support

---

## Container & Infrastructure

- **Docker**
    - Used to containerize infrastructure and services
    - Ensures environment consistency

- **Docker Compose**
    - Manages local infrastructure dependencies
    - Currently used for:
        - PostgreSQL database

---

## Project Structure (Initial)

nexia-core/
├── .mvn/
│   └── wrapper/
│       ├── maven-wrapper.jar
│       └── maven-wrapper.properties
├── mvnw
├── mvnw.cmd
├── pom.xml
├── PACKAGES.md
├── compose.yaml
└── src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── nexia/
│   │           └── core/
│   │               └── NexiaCoreApplication.java
│   └── resources/
│       ├── application.yml
│       ├── static/
│       └── templates/
└── test/
└── java/
└── com/
└── nexia/
└── core/
└── NexiaCoreApplicationTests.java



## Purpose

This setup provides:
- A production-grade backend foundation
- A scalable microservices-ready architecture
- A clean base for adding:
  - Additional services
  - Authentication
  - Messaging
  - AI / LLM integrations

Further dependencies will be documented in this file 
as the project evolves.