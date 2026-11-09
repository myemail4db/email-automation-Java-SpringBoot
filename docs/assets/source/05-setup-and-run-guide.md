# Setup and Run Guide

1. Configure Gmail OAuth and application properties.
2. Run `mvn clean test`.
3. Start with `mvn spring-boot:run`.
4. Verify `http://localhost:8080/api/health` and `http://localhost:8080/api/gmail/status`.
5. Export with `/api/export`, `/api/export?format=text`, or `/api/export?format=word`. The optional format defaults to `text`.
6. Manually review/modify the files in `processed_review/`.
7. Send with `/api/send`, `/api/send?format=text`, or `/api/send?format=word`. The optional format defaults to `text`.
8. Verify individual files in `sent_archive_email/` and the exact ZIP in `sent_archive_zip/`. Duplicates should move to `duplicate/` and should not be resent.
