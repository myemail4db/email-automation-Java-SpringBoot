package com.example.email_automation.model;

/**
 * Represents an email message with its details.
 */
 
public class EmailMessage {

    private String gmailId;
    private String subject;
    private String from;
    private String body;
    private String receivedDate;

    public EmailMessage() {
        this.gmailId = "";
        this.subject = "";
        this.from = "";
        this.body = "";
        this.receivedDate = "";
    }
    
    public EmailMessage(
            String gmailId,
            String subject, 
            String from, 
            String body, 
            String receivedDate) {
                
        this.gmailId = gmailId;
        this.subject = subject;
        this.from = from;
        this.body = body;
        this.receivedDate = receivedDate;
    }

    public void setGmailId(String gmailId) {
        this.gmailId = gmailId;
    }
    public String getGmailId() {
        return gmailId;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
    public String getSubject() {
        return subject;
    }

    public void setFrom(String from) {
        this.from = from;
    }
    public String getFrom() {
        return from;
    }

    public void setBody(String body) {
        this.body = body;
    }
    public String getBody() {
        return body;
    }

    public void setReceivedDate(String receivedDate) {
        this.receivedDate = receivedDate;
    }
    public String getReceivedDate() {
        return receivedDate;
    }
}
