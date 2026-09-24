# Project Background

## Overview

Email Automation began as a practical effort to reduce the amount of manual work required to review recruiter and job opportunity emails.

The original manual workflow required approximately 12 hours of review and processing. A Python implementation was created first to automate the workflow and reduced the processing effort to approximately 90 minutes.

The Java implementation rebuilds that proven workflow using Java 17 and Spring Boot, with responsibilities separated into dedicated services.

## Current Workflow

The application currently processes emails that the user has already placed in a designated Gmail label.

It does not automatically organize or move messages from the user's Inbox.

## Java Application Approach

The Java implementation uses Spring Boot services to separate the responsibilities required by the workflow.

Major responsibilities include:

- Gmail authentication
- Gmail email retrieval
- email content extraction
- text filtering and normalization
- TXT and DOCX file generation
- ZIP archive creation
- outbound email delivery
- workflow coordination

`EmailExportService` acts as the primary workflow orchestrator and coordinates the individual processing services.

## Current Development

The Java application currently supports the workflow through email retrieval, content processing, and TXT/DOCX file generation.

ZIP processing is currently being completed.

Outbound email delivery will complete the next stage of the end-to-end workflow.

After ZIP creation and outbound email delivery are working together, workflow reporting will be expanded so that the result of each major processing stage can be collected as the workflow runs and presented when processing is complete.

## Future Development

After the core Java workflow is completed, possible future enhancements include:

- React-based text filtering, preview, and user review
- security hardening and secure credential storage
- protected ZIP archives
- secure backup and recovery
- improved workflow reporting and monitoring
- additional email provider support
