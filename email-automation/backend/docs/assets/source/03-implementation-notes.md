# Implementation Notes

## Manual review checkpoint
Export and Send are intentionally separate. Export writes TXT/DOCX files to `processed_review/` and stops. The user reviews and may modify the files before explicitly starting Send.

## Gmail-ID duplicate identity
Generated-file hashes were not reliable business identity because repeated exports of the same Gmail message can produce different bytes. The Java workflow therefore persists the Gmail message ID in filenames as `_GMAIL-<messageId>` and checks that ID against `sent_archive_email/`.

Already-sent messages move to `duplicate/` before ZIP creation. Duplicate-directory filename collisions are preserved with `_2`, `_3`, and later suffixes.

## ZIP lifecycle
New reviewed files are packaged in `ready_to_send/`. If Gmail send fails, the temporary ZIP is deleted and source files remain available. After confirmed delivery, individual files move to `sent_archive_email/` and the exact ZIP moves to `sent_archive_zip/`.

## Workflow reporting
`WorkflowReport` records export/send outcomes, counts, ZIP creation, email delivery, archive results, completion state, timing, and status messages. Partial-success states are reported explicitly.

## Testing
JUnit 5 and Mockito tests cover Gmail-ID duplicate detection, duplicate filename collisions, ZIP archival, failed-send cleanup, and partial-success behavior. The lifecycle was also verified with real Gmail runs.
