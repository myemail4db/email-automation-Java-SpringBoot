package com.example.email_automation.service;

import java.nio.file.Path;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.email_automation.model.WorkflowReport;

@Service
public class EmailSendService {

    private static final Logger logger = 
        LoggerFactory.getLogger(EmailSendService.class);

    private final GmailService gmailService;
    private final ZipExportService zipExportService;
    private final ArchiveService archiveService;

    public EmailSendService(
            ZipExportService zipExportService,
            GmailService gmailService,
            ArchiveService archiveService) {

        this.zipExportService = zipExportService;
        this.gmailService = gmailService;
        this.archiveService = archiveService;
    }

    public Path createZipForSend(String format) {

        Path zipFile = zipExportService.createZipEmail(format);

        if (zipFile == null) {
            return null;
        }

        return zipFile;
    }

    public WorkflowReport processSend(String format) {

        WorkflowReport result = new WorkflowReport();
        result.setFormat(format);
        result.setStartTime(LocalDateTime.now());

        int duplicatesArchived = archiveService.archiveDuplicateFiles(format);

        if (duplicatesArchived < 0) {
            logger.error("Send workflow stopped: duplicate file check failed.");

            result.setStatusMessage("Duplicate file check failed. No email was sent.");
            completeWorkflowReport(result);

            return result;
        }

        result.setDuplicateFilesArchived(duplicatesArchived);

        Path zipFile = createZipForSend(format);

        if (zipFile == null) {

            if (duplicatesArchived > 0) {
                logger.info(
                        "Send workflow completed with no new files to send. Duplicates archived={}",
                        duplicatesArchived
                );

                result.setWorkflowCompleted(true);
                result.setStatusMessage(
                        "No new files to send. Duplicate files archived: " + duplicatesArchived
                );

                completeWorkflowReport(result);

                return result;
            }

            logger.warn("Send workflow stopped: ZIP file was not created. Format={}", format);

            result.setStatusMessage("ZIP file was not created.");
            completeWorkflowReport(result);

            return result;
        }

        result.setZipCreated(true);
        logger.info("ZIP file created successfully: {}", zipFile.getFileName());

        boolean isSent = gmailService.sendZipFile(zipFile);

        if (!isSent) {
        logger.error("Send workflow stopped: Gmail send failed.");

        try {
            zipExportService.deleteZipFile(zipFile);
        } catch (java.io.IOException e) {
            logger.error(
                    "Gmail send failed and temporary ZIP cleanup also failed.",
                    e
            );
        }

        result.setStatusMessage("ZIP file was created, but the email was not sent.");
        completeWorkflowReport(result);

        return result;
    }

        result.setEmailSent(true);
        logger.info("Gmail send completed successfully.");

        int filesArchived = archiveService.archiveFiles(format);

        if (filesArchived < 0) {
            logger.error("Email was sent successfully, but archiving failed.");

            result.setStatusMessage("Email was sent successfully, but archiving failed.");
            completeWorkflowReport(result);

            return result;
        }

        result.setFilesArchivedCount(filesArchived);

        if (filesArchived == 0) {
            logger.warn("Email was sent successfully, but no files were archived.");

            result.setStatusMessage("Email was sent successfully, but no files were archived.");
            completeWorkflowReport(result);

            return result;
        }

        result.setFilesArchived(true);
        logger.info("Archive completed successfully. Files archived={}", filesArchived);

        try {
            zipExportService.archiveZipFile(zipFile);
        } catch (java.io.IOException e) {
            logger.error("Email was sent successfully, but ZIP archiving failed.", e);

            result.setStatusMessage(
                    "Email was sent successfully, but ZIP archiving failed."
            );

            completeWorkflowReport(result);

            return result;
        }

        result.setWorkflowCompleted(true);
        
        if (duplicatesArchived > 0) {
            result.setStatusMessage(
                    "Email sent successfully and files archived. Duplicate files skipped: "
                            + duplicatesArchived
            );
        } else {
            result.setStatusMessage(
                    "Email sent successfully and files archived."
            );
        }

        completeWorkflowReport(result);

        return result;
    }

    private void completeWorkflowReport(WorkflowReport workflowReport) {

        workflowReport.setEndTime(LocalDateTime.now());

        workflowReport.setDuration(
                (int) java.time.Duration.between(
                        workflowReport.getStartTime(),
                        workflowReport.getEndTime()
                ).toSeconds()
        );
    }
}