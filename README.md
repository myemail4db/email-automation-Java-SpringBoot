# Email Automation - Java Spring Boot

![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)
![Status](https://img.shields.io/badge/status-active%20development-yellow)
![License](https://img.shields.io/badge/license-MIT-green)
![API](https://img.shields.io/badge/API-Gmail-red)

## Table of Contents

- [Email Automation - Java Spring Boot](#email-automation---java-spring-boot)
  - [Table of Contents](#table-of-contents)
  - [Overview](#overview)
  - [Project Documentation](#project-documentation)
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
  - [Documentation Source](#documentation-source)

---

## Overview

Email Automation is a Java Spring Boot application for processing recruiter and job opportunity emails through the Gmail API.

The project originated from a recruiter-email automation process that was first implemented in Python and reduced a manual workflow from approximately 12 hours to about 90 minutes.

The Java implementation was developed as a separate solution to achieve the same core processing outcomes using a different architecture and implementation approach. It uses Java 17, Spring Boot, REST APIs, separated services, DTOs, and OAuth2 Gmail integration.

The Java project is maintained as an independent application with its own architecture, source code, testing, configuration, and documentation.

---

## Project Documentation

A documentation website is included with the project for a more detailed walkthrough of the application.

It covers:

* project background and evolution from the original Python implementation
* application architecture
* end-to-end workflow
* engineering and implementation decisions
* REST API endpoints
* setup and local execution
* current development status
* project roadmap

The documentation website starts at:

```text
docs/index.html
```

When published through GitHub Pages, the same documentation can be viewed as a website directly from the repository's GitHub Pages URL.

The original Markdown documentation used as source material for the website is preserved under:

```text
docs/assets/source/
```

---

## Workflow

```mermaid
flowchart TD
    A["Gmail Authentication<br/>(Completed)"] --> B["Email Retrieval<br/>(Completed)"]
    B --> C["Content Extraction<br/>(Completed)"]
    C --> D["Text Filtering<br/>(Completed)"]
    D --> E["TXT / DOCX Export<br/>(Completed)"]
    E --> F["ZIP Processing<br/>(In Development)"]
    F --> G["Outbound Email<br/>(In Development)"]
    G --> H["Workflow Reporting<br/>(Planned)"]
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

See the documentation website's **Current Status** page or [`docs/assets/source/06-current-status.md`](docs/assets/source/06-current-status.md) for additional implementation details.

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

For additional setup information, see the documentation website's **Setup & Run** page or [`docs/assets/source/05-setup-and-run-guide.md`](docs/assets/source/05-setup-and-run-guide.md).

---

## Configuration

Application configuration is managed through the Spring Boot resources directory:

```text
backend/src/main/resources/
```

For example, the export destination can be configured in `application.properties`:

```properties
email.export.export-successful=processed_review
```

OAuth credentials and tokens contain sensitive information and should never be committed to the repository.

See the documentation website's **Setup & Run** page for additional configuration information.

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

See the documentation website's **API** page or [`docs/assets/source/04-api-endpoints.md`](docs/assets/source/04-api-endpoints.md) for the current API reference.

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

See the documentation website's **Roadmap** page or [`docs/assets/source/07-roadmap.md`](docs/assets/source/07-roadmap.md) for planned development.

---

## Documentation Source

The portfolio documentation website is maintained under `docs/`.

```text
docs/
├── index.html
├── pages/
│   ├── project-background.html
│   ├── architecture.html
│   ├── workflow.html
│   ├── engineering.html
│   ├── api.html
│   ├── setup.html
│   ├── status.html
│   └── roadmap.html
└── assets/
    ├── css/
    ├── js/
    └── source/
        ├── 01-project-background.md
        ├── 02-architecture-overview.md
        ├── 03-implementation-notes.md
        ├── 04-api-endpoints.md
        ├── 05-setup-and-run-guide.md
        ├── 06-current-status.md
        └── 07-roadmap.md
```

The website provides the guided project walkthrough, while the Markdown files under `docs/assets/source/` preserve the original technical documentation used as its source.
