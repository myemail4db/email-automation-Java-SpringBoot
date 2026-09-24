# Roadmap

## Overview

The immediate priority is to complete Version 1.

Version 1 is intended to provide functionality equivalent to the original Python email automation program while using an independent Java Spring Boot architecture and implementation approach.

## Version 1 Goal

```text
Gmail Authentication       Completed
        |
Email Retrieval            Completed
        |
Content Extraction         Completed
        |
Text Filtering             Completed
        |
TXT / DOCX Export          Completed
        |
ZIP Processing             In Development
        |
Outbound Email             In Development
        |
Workflow Reporting         Planned
```

Version 1 will be considered functionally complete when the entire workflow can run successfully from beginning to end.

## Remaining Work

### Complete ZIP Processing

Verify generated files, TXT and DOCX archive behavior, ZIP filenames, destination handling, duplicate filename handling, resource cleanup, error handling, and workflow integration.

### Complete Outbound Email Delivery

Reuse the Gmail authentication architecture, create the outbound message, configure the destination, attach the generated archive, send through Gmail, and return the delivery result.

### Complete Workflow Reporting

Accumulate processing results as each major stage completes:

- emails found
- files saved
- files failed
- ZIP created
- email sent
- workflow completed

### Expand Representative Test Coverage

Add tests around Gmail retrieval, filtering, file processing, ZIP processing, outbound delivery, and complete workflow orchestration.

## Future Development

Possible post-Version 1 enhancements include:

- React-based filtering, preview, and user review
- security hardening and secure credential storage
- protected ZIP archives
- secure backup and recovery
- workflow monitoring and history
- additional email provider support

## Development Principles

- preserve clear service responsibilities
- keep the primary business workflow understandable
- add functionality at the appropriate point in the workflow
- keep external integrations separated from internal processing logic
- maintain user control over Gmail messages
- avoid automatically modifying the user's Inbox
- document functionality as completed only after implementation and verification
