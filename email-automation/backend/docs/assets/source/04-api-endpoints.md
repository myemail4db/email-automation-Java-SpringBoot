# API Endpoints

## Overview

The Spring Boot backend exposes REST endpoints for application health, Gmail integration, and workflow processing.

## Health

```text
GET /api/health
```

Used to verify that the Spring Boot application is running.

## Gmail Status

```text
GET /api/gmail/status
```

Used to validate Gmail connectivity and integration.

## Gmail Emails

```text
GET /api/gmail/emails
```

Retrieves messages through the Gmail integration.

## Export Workflow

```text
GET /api/export
```

Starts the email export workflow using the requested format.

Example:

```bash
curl "http://localhost:8080/api/export?format=text"
```

The final workflow response will continue to evolve while Version 1 workflow reporting is completed.
