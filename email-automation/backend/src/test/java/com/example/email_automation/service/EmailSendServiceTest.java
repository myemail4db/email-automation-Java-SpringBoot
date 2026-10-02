package com.example.email_automation.service;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.email_automation.model.WorkflowReport;

class EmailSendServiceTest {

    @Test
    void shouldArchiveZipAfterSuccessfulSend() throws Exception {

        ZipExportService zipExportService =
                mock(ZipExportService.class);

        GmailService gmailService =
                mock(GmailService.class);

        ArchiveService archiveService =
                mock(ArchiveService.class);

        EmailSendService emailSendService =
                new EmailSendService(
                        zipExportService,
                        gmailService,
                        archiveService
                );

        Path zipFile = Path.of("ready_to_send/job_batch_test.zip");

        when(archiveService.archiveDuplicateFiles("text"))
                .thenReturn(0);

        when(zipExportService.createZipEmail("text"))
                .thenReturn(zipFile);

        when(gmailService.sendZipFile(zipFile))
                .thenReturn(true);

        when(archiveService.archiveFiles("text"))
                .thenReturn(2);

        emailSendService.processSend("text");

        verify(zipExportService).archiveZipFile(zipFile);
    }

    @Test
    void shouldNotArchiveZipWhenEmailSendFails() throws Exception {

        ZipExportService zipExportService =
                mock(ZipExportService.class);

        GmailService gmailService =
                mock(GmailService.class);

        ArchiveService archiveService =
                mock(ArchiveService.class);

        EmailSendService emailSendService =
                new EmailSendService(
                        zipExportService,
                        gmailService,
                        archiveService
                );

        Path zipFile = Path.of("ready_to_send/job_batch_test.zip");

        when(archiveService.archiveDuplicateFiles("text"))
                .thenReturn(0);

        when(zipExportService.createZipEmail("text"))
                .thenReturn(zipFile);

        when(gmailService.sendZipFile(zipFile))
                .thenReturn(false);

        emailSendService.processSend("text");

        verify(zipExportService, never())
                .archiveZipFile(zipFile);

        verify(zipExportService)
                .deleteZipFile(zipFile);
    }

    @Test
    void shouldNotArchiveZipWhenIndividualFileArchiveFails() throws Exception {

        ZipExportService zipExportService =
                mock(ZipExportService.class);

        GmailService gmailService =
                mock(GmailService.class);

        ArchiveService archiveService =
                mock(ArchiveService.class);

        EmailSendService emailSendService =
                new EmailSendService(
                        zipExportService,
                        gmailService,
                        archiveService
                );

        Path zipFile = Path.of("ready_to_send/job_batch_test.zip");

        when(archiveService.archiveDuplicateFiles("text"))
                .thenReturn(0);

        when(zipExportService.createZipEmail("text"))
                .thenReturn(zipFile);

        when(gmailService.sendZipFile(zipFile))
                .thenReturn(true);

        when(archiveService.archiveFiles("text"))
                .thenReturn(-1);

        emailSendService.processSend("text");

        verify(zipExportService, never())
                .archiveZipFile(zipFile);
    }

    @Test
    void shouldReportFailureWhenZipArchiveFailsAfterSuccessfulSend() throws Exception {

        ZipExportService zipExportService =
                mock(ZipExportService.class);

        GmailService gmailService =
                mock(GmailService.class);

        ArchiveService archiveService =
                mock(ArchiveService.class);

        EmailSendService emailSendService =
                new EmailSendService(
                        zipExportService,
                        gmailService,
                        archiveService
                );

        Path zipFile = Path.of("ready_to_send/job_batch_test.zip");

        when(archiveService.archiveDuplicateFiles("text"))
                .thenReturn(0);

        when(zipExportService.createZipEmail("text"))
                .thenReturn(zipFile);

        when(gmailService.sendZipFile(zipFile))
                .thenReturn(true);

        when(archiveService.archiveFiles("text"))
                .thenReturn(2);

        doThrow(new IOException("ZIP archive failed"))
                .when(zipExportService)
                .archiveZipFile(zipFile);

        WorkflowReport result =
                emailSendService.processSend("text");

        assertTrue(result.isEmailSent());
        assertTrue(result.isFilesArchived());
        assertFalse(result.isWorkflowCompleted());

        assertEquals(
                "Email was sent successfully, but ZIP archiving failed.",
                result.getStatusMessage()
        );
    }

    @Test
    void shouldSendNewFilesWhenDuplicatesAreFound() throws Exception {

        // Purpose: Verify duplicate files do not prevent
        // new reviewed files from being sent.

        // Arrange
        ZipExportService zipExportService =
                mock(ZipExportService.class);

        GmailService gmailService =
                mock(GmailService.class);

        ArchiveService archiveService =
                mock(ArchiveService.class);

        EmailSendService emailSendService =
                new EmailSendService(
                        zipExportService,
                        gmailService,
                        archiveService
                );

        Path zipFile =
                Path.of("ready_to_send/job_batch_test.zip");

        when(archiveService.archiveDuplicateFiles("text"))
                .thenReturn(1);

        when(zipExportService.createZipEmail("text"))
                .thenReturn(zipFile);

        when(gmailService.sendZipFile(zipFile))
                .thenReturn(true);

        when(archiveService.archiveFiles("text"))
                .thenReturn(1);

        // Act
        WorkflowReport result =
                emailSendService.processSend("text");

        // Assert
        assertTrue(result.isEmailSent());
        assertTrue(result.isFilesArchived());
        assertTrue(result.isWorkflowCompleted());

        verify(zipExportService).createZipEmail("text");
        verify(gmailService).sendZipFile(zipFile);
        verify(archiveService).archiveFiles("text");
        verify(zipExportService).archiveZipFile(zipFile);
    }
}