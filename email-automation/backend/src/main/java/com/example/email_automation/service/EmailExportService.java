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

    private static final Logger logger = 
        LoggerFactory.getLogger(EmailExportService.class);

    // Dependencies
    private final GmailService gmailService;
    private final TextFilterService textFilterService;
    private final EmailBodyExtractorService emailBodyExtractorService;
    private final FileExportService fileExportService;

    // Constructor
    public EmailExportService(
            GmailService gmailService,
            TextFilterService textFilterService,
            EmailBodyExtractorService emailBodyExtractorService,
            FileExportService fileExportService ) {

        this.gmailService = gmailService;
        this.textFilterService = textFilterService;
        this.emailBodyExtractorService = emailBodyExtractorService;
        this.fileExportService = fileExportService;
    }

    // Main workflow
    public WorkflowReport exportEmails(String format) {

        // Initialize a workflow report to track the export process
        WorkflowReport workflowReport = new WorkflowReport();
        workflowReport.setStartTime(LocalDateTime.now());
        
        logger.info("Export workflow started. Format={}", format);

        if (format == null) {
            workflowReport.setStatusMessage("Format parameter is required. Use text or word.");
            workflowReport.setEndTime(LocalDateTime.now());
            workflowReport.setDuration(
                    (int) java.time.Duration.between(
                            workflowReport.getStartTime(),
                            workflowReport.getEndTime()
                    ).toSeconds()
            );
            return workflowReport;
        }

        if (!format.equalsIgnoreCase("text")
                && !format.equalsIgnoreCase("word")) {

            workflowReport.setStatusMessage("Invalid format. Use text or word.");
            workflowReport.setEndTime(LocalDateTime.now());
            workflowReport.setDuration(
                    (int) java.time.Duration.between(
                            workflowReport.getStartTime(),
                            workflowReport.getEndTime()
                    ).toSeconds()
            );

            return workflowReport;
        }

        // Reporting - set the format in the workflow report
        workflowReport.setFormat(format);

        // Get recent emails from Gmail
        List<Message> emails;

        logger.info("Retrieving emails from Gmail...");

        try {
            emails = gmailService.getRecentEmails();
        } catch (Exception e) {
            logger.error("Unable to retrieve emails from Gmail.", e);

            workflowReport.setStatusMessage("Unable to retrieve emails from Gmail.");
            workflowReport.setEndTime(LocalDateTime.now());
            workflowReport.setDuration(
                    (int) java.time.Duration.between(
                            workflowReport.getStartTime(),
                            workflowReport.getEndTime()
                    ).toSeconds()
            );

            return workflowReport;
        }

        // Reporting - emails found
        workflowReport.setEmailsFound(emails.size());

        logger.info("Found {} Gmail emails for {} export.", emails.size(), format);
        System.out.println("Found " + emails.size() + " Gmail emails for " + format + " export.");

        // Handle case when there are no emails to export
        if (emails.isEmpty()) {

            workflowReport.setStatusMessage("No emails found to export.");
            workflowReport.setEndTime(LocalDateTime.now());
            workflowReport.setDuration(
                    (int) java.time.Duration.between(
                            workflowReport.getStartTime(),
                            workflowReport.getEndTime()
                    ).toSeconds()
            );

            return workflowReport;
        }

        // Initialize counters for saved and failed files
        int filesSaved = 0;
        int filesFailed = 0;

        logger.info("Beginning processing of {} email(s).", emails.size());

        int emailNumber = 0;

        // Process each email
        for (Message message : emails) {

            emailNumber++;
            logger.info("Processing email {} of {}.", emailNumber, emails.size());

            try {

                EmailMessage email = emailBodyExtractorService.extractEmailMessage(message);
                EmailMessage cleanedEmail = cleanEmailBody(email);
                boolean isSaved = fileExportService.saveFile(cleanedEmail, format);

                if (isSaved) {
                    filesSaved++;
                    logger.info("Email {} of {} saved successfully.", emailNumber, emails.size());
                } else {
                    filesFailed++;
                    logger.warn("Email {} of {} was not saved.", emailNumber, emails.size());
                }

                try {
                    gmailService.moveEmailToLabel(message, isSaved);

                    logger.info(
                            "Gmail label updated for email {} of {}.",
                            emailNumber,
                            emails.size()
                    );

                } catch (Exception e) {
                    logger.error("Error occurred while moving email to label.", e);
                }

            } catch (Exception e) {
                filesFailed++;
                logger.error("Error occurred while processing email.", e);

                try {
                    gmailService.moveEmailToLabel(message, false);
                } catch (Exception labelException) {
                    logger.error("Error occurred while moving failed email to label.", labelException);
                }
            }
        }

        // Reporting
        workflowReport.setFilesSaved(filesSaved);
        workflowReport.setFilesFailed(filesFailed);
        workflowReport.setWorkflowCompleted(filesFailed == 0);

        // Reporting - end time and duration
        workflowReport.setEndTime(LocalDateTime.now());
        workflowReport.setDuration(
            (int) java.time.Duration.between(
                workflowReport.getStartTime(), 
                workflowReport.getEndTime()
            ).toSeconds());

        logger.info(
                "Export workflow finished. Emails found={}, Files saved={}, Files failed={}, Duration={} seconds.",
                workflowReport.getEmailsFound(),
                workflowReport.getFilesSaved(),
                workflowReport.getFilesFailed(),
                workflowReport.getDuration()
        );

        // Reporting - create the summary report to the browser and console
        if (filesFailed == 0) {
            workflowReport.setStatusMessage("Export completed successfully.");
        } else {
            workflowReport.setStatusMessage(
                    "Export completed with " + filesFailed + " file(s) failed."
            );
        }

        return workflowReport;
    }

    // Helper methods
    private String createReportHeader() {
        return "<h1>Export Summary</h1>";
    }

    private String createReportBody(WorkflowReport workflowReport) {
        // Return a summary report of the export operation
        String reportHeader = "Export Summary:\n";
        String exportReport = "Format: " + workflowReport.getFormat() + "\n"
                + "Emails found: " + workflowReport.getEmailsFound() + "\n"
                + "Files saved: " + workflowReport.getFilesSaved() + "\n"
                + "Files failed: " + workflowReport.getFilesFailed() + "\n"
                + "Export completed at: " + workflowReport.getEndTime() + "\n"
                + "Duration: " + workflowReport.getDuration() + " seconds\n";

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
