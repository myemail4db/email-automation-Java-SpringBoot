package com.example.email_automation.dto;

import java.time.LocalDateTime;

public class GmailEmailDto {
    
    private String gmailMessageId;
    private String subject;
    private String sender;
    private LocalDateTime receivedDate;
    private String originalBody;
    private String cleanedBody;
    private String status;

    public GmailEmailDto() {
    }

    public GmailEmailDto(String gmailMessageId, String subject, String sender, 
                      LocalDateTime receivedDate, String originalBody, 
                      String cleanedBody, String status) {
        this.gmailMessageId = gmailMessageId;
        this.subject = subject;
        this.sender = sender;
        this.receivedDate = receivedDate;
        this.originalBody = originalBody;
        this.cleanedBody = cleanedBody;
        this.status = status;
    }

    public void setGmailMessageId(String gmailMessageId) {
        this.gmailMessageId = gmailMessageId;
    }
    public String getGmailMessageId() {
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
}
