# Customize the Application

## Gmail labels
- `email.gmail.source-label`
- `email.gmail.success-label`
- `email.gmail.fail-label`

## Local directories
- `email.files.processed-dir=processed_review`
- `email.files.zip-dir=ready_to_send`
- `email.files.archive-dir=sent_archive_email`
- `email.files.zip-archive-dir=sent_archive_zip`
- `email.files.duplicate-dir=duplicate`
- `email.files.zip-prefix=job_batch`

## Export
- `email.export.default-format=text`

## Private configuration
- `email.gmail.recipient` controls the outbound destination and should remain private.

Supported workflow formats are `text` and `word`. Keep credentials, tokens, private properties, logs, generated files, duplicates, and sent archives out of source control.
