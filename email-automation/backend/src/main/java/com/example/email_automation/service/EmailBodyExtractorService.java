package com.example.email_automation.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.email_automation.model.EmailMessage;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartHeader;

@Service
public class EmailBodyExtractorService {

    public EmailMessage extractEmailMessage(Message message) {
        if (message == null) {
            return new EmailMessage("", "", "", "");
        }

        String subject = extractEmailSubject(message);
        String from = extractEmailFrom(message);
        String body = extractEmailBody(message);
        String receivedDate = extractEmailReceivedDate(message);

        return new EmailMessage(
                subject,
                from,
                body,
                receivedDate
        );
    }

    public String extractEmailBody(Message message) {
        if (message == null || message.getPayload() == null) {
            return "";
        }

        // Recursively search for the body data in the message parts
        try {
            String rawMessage = findBodyData(message.getPayload());

            if (rawMessage == null || rawMessage.isBlank()) {
                return "";
            }

            // Decode the Base64-encoded email body
            byte[] decodedBytes = Base64.getUrlDecoder().decode(rawMessage);

            // Convert the decoded bytes to a UTF-8 string
            return new String(decodedBytes, StandardCharsets.UTF_8);

        } catch (Exception e) {
            System.out.println("Error extracting email body: " + e.getMessage());
            return "";
        }
    }

    private String findBodyData(MessagePart part) {
        if (part == null) {
            return null;
        }

        // Check if this part has the body data
        if (part.getBody() != null && part.getBody().getData() != null) {

            // Return the body data if found
            return part.getBody().getData();
        }

        // If not, recursively check the child parts
        if (part.getParts() != null) {

            // Recursively search for body data in child parts
            for (MessagePart childPart : part.getParts()) {

                // Recursively search for body data in child parts
                String data = findBodyData(childPart);

                if (data != null) {
                    return data;
                }
            }
        }

        return null;
    }

    public String extractEmailSubject(Message message) {
        if (message == null || message.getPayload() == null || message.getPayload().getHeaders() == null) {
            return "";
        }

        String subject = "";

        // Search for the "Subject" header in the message headers
        List<MessagePartHeader> headers = message.getPayload().getHeaders();

        // Search for the "Subject" header in the message headers
        for (MessagePartHeader header : headers) {
            // Check if the header name is "Subject" (case-insensitive)
            if ("Subject".equalsIgnoreCase(header.getName())) {
                // If found, return the header value as the email subject
                subject = header.getValue();
                break;
            }
        }

        return subject;
    }

    public String extractEmailFrom(Message message) {
        if (message == null || message.getPayload() == null || message.getPayload().getHeaders() == null) {
            return "";
        }
        
        String from = "";

        // Search for the "From" header in the message headers
        List<MessagePartHeader> headers = message.getPayload().getHeaders();

        // Search for the "From" header in the message headers
        for (MessagePartHeader header : headers) {
            // Check if the header name is "From" (case-insensitive)
            if ("from".equalsIgnoreCase(header.getName())) {
                // If found, return the header value as the email sender
                from = header.getValue();
                break;
            }
        }
        return from;
    }

    public String extractEmailReceivedDate(Message message) {
        if (message == null || message.getPayload() == null || message.getPayload().getHeaders() == null) {
            return "";
        }

        String receivedDate = "";

        // Search for the "Date" header in the message headers
        List<MessagePartHeader> headers = message.getPayload().getHeaders();

        // Search for the "Date" header in the message headers
        for (MessagePartHeader header : headers) {
            // Check if the header name is "Date" (case-insensitive)
            if ("date".equalsIgnoreCase(header.getName())) {
                // If found, return the header value as the email received date
                receivedDate = header.getValue();
                break;
            }
        }
        return receivedDate;
    }

    public String findBodyDataByMimeType(MessagePart part, String mimeType) {

        if (part == null || mimeType == null) {
            return null;
        }

        // Check if this part has the specified MIME type and contains body data
        if (mimeType.equals(part.getMimeType()) && part.getBody() != null && part.getBody().getData() != null) {

            // Return the body data if found
            return part.getBody().getData();
        
        } else {

            // Recursively search for body data in child parts
            if (part.getParts() == null) {
                return null;
            }

            // If not, recursively check the child parts
            for (MessagePart childPart : part.getParts()) {

                // Recursively search for body data in child parts
                String data = findBodyDataByMimeType(childPart, mimeType);
                if (data != null) {
                    return data;
                }   
            }
        }

        return null;
    }

}