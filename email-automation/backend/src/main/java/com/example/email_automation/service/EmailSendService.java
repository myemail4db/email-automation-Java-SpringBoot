package com.example.email_automation.service;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.email_automation.model.SendWorkflowResult;

@Service
public class EmailSendService {

    private static final Logger logger = LoggerFactory.getLogger(EmailSendService.class);

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

    public SendWorkflowResult processSend(String format) {

        SendWorkflowResult result = new SendWorkflowResult();

        Path zipFile = createZipForSend(format);

        if (zipFile == null) {
            logger.warn("Send workflow stopped: ZIP file was not created. Format={}", format);
            return result;
        }

        result.setZipCreated(true);
        logger.info("ZIP file created successfully: {}", zipFile.getFileName());

        boolean isSent = gmailService.sendZipFile(zipFile);

        if (!isSent) {
            logger.error("Send workflow stopped: Gmail send failed.");
            return result;
        }

        result.setEmailSent(true);
        logger.info("Gmail send completed successfully.");

        int filesArchived = archiveService.archiveFiles(format);

        if (filesArchived < 0) {
            logger.error("Email was sent successfully, but archiving failed.");
            return result;
        }

        result.setFilesArchivedCount(filesArchived);

        if (filesArchived == 0) {
            logger.warn("Email was sent successfully, but no files were archived.");
            return result;
        }

        result.setFilesArchived(true);
        logger.info("Archive completed successfully. Files archived={}", filesArchived);

        return result;
    }
}