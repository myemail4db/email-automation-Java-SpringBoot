# Project Background

The original recruiter-email review process required roughly 12 hours of manual work. A Python implementation first validated the automation concept and reduced the effort to about 90 minutes.

The Java 17 / Spring Boot version rebuilds that proven workflow with separated services, REST entry points, workflow reporting, Gmail-ID duplicate prevention, and explicit sent-artifact lifecycle management.

## Current Workflow

1. Gmail messages are retrieved from the configured source label.
2. Export creates TXT or DOCX files in `processed_review/`; filenames retain `_GMAIL-<messageId>`.
3. The workflow stops for manual review. The user reviews, modifies if needed, and saves the files.
4. The user starts Send.
5. Previously sent Gmail IDs are moved to `duplicate/` and excluded from delivery.
6. New files are ZIPped in `ready_to_send/` and sent through Gmail.
7. After confirmed delivery, individual files move to `sent_archive_email/` and the exact ZIP moves to `sent_archive_zip/`.

The core Java workflow is implemented and runtime-validated. The next feature is a CLI that reuses the same service layer.
