package com.example.email_automation.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.email_automation.model.EmailMessage;
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

        FileExportService fileExportService = new FileExportService();

        if (format.equalsIgnoreCase("text")) {

            List<Message> emails = gmailService.getRecentEmails();

            System.out.println("Found " + emails.size() + " Gmail emails for text export");

            // Handle case when there are no emails to export
            if (emails.isEmpty() || emails.get(0) == null) {
                return "No emails found to export.";
            }

            String exportedContent = emails.stream()
                .map(emailBodyExtractorService::extractEmailMessage)       // Extract email message details
                .map(this::cleanEmailBody)                                 // Clean the email body using TextFilterService
                .map(email -> fileExportService.saveFile(email, format))
                .collect(Collectors.joining("\n\n==============================\n\n"));

            // Return a summary report of the export operation
            return "Email has been exported in " + format + " format. Exported content:\n\n" + exportedContent;
        }

        if (format.equalsIgnoreCase("word")) {
            return "Word export is not implemented yet";
        }

        return "Invalid format. Use text or word.";    }

    private EmailMessage cleanEmailBody(EmailMessage email) {
        String cleanedBody = textFilterService.clean(email.getBody());

        return new EmailMessage(
                email.getSubject(),
                email.getFrom(),
                cleanedBody,
                email.getReceivedDate()
        );
    }

}