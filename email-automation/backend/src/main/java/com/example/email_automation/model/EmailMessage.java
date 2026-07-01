package com.example.email_automation.model;

/**
 * Represents an email message with its details.
 */
 
public class EmailMessage {

    private String subject;
    private String from;
    private String body;
    private String receivedDate;

    public EmailMessage() {
        this.subject = "";
        this.from = "";
        this.body = "";
        this.receivedDate = "";
    }
    
    public EmailMessage(String subject, String from, String body, String receivedDate) {
        this.subject = subject;
        this.from = from;
        this.body = body;
        this.receivedDate = receivedDate;
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
