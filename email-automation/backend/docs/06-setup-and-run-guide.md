# Setup and Run Guide

## Overview

This guide explains how to run the Email Automation Spring Boot backend locally.

The backend currently supports:
- Spring Boot startup
- Gmail OAuth2 authentication
- Gmail API connectivity testing
- recruiter email retrieval from Gmail

---

## Prerequisites

Install or confirm the following:

- Java 17
- Maven
- Git

You will also need:
- a Google account
- a Google Cloud project
- Gmail API enabled
- OAuth credentials created for local development

---

## Clone the Repository

```
git clone <repository-url>
cd email-automation/backend
```

---

## Gmail API Setup

### 1. Create OAuth Credentials

In Google Cloud Console:

```
APIs & Services
↓
Credentials
↓
Create Credentials
↓
OAuth Client ID
```

Use OAuth credentials that support local development.

---

### 2. Enable Gmail API

In Google Cloud Console:

```
APIs & Services
↓
Library
↓
Gmail API
↓
Enable
```

---

### 3. Configure Redirect URI

For the local OAuth flow, add this redirect URI:

```
http://localhost:8888/Callback
```

The redirect URI must match exactly.

---

### 4. Add Credentials File

Download the OAuth credentials file from Google Cloud Console.

Rename it to:
```
credentials.json
```

Place it here:

```
backend/src/main/resources/credentials.json
```

**NOTE: Do not commit this file to GitHub.**

---

## Verify `.gitignore`

Make sure these files are ignored:

```
credentials.json
src/main/resources/credentials.json
tokens/
token.json
.env
```

The OAuth token will be created locally after the first successful Gmail authentication.

---

## Build the Project

From the backend directory:

```
mvn clean compile
```

---

## Run the Backend

From the backend directory:

```
mvn spring-boot:run
```

The application should start on:

```
http://localhost:8080
```

---

## Test the Health Endpoint

Open in a browser or use curl:

```
curl http://localhost:8080/api/health
```

Expected response:

```
Email Automation API is running
```

---

## Test Gmail Connectivity

Use:

```
curl http://localhost:8080/api/gmail/status
```

On the first run, the application may print a Google OAuth URL in the terminal.

Open the Google OAuth URL in a browser, sign in, and approve Gmail read-only access.

After approval, the application should create a local `tokens/` folder.

Expected response:

```
Gmail API is working! User email: your-email@example.com
```

---

## Test Gmail Email Retrieval

Use:

```
curl http://localhost:8080/api/gmail/emails
```

This endpoint retrieves recent Gmail messages from the configured Gmail label.

Current label query:

```
label: for_friend
```

---

## Common Issues

### Browser tries to use HTTPS for localhost

If the browser tries to open:

```
https://localhost:8080
```

use:
```
http://localhost:8080
```

The Spring Boot backend currently runs on local HTTP.

Using curl may avoid browser HTTPS-only behavior:

```
curl http://localhost:8080/api/gmail/status
```

---

### Redirect URI mismatch

If Google returns:

```
redirect_uri_mismatch
```

confirm this URI is registered in Google Cloud Console:

```
http://localhost:8888/Callback
```

The value must match exactly.

---

### Address already in use

If the OAuth callback port is already in use, check port `8888`:

```
lsof -i :8888
```

Then stop the process using that port if needed.

---

## Current Development Notes

The backend is currently in active development.

Current completed work:

- Spring Boot backend startup
- Gmail OAuth2 authentication
- Gmail API connectivity
- Gmail email retrieval
- DTO structure

Next development work:
- improve DTO population
- normalize email body content
- apply text filtering
- export cleaned emails locally
