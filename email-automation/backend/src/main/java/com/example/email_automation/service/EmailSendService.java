package com.example.email_automation.service;

import java.nio.file.Path;

import org.springframework.stereotype.Service;

@Service
public class EmailSendService {

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

    public boolean sendEmail(String format) {

        Path zipFile = createZipForSend(format);

        if (zipFile == null) {
            return false;
        }

        boolean isSent = gmailService.sendZipFile(zipFile);

        if (!isSent) {
            return false;
        }

        return archiveService.archiveFiles(format);
    }
}