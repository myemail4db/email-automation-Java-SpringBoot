package com.example.email_automation.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.email_automation.model.EmailMessage;
import com.google.api.services.gmail.model.Message;

/**
 * This service coordinates the workflow
 */

@Service
public class EmailExportService {

    private static final Logger logger = LoggerFactory.getLogger(EmailExportService.class);

    // Dependencies
    private final GmailService gmailService;
    private final TextFilterService textFilterService;
    private final EmailBodyExtractorService emailBodyExtractorService;
    private final FileExportService fileExportService;
    private final ZipExportService zipExportService;

    // Constructor
    public EmailExportService(
            GmailService gmailService,
            TextFilterService textFilterService,
            EmailBodyExtractorService emailBodyExtractorService,
            FileExportService fileExportService,
            ZipExportService zipExportService) {

        this.gmailService = gmailService;
        this.textFilterService = textFilterService;
        this.emailBodyExtractorService = emailBodyExtractorService;
        this.fileExportService = fileExportService;
        this.zipExportService = zipExportService;
    }

    // Main workflow
    public String exportEmails(String format) {

        if (format == null) {
            return "Format parameter is required. Use text or word.";
        }

        if (format.equalsIgnoreCase("text") || format.equalsIgnoreCase("word")) {

            List<Message> emails = gmailService.getRecentEmails();

            logger.info("Found {} Gmail emails for {} export.", emails.size(), format);

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

            String exportHeader = createExportHeader();
            String exportReport = generateExportReport(format, emails, filesSaved, filesFailed); 
            exportReport = exportReport.replaceAll("\n", "<br>");

            boolean createZipFile = zipExportService.createZipEmails(format);

            return exportHeader + exportReport;
        }

        return "Invalid format. Use text or word.";    
    }

    // Helper methods
    private String createExportHeader() {
        return "<h1>Export Summary</h1>";
    }

    private String generateExportReport(String format, List<Message> emails, int filesSaved, int filesFailed) {
        // Return a summary report of the export operation
        String exportHeader = "Export Summary:\n";
        String exportReport = "Format: " + format + "\n" +
                              "Output Directory: " + fileExportService.getExportDirectory() + "\n" +
                              "Emails found: " + emails.size() + "\n" +
                              "Files saved: " + filesSaved + "\n" +
                              "Files failed: " + filesFailed + "\n" +
                              "Export completed at: " + formatCurrentDateTime(LocalDateTime.now()) + "\n";
                      
        // Print the export summary to the console
        logger.info(exportHeader + exportReport);

        return exportReport;
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

    private String formatCurrentDateTime(LocalDateTime currentDate) {

        // 2. Define the exact format pattern
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a", Locale.US);

        // 3. Format the LocalDateTime object into a String
        String formattedDateTime = currentDate.format(formatter);

        // Log the result (e.g., "06/12/2026 05:12 PM")
        logger.info(formattedDateTime);
        
        return formattedDateTime;
    }
}