# Setup and Run Guide

## Prerequisites

- Java 17
- Maven
- Git
- Google account
- Google Cloud project with Gmail API enabled
- OAuth credentials for Gmail API access

## Clone the Repository

```bash
git clone <repository-url>
cd email-automation/backend
```

## Build

```bash
mvn clean compile
```

## Run

```bash
mvn spring-boot:run
```

The backend runs locally at:

```text
http://localhost:8080
```

## Verify

```bash
curl http://localhost:8080/api/health
```

## Configuration

Spring Boot application configuration is stored under:

```text
backend/src/main/resources/
```

Example:

```properties
email.export.output-dir=processed_review
```

## Gmail OAuth

A Google Cloud project with the Gmail API enabled and appropriate OAuth credentials is required.

OAuth client credentials, access tokens, refresh tokens, and other sensitive configuration must not be committed to the repository.
