# Architecture Overview

## Overview

Email Automation is a Java 17 Spring Boot application that processes recruiter and job opportunity emails through the Gmail API.

The application uses a service-based backend architecture that separates Gmail authentication, email retrieval, content extraction, text filtering, file generation, ZIP processing, and outbound email responsibilities.

The current architecture is centered around a REST-driven workflow coordinated by Spring Boot services.

## Current Processing Flow

```text
ExportController
      |
      v
EmailExportService
  |-- GmailService -- GmailAuthService -- Gmail API
  |-- EmailBodyExtractorService
  |-- TextFilterService
  |-- FileExportService -- TXT / DOCX
  |-- ZipExportService       [In Development]
  `-- EmailSendService       [In Development]
```

## Project Structure

```text
backend/
|-- src/
|   |-- main/
|   |   |-- java/com/example/email_automation/
|   |   |   |-- controller/
|   |   |   |-- dto/
|   |   |   |-- model/
|   |   |   |-- repository/
|   |   |   `-- service/
|   |   `-- resources/
|   `-- test/
|-- docs/
`-- pom.xml
```

## Controller Layer

Current controllers include `HealthController`, `GmailController`, `ExportController`, and `EmailController`.

`ExportController` receives the requested export format and coordinates the user-facing export request through the service layer.

## Gmail Authentication Layer

`GmailAuthService` handles OAuth client credentials, the Google authorization flow, local authorization tokens, and creation of an authenticated Gmail API client.

## Gmail Service Layer

`GmailService` handles Gmail operations after authentication, including connectivity validation, account profile access, and message retrieval from the configured processing label.

## Processing Services

`EmailBodyExtractorService` converts Gmail message data into the application's internal representation.

`TextFilterService` normalizes and cleans email content.

`FileExportService` creates TXT and DOCX files and handles export filenames.

`ZipExportService` packages generated files.

`EmailSendService` is intended to send the completed archive through Gmail.

`EmailExportService` coordinates these services as one business workflow.
