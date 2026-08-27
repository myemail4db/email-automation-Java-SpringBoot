package com.example.email_automation.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;

@Entity
public class WorkflowReport {

    private String format;
    private int emailsFound;
    private int filesSaved;
    private int filesFailed;
    private boolean isZipCreated;
    private boolean isEmailSent;
    private boolean isWorkflowCompleted;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int duration;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public WorkflowReport() {
        this.format = "";
        this.emailsFound = 0;
        this.filesSaved = 0;
        this.filesFailed = 0;
        this.isZipCreated = false;
        this.isEmailSent = false;
        this.isWorkflowCompleted = false;
        this.startTime = LocalDateTime.now();
        this.endTime = null;
        this.duration = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void setFormat(String format) {
        this.format = format;
    }
    public String getFormat() {
        return format;
    }

    public void setEmailsFound(int emailsFound) {
        this.emailsFound = emailsFound;
    }
    public int getEmailsFound() {
        return emailsFound;
    }

    public void setFilesSaved(int filesSaved) {
        this.filesSaved = filesSaved;
    }
    public int getFilesSaved() {
        return filesSaved;
    }

    public void setFilesFailed(int filesFailed) {
        this.filesFailed = filesFailed;
    }
    public int getFilesFailed() {
        return filesFailed;
    }

    public void setZipCreated(boolean isZipCreated) {
        this.isZipCreated = isZipCreated;
    }
    public boolean isZipCreated() {
        return isZipCreated;
    }

    public void setEmailSent(boolean isEmailSent) {
        this.isEmailSent = isEmailSent;
    }
    public boolean isEmailSent() {
        return isEmailSent;
    }

    public void setWorkflowCompleted(boolean isWorkflowCompleted) {
        this.isWorkflowCompleted = isWorkflowCompleted;
    }
    public boolean isWorkflowCompleted() {
        return isWorkflowCompleted;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
    public int getDuration() {
        return duration;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

}
