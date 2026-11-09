# Architecture Overview

The application has two explicit entry paths separated by a manual review checkpoint.

```text
ExportController -> EmailExportService
  -> GmailService / GmailAuthService
  -> EmailBodyExtractorService
  -> TextFilterService
  -> FileExportService
  -> processed_review/
  -> USER MANUAL REVIEW

SendController -> EmailSendService
  -> ArchiveService (Gmail-ID duplicate detection)
  -> ZipExportService
  -> GmailService (send)
  -> sent_archive_email/
  -> sent_archive_zip/
```

`ArchiveService` owns individual-file duplicate/archive behavior. `ZipExportService` owns ZIP creation, failed-send cleanup, and sent-ZIP archival. `EmailSendService` coordinates the order and reports partial-success states.
