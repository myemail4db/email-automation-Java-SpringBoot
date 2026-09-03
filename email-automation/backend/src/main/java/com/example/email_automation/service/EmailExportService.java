package com.example.email_automation.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.email_automation.model.EmailMessage;
import com.example.email_automation.model.WorkflowReport;
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

        // Initialize a workflow report to track the export process
        WorkflowReport workflowReport = new WorkflowReport();
        workflowReport.setStartTime(LocalDateTime.now());

        if (format == null) {
            return "Format parameter is required. Use text or word.";
        }

        if (format.equalsIgnoreCase("text") || format.equalsIgnoreCase("word")) {

            // Reporting - set the format in the workflow report
            workflowReport.setFormat(format);

            // Get recent emails from Gmail
            List<Message> emails = gmailService.getRecentEmails();

            // Reporting - emails found
            workflowReport.setEmailsFound(emails.size());

            logger.info("Found {} Gmail emails for {} export.", emails.size(), format);
            System.out.println("Found " + emails.size() + " Gmail emails for " + format + " export.");

            // Handle case when there are no emails to export
            if (emails.isEmpty() || emails.get(0) == null) {
                return "No emails found to export.";
            }

            // Initialize counters for saved and failed files
            int filesSaved = 0;
            int filesFailed = 0;

            // Process each email
            for (int i = 0; i < emails.size(); i++) {
                
                try {

                    EmailMessage email = emailBodyExtractorService.extractEmailMessage(emails.get(i));
                    email = cleanEmailBody(email);
                    boolean isSaved = fileExportService.saveFile(email, format);                

                    if (isSaved) {

                        // Reporting
                        filesSaved++;

                        try {
                            gmailService.moveEmailToLabel(emails.get(i), isSaved);

                        } catch (Exception e) {
                            logger.error("Error occurred while moving email to label.", e);
                        }

                    } else {

                        // Reporting
                        filesFailed++;

                        gmailService.moveEmailToLabel(emails.get(i), isSaved);
                    }

                } catch (Exception e) {
                    logger.error("Error occurred: " + e.getMessage(), e);
                    throw new RuntimeException("Error occurred: " + e.getMessage(), e);
                }
            }

            // Reporting
            workflowReport.setFilesSaved(filesSaved);
            workflowReport.setFilesFailed(filesFailed);

            boolean isZipFileCreated = zipExportService.createZipEmail(format);

            // Reporting
            workflowReport.setZipCreated(isZipFileCreated);
            workflowReport.setWorkflowCompleted(isZipFileCreated);

            // Reporting - end time and duration
            workflowReport.setEndTime(LocalDateTime.now());
            workflowReport.setDuration((int) java.time.Duration.between(workflowReport.getStartTime(), workflowReport.getEndTime()).toSeconds());            

            // Reporting - create the summary report to the browser and console
            String reportHeader = createReportHeader();
            String reportBody = createReportBody(workflowReport); 
            reportBody = reportBody.replaceAll("\n", "<br>");

            return reportHeader + reportBody;
        }

        return "Invalid format. Use text or word.";    
    }

    // Helper methods
    private String createReportHeader() {
        return "<h1>Export Summary</h1>";
    }

    private String createReportBody(WorkflowReport workflowReport) {
        // Return a summary report of the export operation
        String reportHeader = "Export Summary:\n";
        String exportReport = "Format: " + workflowReport.getFormat() + "\n" +
                              "Emails found: " + workflowReport.getEmailsFound() + "\n" +
                              "Files saved: " + workflowReport.getFilesSaved() + "\n" +
                              "Files failed: " + workflowReport.getFilesFailed() + "\n" +
                              "Zip file created: " + workflowReport.isZipCreated() + "\n" +
                              "Export completed at: " + workflowReport.getEndTime() + "\n" +
                              "Duration: " + workflowReport.getDuration() + " seconds\n";

        // Print the export summary to the console
        logger.info(reportHeader + exportReport);

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

}