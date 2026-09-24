# Customize the Application

## Overview

Email Automation uses Spring Boot configuration so the application can be adapted to a user's Gmail account and local workflow without changing Java source code.

Configuration is divided between normal application behavior and private, user-specific values.

## application.properties

Use `application.properties` for normal workflow settings that do not contain private account information.

### Gmail Processing Label

```properties
email.gmail.label=for_friend
```

Controls which Gmail label the application reads.

Change `for_friend` to the Gmail label you want the application to process. The label must exist in the Gmail account and contain the messages you want processed.

### Export Directory

```properties
email.export.output-dir=processed_review
```

Controls where generated TXT and DOCX files are written.

The value can be changed to another local directory name or path that the application can write to.

### Default Export Format

```properties
email.export.default-format=text
```

Controls the default export format.

Supported formats documented by the current application are:

- `text`
- `word`

The `/api/export` endpoint can also receive the requested format directly.

### ZIP Export Directory

```properties
email.zip.export-dir=ready_to_send
```

Controls where generated ZIP archives are written.

ZIP processing is currently in development.

### Sent Archive Directory

```properties
email.sent.folder.name=send_archive
```

Controls the directory intended for archive handling after outbound processing.

Outbound email delivery and final archive handling are currently in development.

## application-private.properties

Use `application-private.properties` for values specific to the user's email accounts.

```properties
email.gmail.source-account=your-email@example.com
email.send.recipient=destination@example.com
```

### Source Account

`email.gmail.source-account` identifies the Gmail account used by the workflow.

Replace the example value with the Gmail account authorized for this installation.

### Send Recipient

`email.send.recipient` identifies the address intended to receive the completed outbound archive.

Replace the example value with the destination address for your workflow.

Outbound email delivery is currently in development.

## Keep Private Configuration Private

`application-private.properties`, OAuth client credentials, Gmail authorization tokens, access tokens, refresh tokens, passwords, and other private authentication information must not be committed to Git.

## Common Customizations

### Process a Different Gmail Label

Change:

```properties
email.gmail.label=for_friend
```

to the Gmail label you want processed.

### Store Generated Files Somewhere Else

Change:

```properties
email.export.output-dir=processed_review
```

to another writable directory or path.

### Use DOCX as the Default

Change:

```properties
email.export.default-format=text
```

to:

```properties
email.export.default-format=word
```

### Change the ZIP Destination

Change:

```properties
email.zip.export-dir=ready_to_send
```

to another writable directory or path.

### Change the Outbound Recipient

In `application-private.properties`, change:

```properties
email.send.recipient=destination@example.com
```

to the email address that should receive the completed archive.

## Before Running

Confirm that:

1. The configured Gmail label exists.
2. The source Gmail account is the account authorized through OAuth.
3. Local directories are writable.
4. The export format is `text` or `word`.
5. Private configuration and OAuth files are excluded from source control.
6. ZIP and outbound email stages are understood to still be in development.
