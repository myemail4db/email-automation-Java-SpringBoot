# Architecture Overview <!-- omit from toc -->

# Table of Contents <!-- omit from toc -->
- [Overview](#overview)
- [Current Architecture Flow](#current-architecture-flow)
- [Backend Structure](#backend-structure)
- [Controller Layer](#controller-layer)

---

## Overview

The Email Automation project is a Java Spring Boot backend application designed to process recruiter and job application emails retrieved from Gmail using OAuth2 and the Gmail API.

The original Python version validated the workflow and automation process. The Java Spring Boot version was later created to redesign the system using a more structured backend architecture that supports future workflow expansion, analytics, and frontend integration.

The current backend architecture focuses on:
- Gmail API integration
- email processing workflows
- service-layer separation
- DTO-based data handling
- export pipeline preparation

---

## Current Architecture Flow

```
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

## Backend Structure

The backend application is organized using a layered Spring Boot architecture.

Current structure:

```
backend/
├── controller/
├── service/
├── dto/
├── model/
├── repository/
└── resources/
```

The goal of the structure is to separate responsibilities across the application and reduce coupling between external APIs, internal processing, and future persistence layers.

---

## Controller Layer

The controller layer exposes REST API endpoints used to interact with the backend workflow.
Current responsibilities:

health validation
Gmail connectivity testing
Gmail email retrieval
workflow testing endpoints
Example endpoints:
/api/health
/api/export
/api/gmail/status
/api/gmail/emails
The controller layer delegates processing to the service layer instead of implementing business logic directly inside endpoints.
Service Layer
The service layer contains the core workflow and processing logic for the application.
Current services include:

GmailAuthService
GmailService
TextFilterService
TextExportService
EmailExportService
BatchSendService
EmailSendService
Each service was separated by responsibility to simplify future maintenance and expansion.
Gmail Authentication Architecture
The Gmail authentication workflow uses OAuth2 and the Gmail API.
The authentication logic was isolated into GmailAuthService to separate OAuth handling from Gmail processing operations.

Responsibilities include:

loading OAuth credentials
managing token persistence
authenticating Gmail access
building authenticated Gmail API clients
The Gmail API integration currently supports:
Gmail connectivity validation
Gmail label retrieval
recruiter email retrieval
Gmail message access
Gmail Processing Layer
GmailService handles Gmail API operations after authentication is completed.
Current responsibilities:

Gmail connectivity validation
Gmail label retrieval
recruiter email retrieval
Gmail message processing
subject extraction
body extraction
The service retrieves recruiter emails using Gmail label queries and prepares the data for normalization and export workflows.
DTO Architecture
The project originally returned raw Gmail Message objects directly from the API layer.
As the workflow expanded, DTO separation was introduced to isolate Gmail API models from internal application models and future persistence layers.

Current DTO:

GmailEmailDto
The DTO structure currently stores:
Gmail message ID
subject
sender
received date
original body
cleaned body
workflow status
This separation simplifies future filtering, export, and persistence workflows.
Text Processing Workflow
The text processing workflow is currently being expanded to normalize recruiter email content before export.
Current focus areas:

recruiter email cleanup
reply-chain removal
disclaimer removal
body normalization
export preparation
The filtering workflow is handled through TextFilterService.
Export Workflow
The export workflow is designed to support local review and batch processing of recruiter emails.
Planned export support includes:

.txt export
.docx export
zip batch packaging
The export pipeline will support manual review workflows before outbound processing and archival.
Future Architecture Expansion
Planned future expansion areas include:
database persistence
recruiter/job tracking
workflow status management
analytics and reporting
outbound email workflows
React frontend integration
The current backend structure was designed to support future expansion without requiring major architectural redesign.





