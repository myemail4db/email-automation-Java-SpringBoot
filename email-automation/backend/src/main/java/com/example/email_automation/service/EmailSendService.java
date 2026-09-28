package com.example.email_automation.service;

import java.nio.file.Path;

import org.springframework.stereotype.Service;

@Service
public class EmailSendService {

    private final GmailService gmailService;

    private final ZipExportService zipExportService;

    public EmailSendService(
            ZipExportService zipExportService,
            GmailService gmailService) {

        this.zipExportService = zipExportService;
        this.gmailService = gmailService;
    }

    public Path createZipForSend(String format) {

        Path zipFile = zipExportService.createZipEmail(format);

        if (zipFile == null) {
            return null;
        }

        return zipFile;
    }
}