# Roadmap

## Overview

The current Java Spring Boot iteration focuses on rebuilding the recruiter email workflow using a cleaner backend architecture and preparing the system for future workflow automation and analytics expansion.

The roadmap below outlines the planned next phases of the project.

---

# Phase 1
## Email Processing and Export Workflow

Current focus:
- recruiter email normalization
- text filtering improvements
- DTO population cleanup
- export pipeline preparation

Planned additions:
- remove recruiter email disclaimers
- remove reply-chain content
- normalize spacing and formatting
- improve recruiter email readability

Export support:
- `.txt` export
- `.docx` export
- zip batch packaging

Goal:
Create a cleaner local review workflow for recruiter email processing.

---

# Phase 2
## Gmail Workflow Automation

Planned additions:
- Gmail label processing
- processed email tracking
- automatic workflow labeling
- outbound email support
- reviewed batch workflows

Workflow goals:
- retrieve recruiter emails
- process and clean content
- export locally
- review processed files
- package reviewed batches
- support outbound processing workflows

Goal:
Reduce additional manual processing steps and improve workflow consistency.

---

# Phase 3
## Database Persistence and Tracking

Planned additions:
- database persistence
- recruiter tracking
- job tracking
- workflow status tracking
- recruiter activity history

Possible future data tracking:
- recruiter name
- company
- job title
- workflow status
- processing timestamps

Goal:
Support long-term recruiter and workflow management.

---

# Phase 4
## Analytics and Reporting

Planned additions:
- recruiter activity reporting
- workflow processing statistics
- export tracking
- recruiter volume trends
- dashboard reporting

Possible future analytics:
- recruiter frequency
- application tracking
- workflow completion statistics
- processing history

Goal:
Provide visibility into recruiter activity and workflow processing trends.

---

# Phase 5
## Frontend Integration

Planned additions:
- React frontend integration
- workflow management dashboard
- recruiter tracking views
- export management screens
- analytics dashboards

Frontend goals:
- manage recruiter workflows
- review processed exports
- monitor workflow status
- view recruiter tracking information

Goal:
Create a more complete workflow management interface for the automation platform.

---

# Long-Term Direction

Long-term goals for the project include:
- improving workflow automation
- reducing repetitive manual processing
- supporting larger recruiter email workflows
- expanding reporting and tracking capabilities
- continuing backend architecture improvements

The current Java Spring Boot version is designed to support future expansion without requiring major architectural redesign.