package com.example.email_automation.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.google.api.services.gmail.model.Message;

/**
 * This service coordinates the workflow
 */

@Service
public class EmailExportService {

    private final GmailService gmailService;
    private final TextFilterService textFilterService;
    private final EmailBodyExtractorService emailBodyExtractorService;

    public EmailExportService(
            GmailService gmailService,
            TextFilterService textFilterService,
            EmailBodyExtractorService emailBodyExtractorService) {

        this.gmailService = gmailService;
        this.textFilterService = textFilterService;
        this.emailBodyExtractorService = emailBodyExtractorService;
    }

    public String exportEmails(String format) {

        if (format == null) {
            return "Format parameter is required. Use text or word.";
        }

        if (format.equalsIgnoreCase("text")) {

            List<Message> emails = gmailService.getRecentEmails();

            System.out.println("Found " + emails.size() + " Gmail emails for text export");

            return emails.stream()
                    .map(emailBodyExtractorService::extractEmailMessage)
                    .map(email -> {
                        String cleanedBody = textFilterService.clean(email.getBody());

                        return "Subject: " + email.getSubject() + "\n" +
                                "From: " + email.getFrom() + "\n" +
                                "Received: " + email.getReceivedDate() + "\n" +
                                "Body:\n" + cleanedBody;
                    })
                    .collect(Collectors.joining("\n\n==============================\n\n"));
        }

        if (format.equalsIgnoreCase("word")) {
            return "Word export is not implemented yet";
        }

        return "Invalid format. Use text or word.";
    }
}