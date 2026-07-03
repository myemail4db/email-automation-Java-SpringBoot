package com.example.email_automation.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

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
    private final FileExportService fileExportService;

    public EmailExportService(
            GmailService gmailService,
            TextFilterService textFilterService,
            EmailBodyExtractorService emailBodyExtractorService,
            FileExportService fileExportService) {

        this.gmailService = gmailService;
        this.textFilterService = textFilterService;
        this.emailBodyExtractorService = emailBodyExtractorService;
        this.fileExportService = fileExportService;
    }

    public String exportEmails(String format) {

        if (format == null) {
            return "Format parameter is required. Use text or word.";
        }

        if (format.equalsIgnoreCase("text") || format.equalsIgnoreCase("word")) {

            List<Message> emails = gmailService.getRecentEmails();

            System.out.println("Found " + emails.size() + " Gmail emails for text export");

            // Handle case when there are no emails to export
            if (emails.isEmpty() || emails.get(0) == null) {
                return "No emails found to export.";
            }

            int filesSaved = 0;
            int filesFailed = 0;
            for (int i = 0; i < emails.size(); i++) {
                EmailMessage email = emailBodyExtractorService.extractEmailMessage(emails.get(i));
                email = cleanEmailBody(email);
                boolean isSaved = fileExportService.saveFile(email, format);

                if (isSaved) {
                    filesSaved++;
                } else {
                    filesFailed++;
                }
            }

            // Return a summary report of the export operation
            String exportHeader = "Export Summary:\n";
            String exportReport = "Format: " + format + "\n" +
                                  "Output Directory: " + fileExportService.getExportDirectory() + "\n" +
                                  "Emails found: " + emails.size() + "\n" +
                                  "Files saved: " + filesSaved + "\n" +
                                  "Files failed: " + filesFailed + "\n" +
                                  "Export completed at: " + normalizeCurrentDate(LocalDateTime.now()) + "\n";

                                  
            System.out.print(exportHeader + exportReport);
 
            exportHeader = "<h1>Export Summary</h1>";
            exportReport = exportReport.replaceAll("\n", "<br>");

            return exportHeader + exportReport;
        }

        return "Invalid format. Use text or word.";    
    }

    private EmailMessage cleanEmailBody(EmailMessage email) {
        String cleanedBody = textFilterService.clean(email.getBody());

        return new EmailMessage(
                email.getSubject(),
                email.getFrom(),
                cleanedBody,
                email.getReceivedDate()
        );
    }

    private String normalizeCurrentDate(LocalDateTime currentDate) {

        // 2. Define the exact format pattern
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a", Locale.US);

        // 3. Format the LocalDateTime object into a String
        String formattedDateTime = currentDate.format(formatter);

        // Print the result (e.g., "06/12/2026 05:12 PM")
        System.out.println(formattedDateTime);
        
        return formattedDateTime;
    }
}