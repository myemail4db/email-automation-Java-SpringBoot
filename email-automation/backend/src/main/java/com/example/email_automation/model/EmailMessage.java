package com.example.email_automation.model;

/**
 * Represents an email message with its details.
 */
 
public class EmailMessage {

    private final String subject;
    private final String from;
    private final String body;
    private final String receivedDate;

    public EmailMessage(String subject, String from, String body, String receivedDate) {
        this.subject = subject;
        this.from = from;
        this.body = body;
        this.receivedDate = receivedDate;
    }

    public String getSubject() {
        return subject;
    }
    public String getFrom() {
        return from;
    }
    public String getBody() {
        return body;
    }
    public String getReceivedDate() {
        return receivedDate;
    }
}
