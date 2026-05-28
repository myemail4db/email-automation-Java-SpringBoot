# Email Automation Project Timeline <!-- omit from toc -->

## Table of Contents <!-- omit from toc -->
- [Overview](#overview)
- [2025](#2025)
  - [Python Email Automation Prototype Started](#python-email-automation-prototype-started)
- [2025 – Early 2026](#2025--early-2026)
  - [Python Workflow Expansion](#python-workflow-expansion)
- [2026-04-12](#2026-04-12)
  - [Initial Java Spring Boot Migration Planning](#initial-java-spring-boot-migration-planning)
- [2026-04-29](#2026-04-29)
  - [Spring Boot Email Automation Architecture Planning](#spring-boot-email-automation-architecture-planning)
- [2026-05-06](#2026-05-06)
  - [Spring Boot Backend Implementation Started](#spring-boot-backend-implementation-started)
- [2026-05-07](#2026-05-07)
  - [Core Backend Foundation Completed](#core-backend-foundation-completed)
- [2026-05-08](#2026-05-08)
  - [Gmail OAuth2 Integration Completed](#gmail-oauth2-integration-completed)
- [2026-05-08](#2026-05-08-1)
  - [Gmail API Email Retrieval Completed](#gmail-api-email-retrieval-completed)
- [2026-05-08](#2026-05-08-2)
  - [DTO Architecture Refactor Completed](#dto-architecture-refactor-completed)
- [Current Phase](#current-phase)
  - [Email Processing and Normalization](#email-processing-and-normalization)
- [Next Planned Milestones](#next-planned-milestones)


## Overview

The original version of the email automation workflow was first developed in Python to automate the manual process of organizing recruiter and job application emails.

The Python version focused on Gmail API integration, text cleanup, document export, and batch email workflows.

After validating the workflow and seeing the practical value of the automation process, the project was later redesigned and rebuilt using Java Spring Boot to create a more structured backend architecture and support future expansion into workflow automation, analytics, and frontend integration.

---

## 2025
### Python Email Automation Prototype Started

Started building the original Python-based workflow to reduce the manual effort involved in organizing recruiter and job application emails.

The initial workflow focused on:
- retrieving emails from Gmail
- cleaning recruiter email text
- exporting processed content locally
- supporting batch processing workflows

Result:
Established the initial automation workflow and validated the overall processing approach.

---

## 2025 – Early 2026
### Python Workflow Expansion

Expanded the Python workflow to support larger email batches and more consistent recruiter email processing.

Completed:
- Gmail label workflow support
- text filtering improvements
- document export improvements
- zip batch processing
- logging improvements
- duplicate filename handling

Result:
Created a stable workflow capable of processing recruiter emails more efficiently than the original manual process.

---

## 2026-04-12
### Initial Java Spring Boot Migration Planning

Started planning the migration of the Python workflow into a Java Spring Boot application.

The goal was to redesign the workflow using a cleaner backend architecture that would support future expansion into:
- workflow automation
- analytics
- frontend integration

Result:
Defined the early architecture direction for the Java Spring Boot version of the project.

---

## 2026-04-29
### Spring Boot Email Automation Architecture Planning

Expanded the Java Spring Boot project plan into a phased backend workflow system.

Defined:
- Spring Boot backend structure
- Gmail processing workflow
- export pipeline direction
- analytics and recruiter tracking goals
- future React frontend integration

Result:
Transitioned the project from planning into a structured implementation phase.

---

## 2026-05-06
### Spring Boot Backend Implementation Started

Created the Spring Boot backend structure and configured the initial Maven project layout.

Completed:
- backend project setup
- package structure
- Maven configuration
- application startup validation

Result:
Established the backend foundation for the Java migration project.

---

## 2026-05-07
### Core Backend Foundation Completed

Implemented the first working backend endpoints and service layer structure.

Completed:
- `/api/health`
- `/api/export`
- initial backend services
- backend package organization

Services created:
- TextFilterService
- TextExportService
- EmailExportService
- BatchSendService
- EmailSendService

Result:
Established the first working backend processing structure for the project.

---

## 2026-05-08
### Gmail OAuth2 Integration Completed

Connected the Spring Boot backend to Gmail using OAuth2 and the Gmail API.

Completed:
- Google Cloud OAuth setup
- Gmail API dependency integration
- OAuth browser authentication flow
- token persistence
- Gmail API connectivity validation

Challenges solved:
- Maven dependency conflicts
- redirect URI mismatch issues
- localhost HTTPS browser behavior
- Gmail callback port conflicts
- Gmail credentials resource loading

Result:
Successfully authenticated the Spring Boot application against live Gmail APIs.

---

## 2026-05-08
### Gmail API Email Retrieval Completed

Implemented Gmail label retrieval and recruiter email retrieval using the Gmail API.

Completed:
- Gmail label queries
- recruiter email retrieval by label
- subject extraction
- body extraction
- Gmail message retrieval

Result:
Successfully retrieved and processed recruiter email content through the Spring Boot backend.

---

## 2026-05-08
### DTO Architecture Refactor Completed

Introduced DTO separation to isolate Gmail API models from internal application models.

Initially returned raw Gmail Message objects directly from the API layer, but later separated the Gmail API models from internal application DTOs to improve service-layer organization and prepare for future export and persistence workflows.

Completed:
- created `GmailEmailDto`
- separated DTOs from database entities
- reduced Gmail API model coupling
- improved backend layering

Result:
Established a cleaner backend architecture for future workflow expansion.

---

## Current Phase
### Email Processing and Normalization

Current focus:
- email body normalization
- recruiter email text filtering
- DTO population improvements
- export pipeline preparation

---

## Next Planned Milestones

Planned next steps:
- implement text filtering pipeline
- export cleaned emails to `.txt`
- export emails to `.docx`
- zip batch export files
- implement outbound email sending
- support Gmail label processing workflows
- add database persistence
- add analytics and reporting
- integrate React frontend