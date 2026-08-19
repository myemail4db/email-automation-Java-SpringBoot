# Setup and Run Guide

## Overview

This guide explains how to configure and run the Java Spring Boot Email Automation application locally.

The application uses the Gmail API with OAuth 2.0 authentication to retrieve emails from a user-designated Gmail label and process them through the email automation workflow.

---

## Version 1 Scope

The first Java implementation is intended to provide functionality equivalent to the original Python email automation program while using an independent Java Spring Boot architecture and implementation approach.

Version 1 focuses on the end-to-end email processing workflow:

1. Retrieve emails from the configured Gmail label
2. Extract email content
3. Filter and normalize the content
4. Generate TXT or DOCX files
5. Create a ZIP archive
6. Send the ZIP archive by email
7. Return the workflow result

ZIP processing and outbound email delivery are currently being completed.

---

## Prerequisites

Before running the application, install or configure:

- Java 17
- Maven
- Git
- Google account
- Google Cloud project
- Gmail API
- OAuth 2.0 credentials

---

## Clone the Repository

Clone the project:

```bash
git clone <repository-url>
```

Move into the backend directory:

```bash
cd email-automation-Java-SpringBoot/backend
```

---

## Verify Java

Confirm that Java is installed:

```bash
java -version
```

The project is configured for Java 17.

---

## Verify Maven

Confirm that Maven is available:

```bash
mvn -version
```

---

# Gmail API Setup

## Create a Google Cloud Project

Create or select a project in the Google Cloud Console.

The project provides Gmail API access for the application.

---

## Enable the Gmail API

Within the Google Cloud project:

1. Open the API Library.
2. Locate the Gmail API.
3. Enable the Gmail API for the project.

---

## Configure OAuth

Configure the OAuth consent screen for the Google Cloud project.

The Gmail integration uses OAuth 2.0 authorization.

During development, the Google account used to test the application may need to be added as an authorized test user depending on the OAuth application configuration.

---

## Create OAuth Credentials

Create OAuth client credentials for a desktop application.

Download the generated client credential file and place it in the location expected by the application.

OAuth credentials contain sensitive information and must not be committed to Git.

---

## Gmail Authorization

When Gmail functionality is used for the first time, the application starts the Google OAuth authorization flow.

The user is asked to sign in to the Google account and authorize the requested Gmail access.

After successful authorization, the application stores the authorization token locally so that authorization does not normally need to be repeated every time the application starts.

The stored authorization token must also be treated as sensitive information and must not be committed to Git.

---

# Gmail Label Configuration

The application processes emails from a designated Gmail label.

The current development label is:

```text
for_friend
```

The Gmail label can be configured through:

```properties
email.gmail.label=for_friend
```

Emails that should be processed by the application must be placed in the configured Gmail label.

The application does not automatically organize or move messages from the user's Inbox.

---

# Application Configuration

Application configuration is located under:

```text
backend/src/main/resources/
```

The primary Spring Boot configuration file is:

```text
application.properties
```

---

## Export Directory

The directory used for generated TXT and DOCX files can be configured with:

```properties
email.export.output-dir=processed_review
```

---

## Default Export Format

The default export format can be configured with:

```properties
email.export.default-format=text
```

The export endpoint can also receive the requested format directly.

Current supported formats are:

```text
text
word
```

---

## ZIP Export Directory

The destination directory used for generated ZIP archives can be configured with:

```properties
email.zip.export-dir=ready_to_send
```

ZIP processing is currently under development.

---

## Sent Archive Directory

The application contains configuration for the directory used after outbound processing:

```properties
email.sent.folder.name=send_archive
```

Outbound email delivery and final archive handling are currently under development.

---

# Build the Application

From the `backend` directory, compile the application:

```bash
mvn clean compile
```

To run the current automated tests:

```bash
mvn test
```

The current test suite provides representative coverage and will continue to be expanded for important workflow components.

---

# Run the Application

Start the Spring Boot application:

```bash
mvn spring-boot:run
```

By default, the application runs locally at:

```text
http://localhost:8080
```

---

# Verify the Application

## Health Check

Verify that the Spring Boot application is running:

```bash
curl http://localhost:8080/api/health
```

---

## Gmail Connectivity

Verify Gmail authentication and connectivity:

```bash
curl http://localhost:8080/api/gmail/status
```

The first Gmail request may trigger the OAuth authorization process.

---

## Gmail Retrieval

Retrieve emails from the configured Gmail processing label:

```bash
curl http://localhost:8080/api/gmail/emails
```

This endpoint can be used to verify Gmail authentication, label lookup, and message retrieval before running the complete export workflow.

---

# Run the Export Workflow

`/api/export` is the primary workflow endpoint.

`ExportController` receives the request and delegates the workflow to `EmailExportService`.

`EmailExportService` coordinates the individual processing services required to complete the end-to-end workflow.

## TXT Export

Run the workflow using TXT output:

```bash
curl "http://localhost:8080/api/export?format=text"
```

## DOCX Export

Run the workflow using DOCX output:

```bash
curl "http://localhost:8080/api/export?format=word"
```

The workflow follows this sequence:

1. Retrieve emails from the configured Gmail label
2. Extract email content
3. Filter and normalize the content
4. Generate TXT or DOCX files
5. Create the ZIP archive *(in development)*
6. Send the ZIP archive by email *(in development)*
7. Return the workflow result

The individual processing services operate as parts of this single workflow rather than as separate user-facing operations.

---

# Generated Files

TXT and DOCX files are written to the configured export directory.

For example:

```text
processed_review/
```

ZIP archives are written to the configured ZIP destination directory.

For example:

```text
ready_to_send/
```

The directories can be changed through `application.properties`.

---

# Sensitive Files

OAuth credentials and authorization tokens contain sensitive account information and must not be committed to Git or included in the repository.

Sensitive information includes:

- OAuth client credentials
- Gmail access tokens
- Gmail refresh tokens
- passwords
- private authentication information

Before committing changes, verify that sensitive files and local authentication data are excluded from source control.

---

# Troubleshooting

## Application Does Not Start

Verify that Java 17 and Maven are available:

```bash
java -version
mvn -version
```

---

## Gmail Authentication Fails

Verify that:

- the Gmail API is enabled
- the OAuth client credentials are valid
- the correct Google account is being used
- the account is authorized for the OAuth application
- the local OAuth authorization token is valid

If the OAuth configuration changes significantly, reauthorization may be required.

---

## Gmail Label Is Not Found

Verify that the configured Gmail label exists and matches the value in `application.properties`.

For example:

```properties
email.gmail.label=for_friend
```

If a different Gmail label is used, update the property accordingly.

---

## No Emails Are Retrieved

Verify that:

- Gmail authentication succeeds
- the configured Gmail label exists
- the label contains messages
- the application has permission to read Gmail messages

Test Gmail connectivity first:

```bash
curl http://localhost:8080/api/gmail/status
```

Then test Gmail retrieval:

```bash
curl http://localhost:8080/api/gmail/emails
```

This helps isolate Gmail configuration problems from the complete export workflow.

---

## Export Files Are Not Created

Verify that:

- the requested format is `text` or `word`
- the export directory is configured correctly
- the application has permission to create files in the configured directory
- Gmail messages were successfully retrieved

---

## ZIP or Email Delivery Does Not Complete

ZIP processing and outbound email delivery are currently under development.

These stages should not yet be treated as completed Version 1 functionality.

---

# Version 1 Completion

Version 1 will be considered functionally complete when the Java application can independently complete the full email automation workflow and provide functionality equivalent to the original Python implementation.

The Java application uses its own Spring Boot architecture and implementation approach while targeting the same core processing outcome.

Additional enhancements beyond this baseline are outside the Version 1 scope.

---

# Related Documentation

- [Project Background](01-project-background.md)
- [Architecture Overview](02-architecture-overview.md)
- [Implementation Notes](03-implementation-notes.md)
- [API Endpoints](04-api-endpoints.md)
- [Current Status](06-current-status.md)
- [Roadmap](07-roadmap.md)