# Ocean View Resort Backend

A robust RESTful API backend for the Ocean View Resort management system built with Java, Jakarta EE (JAX-RS/Jersey), and Hibernate ORM.

## Architecture & Tech Stack

This project is structured using the MVC pattern (Model, View/Controller, Service, Repository) and relies on the following technologies:
- **Language**: Java 22
- **Framework**: Jakarta EE (Servlets, JAX-RS via Jersey)
- **Database ORM**: Hibernate Core (6.2.7)
- **Database**: PostgreSQL (42.6.0)
- **Authentication**: JSON Web Tokens (JWT via Auth0 / JJWT)
- **Password Hashing**: BCrypt
- **JSON Parsing**: Jackson Databind
- **PDF Generation**: iTextPDF

## Core Features

### 1. Authentication & Security
- **Registration**: Securely register new users with BCrypt password hashing (`/api/auth`).
- **Login**: Issue short-lived JWTs containing role-based claims (`/api/auth/login`).
- **Authorization**: Custom `JwtFilter` deployed on `/api/protected/*` paths to ensure endpoints are accessed solely by authenticated requests with valid Bearer tokens.

### 2. Room Management
- **Room Creation**: Support for `multipart/form-data` uploads, saving room details and uploading image binaries directly to the local filesystem (`/api/room`).
- **Room Lookups**: Retrieve all rooms, a specific room by ID, or filter by specific enum combinations like `SINGLE`, `DOUBLE`, `DELUXE`, or `SUITE`. Images are dynamically Base64-encoded on retrieval.

### 3. Reservation System
- **Booking Workflows**: Supports booking for both existing registered guests and brand new walk-in guests (`/api/protected/reservation`).
- **Bill Generation**: Built-in `/api/protected/reservation/{id}/bill` endpoint that dynamically generates a formatted `.pdf` receipt utilizing iTextPDF, calculating days stayed using Java `LocalDate` math multiplied by the specific room's nightly rate.

## Setup Instructions

### Prerequisites
- JDK 22 or higher
- Apache Tomcat (v10+ recommended to support Jakarta EE 10/Servlet 6.0)
- PostgreSQL Server
- Maven

### Database Configuration
1. Ensure your PostgreSQL instance is running.
2. Inside `src/main/resources/hibernate.cfg.xml` (or `application.properties`), configure the target local database URL, username, and password.
3. Hibernate is set up to auto-initialize and map all entities (`User`, `UserDetail`, `Role`, `UserRole`, `Room`, `Reservation`) upon Context Initialization via `DBListener`.

### File Uploads
1. The app stores uploaded room images locally.
2. Verify or set the `upload.path` directory in `src/main/resources/application.properties` (e.g., `upload.path=C:/uploads/oceanview/`). Ensure the Tomcat process has read/write permissions to this folder.

### Running the Application (Locally with Maven/Tomcat)
1. Build the WAR file:
   ```bash
   mvn clean install
   ```
2. Deploy the generated `oceanviewresort-1.0-SNAPSHOT.war` located in the `/target` directory to your Tomcat `webapps` folder, or run directly via a SmartTomcat configuration in IntelliJ IDEA.
3. Access the APIs at: `http://localhost:8080/oceanviewresort_war_exploded/api/...` (depending on your context path).

## Postman API Testing

When testing endpoints that lie under `/api/protected/*` (like making reservations), ensure you:
1. Hit `/api/auth/login` to obtain your JWT.
2. Add an `Authorization` header in Postman:
   - **Key**: `Authorization`
   - **Value**: `Bearer <your_jwt_string>`
