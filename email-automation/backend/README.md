# Email Automation Backend

## Overview

This backend application is the current Java Spring Boot iteration of the Email Automation project.

The original workflow was first developed in Python to automate the manual process of organizing recruiter and job application emails. After validating the workflow and reducing a manual 12-hour process down to about 90 minutes, the project continued evolving into its next iteration using Java Spring Boot.

The current backend focuses on rebuilding the workflow using a cleaner backend architecture and preparing the system for future workflow automation, analytics, and frontend integration.

---

## Current Features

Completed:
- Spring Boot backend setup
- Gmail OAuth2 integration
- Gmail API integration
- Gmail connectivity validation
- Gmail label retrieval
- recruiter email retrieval
- email subject extraction
- email body extraction
- DTO architecture separation
- REST API endpoints

Current endpoints:
- `/api/health`
- `/api/export`
- `/api/gmail/status`
- `/api/gmail/emails`

---

## Current Architecture

```text
Gmail API
↓
GmailAuthService
↓
GmailService
↓
DTO Processing
↓
Text Filtering
↓
Export Pipeline
↓
Future Database Persistence
```

---

## Project Structure

```
backend/
├── src/main/java/com/example/email_automation
│   ├── controller/
│   ├── service/
│   ├── dto/
│   ├── model/
│   ├── repository/
│   └── EmailAutomationApplication.java
│
├── src/main/resources/
│
├── pom.xml
└── README.md
```

---

## Technology Stack

Backend:
- Java 17
- Spring Boot
- Maven

Integration:
- Gmail API
- OAuth2

Architecture:
- REST APIs
- service-layer architecture
- DTO pattern

---

## Current Development Focus

Current work is focused on:
- recruiter email normalization
- text filtering improvements
- export pipeline preparation
- DTO population cleanup

Planned export support:
- `.txt`
- `.docx`
- zip batch exports

---

## Running the Backend

### Start the application

```bash
mvn spring-boot:run
```

Default local URL:

```
http://localhost:8080
```

---

## Gmail OAuth Setup

The backend uses OAuth2 and the Gmail API for Gmail access.

Required:
- Google Cloud OAuth credentials
- Gmail API enabled
- local OAuth token generation

Credentials location:

```
src/main/resources/config/credentials.json
```

The OAuth token is generated locally during the first successful Gmail authentication flow.

---

## Notes

This project is currently in active backend development.

The current implementation focuses on rebuilding and improving the original Python workflow using a cleaner backend architecture and more structured service-layer organization.

Future phases will expand the project into workflow automation, analytics, recruiter tracking, and frontend integration.