# Roadmap

## Overview

The core Java Spring Boot email automation workflow is now implemented and verified end to end. Gmail retrieval, export, the manual review checkpoint, duplicate prevention, ZIP creation, outbound delivery, sent-file archiving, sent-ZIP archiving, and workflow reporting are complete.

Development now moves from completing the core workflow to adding additional ways to operate and strengthen the application.

## Core Workflow — Completed

```text
Gmail Authentication             Completed
        |
Email Retrieval                  Completed
        |
Content Extraction               Completed
        |
Text Filtering                   Completed
        |
TXT / DOCX Export                Completed
        |
Manual User Review               Completed
        |
Gmail-ID Duplicate Detection     Completed
        |
ZIP Processing                   Completed
        |
Outbound Email                   Completed
        |
Sent File / ZIP Archiving        Completed
        |
Workflow Reporting               Completed
        |
Runtime Validation               Completed
```

The completed workflow was verified with automated tests and real Gmail runs, including successful delivery, duplicate rejection, and repeated duplicate filename collision handling.

## Next: Command-Line Interface

The next development step is a command-line interface (CLI) that uses the same service layer as the web/API interface.

Planned commands will provide operations such as:

```text
java -jar email-automation.jar export text
java -jar email-automation.jar export word
java -jar email-automation.jar send text
java -jar email-automation.jar send word
```

The CLI will call `EmailExportService` and `EmailSendService` rather than duplicating workflow logic. This keeps the web/API and command-line interfaces as separate entry points into the same application services.

## Later Enhancements

### Security Hardening

- stronger protection for OAuth tokens and credentials
- encrypted ZIP exports
- separate secure delivery of ZIP access credentials when encryption is added
- additional configuration validation and recovery safeguards

### User Review Experience

- possible React-based filtering, preview, and review interface
- clearer workflow progress and result presentation
- preserve the manual review checkpoint before sending

### Monitoring and Diagnostics

- expanded workflow monitoring
- execution history and audit-friendly status information
- improved operational diagnostics and recovery information

### Additional Integrations

- additional email provider support where useful
- preserve provider-specific code behind service boundaries

## Development Principles

- preserve clear service responsibilities
- reuse the same business services across web/API and CLI interfaces
- keep the manual review checkpoint explicit
- prevent previously sent Gmail messages from being resent
- preserve exact sent artifacts for troubleshooting and auditability
- keep external integrations separated from internal processing logic
- maintain user control over Gmail messages
- document functionality as completed only after implementation and verification
