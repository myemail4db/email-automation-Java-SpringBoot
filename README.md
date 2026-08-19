# Email Automation - Java Spring Boot <!-- omit from toc -->

![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)
![Status](https://img.shields.io/badge/status-active%20development-yellow)
![License](https://img.shields.io/badge/license-MIT-green)
![API](https://img.shields.io/badge/API-Gmail-red)

## Table of Contents <!-- omit from toc -->

- [Overview](#overview)
- [Workflow](#workflow)
- [Technologies](#technologies)
- [Current Status](#current-status)
  - [Completed](#completed)
  - [In Development](#in-development)
- [Running Locally](#running-locally)
  - [Prerequisites](#prerequisites)
  - [Clone the Repository](#clone-the-repository)
  - [Build the Application](#build-the-application)
  - [Run the Application](#run-the-application)
  - [Verify the Application](#verify-the-application)
- [Configuration](#configuration)
- [API](#api)
- [Project Direction](#project-direction)
- [Documentation](#documentation)

## Overview

Email Automation is a Java Spring Boot application for processing recruiter and job opportunity emails through the Gmail API.

The project originated from a recruiter-email automation process that was first implemented in Python and reduced a manual workflow from approximately 12 hours to about 90 minutes.

The Java implementation was developed as a separate solution to achieve the same core processing outcomes using a different architecture and implementation approach. It uses Java 17, Spring Boot, REST APIs, separated services, DTOs, and OAuth2 Gmail integration.

The Java project is maintained as an independent application with its own architecture, source code, testing, configuration, and documentation.

---

## Workflow

```mermaid
flowchart LR
    A[Gmail Label] --> B[Retrieve Emails]
    B --> C[Extract Content]
    C --> D[Filter Text]
    D --> E[TXT / DOCX Export]
    E --> F[ZIP Processing<br/>(In Development)]
    F --> G[Outbound Email<br/>(In Development)]
```

The application processes emails from a user-designated Gmail label and does not automatically organize or move messages from the user's Inbox.

---

## Technologies

* Java 17
* Spring Boot
* Maven
* Gmail API
* OAuth 2.0
* REST APIs
* Apache POI
* JUnit 5
* Mockito

---

## Current Status

### Completed

* Spring Boot backend setup
* Gmail OAuth2 authentication
* Gmail API connectivity
* Gmail label-based email retrieval
* Email subject and body extraction
* DTO-based email processing
* Recruiter email text filtering and normalization
* TXT export
* DOCX export
* Duplicate filename handling
* Configurable export directory

### In Development

* ZIP export workflow
* Outbound email delivery
* Final workflow reporting
* Expanded unit test coverage

See [Current Status](backend/docs/07-current-status.md) for additional implementation details.

---

## Running Locally

### Prerequisites

* Java 17
* Maven
* Git
* Google account
* Google Cloud project with the Gmail API enabled
* OAuth credentials for the Gmail API

### Clone the Repository

```bash
git clone <repository-url>
cd email-automation/backend
```

### Build the Application

```bash
mvn clean compile
```

### Run the Application

```bash
mvn spring-boot:run
```

The Spring Boot backend runs locally at:

```text
http://localhost:8080
```

### Verify the Application

```bash
curl http://localhost:8080/api/health
```

For Google Cloud configuration, OAuth setup, credentials, redirect URI configuration, and troubleshooting, see the [Setup and Run Guide](backend/docs/06-setup-and-run-guide.md).

---

## Configuration

Application configuration is managed through the Spring Boot resources directory:

```text
backend/src/main/resources/
```

For example, the export destination can be configured in `application.properties`:

```properties
email.export.output-dir=processed_review
```

OAuth credentials and tokens contain sensitive information and should never be committed to the repository.

See the [Setup and Run Guide](backend/docs/06-setup-and-run-guide.md) for detailed configuration instructions.

---

## API

The Spring Boot backend exposes REST endpoints for application health, Gmail integration, and workflow processing.

Examples include:

```text
GET /api/health
GET /api/gmail/status
GET /api/gmail/emails
GET /api/export
```

See [API Endpoints](backend/docs/05-api-endpoints.md) for the current API reference.

---

## Project Direction

The immediate goal is to complete the Java Spring Boot application so that it independently provides the same core processing outcomes as the original Python email automation solution.

Although both applications address the same business problem, the Java implementation uses its own architecture and development approach. Future development of the Java project will continue independently rather than requiring the Python implementation.

After the core Java workflow is completed, possible future enhancements include:

* React-based text filtering, preview, and user review
* Security hardening and secure credential storage
* Encrypted ZIP exports
* Secure backup and recovery
* Improved workflow reporting and monitoring
* Additional email provider support

These are future possibilities and are not part of the current completed implementation.

See the [Roadmap](backend/docs/08-roadmap.md) for planned development.

---

## Documentation

Detailed project documentation is available under `backend/docs/`.

| Document                                                          | Purpose                                                             |
| ----------------------------------------------------------------- | ------------------------------------------------------------------- |
| [Project Background](backend/docs/01-project-background.md)       | Origin of the project and relationship to the Python implementation |
| [Architecture Overview](backend/docs/02-architecture-overview.md) | Backend architecture and component responsibilities                 |
| [Implementation Notes](backend/docs/03-implementation-notes.md)   | Implementation decisions and development notes                      |
| [API Endpoints](backend/docs/04-api-endpoints.md)                 | REST API reference                                                  |
| [Setup and Run Guide](backend/docs/05-setup-and-run-guide.md)     | Detailed local setup and Gmail OAuth configuration                  |
| [Current Status](backend/docs/06-current-status.md)               | Current implementation status                                       |
| [Roadmap](backend/docs/07-roadmap.md)                             | Planned development and future enhancements                         |

---

