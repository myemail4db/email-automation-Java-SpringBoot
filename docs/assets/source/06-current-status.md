# Current Status

## Completed

- Spring Boot backend setup
- Gmail OAuth 2.0 authentication and Gmail API connectivity
- Gmail label-based email retrieval and label movement
- email subject and body extraction
- DTO-based email processing
- recruiter email text filtering and normalization
- TXT export
- DOCX export using Apache POI
- manual review checkpoint between export and send
- configurable export and workflow directories
- ZIP creation from reviewed files
- outbound ZIP delivery through Gmail
- individual sent-file archiving in `sent_archive_email/`
- exact sent-ZIP archiving in `sent_archive_zip/`
- Gmail message ID persisted in exported filenames as `_GMAIL-<messageId>`
- duplicate-send prevention based on Gmail message ID
- duplicate files moved to `duplicate/` instead of being resent
- duplicate-directory filename collision handling with `_2`, `_3`, and later suffixes
- temporary ZIP cleanup when Gmail delivery fails
- workflow reporting for export, send, archive, duplicate, and partial-success outcomes
- JUnit 5 and Mockito tests for the duplicate and ZIP lifecycle safety paths
- end-to-end runtime validation of successful send, duplicate rejection, and repeated duplicate collision handling

## Current Version 1 Workflow

1. Export Gmail messages to TXT or DOCX files in `processed_review/`.
2. The user manually reviews and may modify the exported files.
3. Run the send workflow.
4. Previously sent Gmail message IDs are detected before ZIP creation and moved to `duplicate/`.
5. New reviewed files are packaged into a ZIP in `ready_to_send/` and sent through Gmail.
6. After confirmed delivery, individual files move to `sent_archive_email/` and the exact ZIP moves to `sent_archive_zip/`.

If Gmail delivery fails, the individual source files are not archived and the temporary ZIP is removed so a later attempt can create a fresh package.

## Next Development Step

The next feature is a command-line interface that will call the same `EmailExportService` and `EmailSendService` used by the web/API layer. The goal is to provide command-line operation without duplicating the business logic.

## Planned Later

- additional security hardening and protected credential storage
- encrypted ZIP exports and secure delivery options
- expanded monitoring and diagnostics
- possible React-based review experience
- additional email provider integrations

Functionality is documented as completed only after it has been implemented and verified. The duplicate-prevention and sent-archive lifecycle above were verified with automated tests and real Gmail workflow runs.
