# API Endpoints

## Overview

This document lists the current API endpoints available in the Email Automation Spring Boot backend.

The endpoints are currently focused on application health checks, Gmail connectivity, and early Gmail email retrieval testing.

---

## Base URL

```
http://localhost:8080
```

---

### Endpoints

---

#### Health Check

- `GET /api/health`
    - Checks whether the Spring Boot backend is running.

**Example Response**

```
Email Automation API is running
```

---

#### Export Test Endpoint

- `GET /api/export?format=text`

    - Tests the early export workflow placeholder.

**Query Parameters**

```
format=text
format=word
```

**Example Response**

```
Completed the export to text format
```

---

#### Gmail Status

- `GET /api/gmail/status`
  - Checks whether the backend can authenticate and connect to Gmail using OAuth2 and the Gmail API.
  - This endpoint validates Gmail connectivity without reading email content.

**Example Response**

```
Gmail API is working! User email: user@example.com
```

---

#### Gmail Emails

- `GET /api/gmail/emails`

  - Retrieves recent Gmail messages from the configured Gmail label.
  - The current version is used to confirm that the backend can retrieve recruiter emails from Gmail and access message details.

**Current Behavior**

```
Retrieves recent messages from the Gmail label used for processing recruiter emails.
```

**Example Label Query**

```
label: for_friend
```

---

### Current Notes

- These endpoints are still part of the early backend development phase.

- The next improvement is to return cleaner response objects using GmailEmailDto instead of exposing raw Gmail API message objects.

---

### Planned Endpoints

- `GET /api/gmail/labels`
  - Planned endpoint for checking available Gmail labels.

- `POST /api/export/text`
  - Planned endpoint for exporting cleaned email content to local text files.

- `POST /api/export/word`
  - Planned endpoint for exporting cleaned email content to Word documents.

- `POST /api/export/zip`
  - Planned endpoint for creating a zip file from processed email exports.

- `POST /api/email/send-batch`
  - Planned endpoint for sending a reviewed batch of exported recruiter emails.





