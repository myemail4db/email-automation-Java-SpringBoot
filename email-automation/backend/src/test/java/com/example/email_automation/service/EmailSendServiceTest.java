package com.example.email_automation.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.email_automation.model.WorkflowReport;

class EmailSendServiceTest {

    @Test
    void shouldArchiveZipAfterSuccessfulSend() throws Exception {

        ZipExportService zipExportService = mock(ZipExportService.class);

        GmailService gmailService = mock(GmailService.class);

        ArchiveService archiveService = mock(ArchiveService.class);

        EmailSendService emailSendService = new EmailSendService(
                zipExportService,
                gmailService,
                archiveService);

        Path zipFile = Path.of("ready_to_send/job_batch_test.zip");

        when(archiveService.archiveDuplicateFiles("text"))
                .thenReturn(0);

        when(archiveService.getProcessedFileGmailIds("text"))
                .thenReturn(List.of("gmail123", "gmail456"));

        when(zipExportService.createZipEmail("text"))
                .thenReturn(zipFile);

        when(gmailService.sendZipFile(zipFile))
                .thenReturn(true);

        when(archiveService.archiveFiles("text"))
                .thenReturn(2);

        emailSendService.processSend("text");

        verify(zipExportService).archiveZipFile(zipFile);
        verify(gmailService).moveEmailToSentLabel("gmail123");
        verify(gmailService).moveEmailToSentLabel("gmail456");
    }

    @Test
    void shouldNotArchiveZipWhenEmailSendFails() throws Exception {

        ZipExportService zipExportService = mock(ZipExportService.class);

        GmailService gmailService = mock(GmailService.class);

        ArchiveService archiveService = mock(ArchiveService.class);

        EmailSendService emailSendService = new EmailSendService(
                zipExportService,
                gmailService,
                archiveService);

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

        ZipExportService zipExportService = mock(ZipExportService.class);

        GmailService gmailService = mock(GmailService.class);

        ArchiveService archiveService = mock(ArchiveService.class);

        EmailSendService emailSendService = new EmailSendService(
                zipExportService,
                gmailService,
                archiveService);

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

        ZipExportService zipExportService = mock(ZipExportService.class);

        GmailService gmailService = mock(GmailService.class);

        ArchiveService archiveService = mock(ArchiveService.class);

        EmailSendService emailSendService = new EmailSendService(
                zipExportService,
                gmailService,
                archiveService);

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

        WorkflowReport result = emailSendService.processSend("text");

        assertTrue(result.isEmailSent());
        assertTrue(result.isFilesArchived());
        assertFalse(result.isWorkflowCompleted());

        assertEquals(
                "Email was sent successfully, but ZIP archiving failed.",
                result.getStatusMessage());
    }

    @Test
    void shouldSendNewFilesWhenDuplicatesAreFound() throws Exception {

        // Purpose: Verify duplicate files do not prevent
        // new reviewed files from being sent.

        // Arrange
        ZipExportService zipExportService = mock(ZipExportService.class);

        GmailService gmailService = mock(GmailService.class);

        ArchiveService archiveService = mock(ArchiveService.class);

        EmailSendService emailSendService = new EmailSendService(
                zipExportService,
                gmailService,
                archiveService);

        Path zipFile = Path.of("ready_to_send/job_batch_test.zip");

        when(archiveService.archiveDuplicateFiles("text"))
                .thenReturn(1);

        when(zipExportService.createZipEmail("text"))
                .thenReturn(zipFile);

        when(gmailService.sendZipFile(zipFile))
                .thenReturn(true);

        when(archiveService.archiveFiles("text"))
                .thenReturn(1);

        // Act
        WorkflowReport result = emailSendService.processSend("text");

        // Assert
        assertTrue(result.isEmailSent());
        assertTrue(result.isFilesArchived());
        assertTrue(result.isWorkflowCompleted());

        verify(zipExportService).createZipEmail("text");
        verify(gmailService).sendZipFile(zipFile);
        verify(archiveService).archiveFiles("text");
        verify(zipExportService).archiveZipFile(zipFile);
    }

    @Test
    void shouldReportFailureWhenGmailLabelUpdateFails() throws Exception {

        ZipExportService zipExportService = mock(ZipExportService.class);

        GmailService gmailService = mock(GmailService.class);

        ArchiveService archiveService = mock(ArchiveService.class);

        EmailSendService emailSendService = new EmailSendService(
                zipExportService,
                gmailService,
                archiveService);

        Path zipFile = Path.of("ready_to_send/job_batch_test.zip");

        when(archiveService.archiveDuplicateFiles("text"))
                .thenReturn(0);

        when(archiveService.getProcessedFileGmailIds("text"))
                .thenReturn(List.of("gmail123", "gmail456"));

        when(zipExportService.createZipEmail("text"))
                .thenReturn(zipFile);

        when(gmailService.sendZipFile(zipFile))
                .thenReturn(true);
        when(archiveService.archiveFiles("text"))
                .thenReturn(2);

        doThrow(new IOException("Gmail label update failed"))
                .when(gmailService)
                .moveEmailToSentLabel("gmail123");

        WorkflowReport result = emailSendService.processSend("text");

        assertTrue(result.isEmailSent());
        assertTrue(result.isFilesArchived());
        assertFalse(result.isWorkflowCompleted());
        verify(gmailService).moveEmailToSentLabel("gmail123");
        verify(gmailService).moveEmailToSentLabel("gmail456");
    }

    @Test
    void shouldNotSendEmailWhenGmailIdCollectionFails() throws Exception {

        ZipExportService zipExportService = mock(ZipExportService.class);

        GmailService gmailService = mock(GmailService.class);

        ArchiveService archiveService = mock(ArchiveService.class);

        EmailSendService emailSendService = new EmailSendService(
                zipExportService,
                gmailService,
                archiveService);

        when(archiveService.archiveDuplicateFiles("text")).thenReturn(0);

        when(archiveService.getProcessedFileGmailIds("text"))
                .thenThrow(new IllegalStateException("Directory read failed"));

        WorkflowReport result = emailSendService.processSend("text");

        assertFalse(result.isWorkflowCompleted());
        assertFalse(result.isEmailSent());
        assertEquals(
                "Failed to gather Gmail IDs. No email was sent.",
                result.getStatusMessage());

        verify(zipExportService, never()).createZipEmail(anyString());
        verify(gmailService, never()).sendZipFile(any());
    }
}