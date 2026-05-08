package com.example.email_automation.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class EmailModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int gmailMessageId;
    private String subject;
    private String sender;
    private LocalDateTime receivedDate;
    private String originalBody;
    private String cleanedBody;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public EmailModel() {
    }

    public EmailModel(int id, int gmailMessageId, String subject, String sender, 
                      LocalDateTime receivedDate, String originalBody, 
                      String cleanedBody, String status, LocalDateTime createdAt, 
                      LocalDateTime updatedAt) {
        this.id = id;
        this.gmailMessageId = gmailMessageId;
        this.subject = subject;
        this.sender = sender;
        this.receivedDate = receivedDate;
        this.originalBody = originalBody;
        this.cleanedBody = cleanedBody;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }

    public void setGmailMessageId(int gmailMessageId) {
        this.gmailMessageId = gmailMessageId;
    }
    public int getGmailMessageId() {
        return gmailMessageId;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public String getSubject() {
        return subject;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
    public String getSender() {
        return sender;
    }

    public void setReceivedDate(LocalDateTime receivedDate) {
        this.receivedDate = receivedDate;
    }
    public LocalDateTime getReceivedDate() {
        return receivedDate;
    }

    public void setOriginalBody(String originalBody) {
        this.originalBody = originalBody;
    }
    public String getOriginalBody() {
        return originalBody;
    }

    public void setCleanedBody(String cleanedBody) {
        this.cleanedBody = cleanedBody;
    }
    public String getCleanedBody() {
        return cleanedBody;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getStatus() {
        return status;
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

