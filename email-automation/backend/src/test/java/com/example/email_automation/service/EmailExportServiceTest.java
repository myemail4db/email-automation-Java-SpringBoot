package com.example.email_automation.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.email_automation.model.EmailMessage;
import com.example.email_automation.model.WorkflowReport;
import com.google.api.services.gmail.model.Message;

@ExtendWith(MockitoExtension.class) // Use MockitoExtension to enable Mockito annotations
public class EmailExportServiceTest {

    @Mock
    private GmailService gmailService;

    @Mock
    private TextFilterService textFilterService;

    @Mock
    private EmailBodyExtractorService emailBodyExtractorService;

    @Mock
    private FileExportService fileExportService;

    @InjectMocks
    private EmailExportService emailExportService;

    @Test
    public void processExport_withNullFormat_returnsErrorMessage() {
        // Act
        WorkflowReport result = emailExportService.processExport(null);

        // Assert
        assertEquals(
                "Format parameter is required. Use text or word.",
                result.getStatusMessage());
    }

    @Test
    public void processExport_withTextFormat_emailsEmpty_returnsNoEmailsMessage() {

        // Purpose: Verify export exits cleanly when no emails are available

        // Arrange
        String format = "Text";
        // Mock the GmailService to return an empty list of emails
        when(gmailService.getRecentEmails()).thenReturn(Collections.emptyList());

        // Act
        WorkflowReport result = emailExportService.processExport(format);

        // Assert
        assertEquals(
                "No emails found to export.",
                result.getStatusMessage());
    }

    @Test
    public void processExport_withTextFormat_emailsNull_returnNoEmailsMessage() {

        // Purpose: Verify the export handles a null email without crashing.
        // The null email should be counted as a failed export.

        // Arrange
        String format = "Text";
        // Mock the GmailService to return a list with a null email
        when(gmailService.getRecentEmails()).thenReturn(Collections.singletonList(null));

        // Act
        WorkflowReport result = emailExportService.processExport(format);

        // Assert
        assertEquals(
                "Export completed with file failures.",
                result.getStatusMessage());

    }

    @Test
    void processExport_withTextFormat_emailsPresent_returnsExportSummary() throws Exception {
        // Purpose: Verify export returns a summary when emails are present.
        // This is the happy path test for the export workflow.

        // Arrange
        String format = "Text";

        Message message = new Message();
        message.setId("Test Subject");
        message.setThreadId("sender@gmail.com");
        message.setSnippet("This is the full email body.");

        EmailMessage emailMessage = new EmailMessage(
                "test-id",
                "Test Subject",
                "sender@gmail.com",
                "This is the full email body.",
                "2024-06-01");

        List<Message> emailList = new ArrayList<>();
        emailList.add(message);

        // Mock the services to return the expected values
        when(gmailService.getRecentEmails()).thenReturn(emailList);
        when(emailBodyExtractorService.extractEmailMessage(message)).thenReturn(emailMessage);
        when(textFilterService.clean(emailMessage.getBody())).thenReturn(emailMessage.getBody());
        when(fileExportService.saveFile(any(EmailMessage.class), eq(format))).thenReturn(true);

        // Act
        WorkflowReport result = emailExportService.processExport(format);

        // Assert
        assertEquals(1, result.getEmailsFound());
        assertEquals(1, result.getFilesSaved());
        assertEquals(0, result.getFilesFailed());
        assertTrue(result.isWorkflowCompleted());

        assertEquals(
                "Export completed successfully.",
                result.getStatusMessage());

        verify(gmailService).updateEmailLabels(message, true);
    }

    @Test
    void processExport_whenFileSaveFails_returnsFailureSummary() throws Exception {

        // Purpose: Verify a file export failure is reported correctly
        // and the Gmail message is moved to the failure label.

        // Arrange
        String format = "Text";

        Message message = new Message();
        message.setId("test-message-id");

        EmailMessage emailMessage = new EmailMessage(
                "test-message-id",
                "Test Subject",
                "sender@gmail.com",
                "This is the full email body.",
                "2024-06-01");

        List<Message> emailList = new ArrayList<>();
        emailList.add(message);

        when(gmailService.getRecentEmails()).thenReturn(emailList);
        when(emailBodyExtractorService.extractEmailMessage(message))
                .thenReturn(emailMessage);
        when(textFilterService.clean(emailMessage.getBody()))
                .thenReturn(emailMessage.getBody());

        when(fileExportService.saveFile(
                any(EmailMessage.class),
                eq(format))).thenReturn(false);

        // Act
        WorkflowReport result = emailExportService.processExport(format);

        // Assert
        assertEquals(1, result.getEmailsFound());
        assertEquals(0, result.getFilesSaved());
        assertEquals(1, result.getFilesFailed());
        assertEquals(false, result.isWorkflowCompleted());

        assertEquals(
                "Export completed with file failures.",
                result.getStatusMessage());

        verify(gmailService).updateEmailLabels(message, false);
    }

    @Test
    void processExport_whenGmailLabelUpdateFails_returnsIncompleteWorkflow() throws Exception {

        // Purpose: Verify that a Gmail label update failure is reported
        // even when the email file is saved successfully.

        // Arrange
        String format = "Text";

        Message message = new Message();
        message.setId("test-message-id");

        EmailMessage emailMessage = new EmailMessage(
                "test-message-id",
                "Test Subject",
                "sender@gmail.com",
                "This is the full email body.",
                "2024-06-01");

        List<Message> emailList = new ArrayList<>();
        emailList.add(message);

        when(gmailService.getRecentEmails()).thenReturn(emailList);
        when(emailBodyExtractorService.extractEmailMessage(message))
                .thenReturn(emailMessage);
        when(textFilterService.clean(emailMessage.getBody()))
                .thenReturn(emailMessage.getBody());

        when(fileExportService.saveFile(
                any(EmailMessage.class),
                eq(format))).thenReturn(true);

        doThrow(new IOException("Simulated Gmail label failure"))
                .when(gmailService)
                .updateEmailLabels(message, true);

        // Act
        WorkflowReport result = emailExportService.processExport(format);

        // Assert
        assertEquals(1, result.getEmailsFound());
        assertEquals(1, result.getFilesSaved());
        assertEquals(0, result.getFilesFailed());
        assertEquals(false, result.isWorkflowCompleted());

        assertEquals(
                "Gmail label updates failed.",
                result.getStatusMessage());

        verify(gmailService).updateEmailLabels(message, true);

    }

    @Test
    void processExport_whenFileSaveAndGmailLabelUpdateFail_returnsIncompleteWorkflow() throws Exception {

        // Purpose: Verify that both a file save failure and a Gmail
        // label update failure are reported in the workflow summary.

        // Arrange
        String format = "Text";

        Message message = new Message();
        message.setId("test-message-id");

        EmailMessage emailMessage = new EmailMessage(
                "test-message-id",
                "Test Subject",
                "sender@gmail.com",
                "This is the full email body.",
                "2024-06-01");

        List<Message> emailList = new ArrayList<>();
        emailList.add(message);

        when(gmailService.getRecentEmails()).thenReturn(emailList);
        when(emailBodyExtractorService.extractEmailMessage(message))
                .thenReturn(emailMessage);
        when(textFilterService.clean(emailMessage.getBody()))
                .thenReturn(emailMessage.getBody());

        when(fileExportService.saveFile(
                any(EmailMessage.class),
                eq(format))).thenReturn(false);

        doThrow(new IOException("Simulated Gmail label failure"))
                .when(gmailService)
                .updateEmailLabels(message, false);

        // Act
        WorkflowReport result = emailExportService.processExport(format);

        // Assert
        assertEquals(1, result.getEmailsFound());
        assertEquals(0, result.getFilesSaved());
        assertEquals(1, result.getFilesFailed());
        assertEquals(false, result.isWorkflowCompleted());

        verify(gmailService).updateEmailLabels(message, false);

        assertEquals(
                "Export completed with file failures and Gmail label-update failures.",
                result.getStatusMessage());
    }

    @Test
    void processExport_whenOneEmailExtractionFails_continuesProcessingRemainingEmails() throws Exception {

        // Purpose: Verify that an email extraction failure does not stop
        // the remaining emails from being processed.
        // Failed emails receive the failure label, while successfully
        // processed emails receive the success label.

        // Arrange
        String format = "Text";

        Message message1 = new Message().setId("test-message-1");
        Message message2 = new Message().setId("test-message-2");
        Message message3 = new Message().setId("test-message-3");

        EmailMessage emailMessage1 = new EmailMessage(
                "test-message-id1",
                "Test Subject",
                "sender@gmail.com",
                "This is the full email body.",
                "2024-06-01");

        EmailMessage emailMessage3 = new EmailMessage(
                "test-message-id3",
                "Test Subject",
                "sender@gmail.com",
                "This is the full email body.",
                "2024-06-01");

        List<Message> emailList = new ArrayList<>();
        emailList.add(message1);
        emailList.add(message2);
        emailList.add(message3);

        when(gmailService.getRecentEmails()).thenReturn(emailList);

        // Message 1
        when(emailBodyExtractorService.extractEmailMessage(message1))
                .thenReturn(emailMessage1);
        when(textFilterService.clean(emailMessage1.getBody()))
                .thenReturn(emailMessage1.getBody());
        when(fileExportService.saveFile(
                any(EmailMessage.class),
                eq(format))).thenReturn(true);

        // Message 2
        when(emailBodyExtractorService.extractEmailMessage(message2))
                .thenThrow(new IllegalArgumentException("Invalid Base64 email body"));

        // Message 3
        when(emailBodyExtractorService.extractEmailMessage(message3))
                .thenReturn(emailMessage3);

        // Act
        WorkflowReport result = emailExportService.processExport(format);

        // Assert
        assertEquals(3, result.getEmailsFound());
        assertEquals(2, result.getFilesSaved());
        assertEquals(1, result.getFilesFailed());
        assertEquals(false, result.isWorkflowCompleted());

        verify(gmailService).updateEmailLabels(message1, true);
        verify(gmailService).updateEmailLabels(message2, false);
        verify(gmailService).updateEmailLabels(message3, true);

        assertEquals(
                "Export completed with file failures.",
                result.getStatusMessage());
    }

    @Test
    void processExport_whenGmailRetrievalFails_returnsFailureReport() throws Exception {

        // Purpose: Verify that a Gmail retrieval failure is handled
        // without attempting to process or save any emails.

        // Arrange
        String format = "Text";

        when(gmailService.getRecentEmails())
                .thenThrow(new RuntimeException("Unable to retrieve emails from Gmail."));

        // Act
        WorkflowReport result = emailExportService.processExport(format);

        // Assert
        assertEquals(0, result.getEmailsFound());
        assertEquals(0, result.getFilesSaved());
        assertEquals(0, result.getFilesFailed());
        assertEquals(false, result.isWorkflowCompleted());

        assertEquals(
                "Unable to retrieve emails from Gmail.",
                result.getStatusMessage());

        verify(fileExportService, never())
                .saveFile(any(EmailMessage.class), eq(format));
    }

    @Test
    void processExport_whenGmailReturnsNullList_returnsFailureReport() {

        // Purpose: Verify the workflow handles a null email list
        // without throwing an exception.

        // Arrange
        String format = "Text";

        when(gmailService.getRecentEmails()).thenReturn(null);

        // Act
        WorkflowReport result = emailExportService.processExport(format);

        // Assert
        assertEquals(0, result.getEmailsFound());
        assertEquals(0, result.getFilesSaved());
        assertEquals(0, result.getFilesFailed());
        assertFalse(result.isWorkflowCompleted());

        assertEquals(
                "Unable to retrieve emails from Gmail.",
                result.getStatusMessage());

    }

    @Test
    void processExport_withInvalidFormat_returnsErrorMessage() {

        // Arrange
        String format = "pdf";

        // Act
        WorkflowReport result = emailExportService.processExport(format);

        // Assert
        assertEquals(
                "Invalid format. Use text or word.",
                result.getStatusMessage());

        assertFalse(result.isWorkflowCompleted());
        assertEquals(0, result.getEmailsFound());
        verify(gmailService, never()).getRecentEmails();
    }

    @Test
    void processExport_withUppercaseTextFormat_acceptsFormat() {

        // Arrange
        String format = "TEXT";

        when(gmailService.getRecentEmails())
                .thenReturn(Collections.emptyList());

        // Act
        WorkflowReport result = emailExportService.processExport(format);

        // Assert
        assertEquals(
                "No emails found to export.",
                result.getStatusMessage());

        assertTrue(result.isWorkflowCompleted());
        assertEquals(0, result.getEmailsFound());

        verify(gmailService).getRecentEmails();
    }

    @Test
    void processExport_whenTextCleaningFails_marksEmailAsFailed() throws Exception {

        // Arrange
        String format = "Text";

        Message message = new Message().setId("test-message-id");

        EmailMessage emailMessage = new EmailMessage(
                "test-message-id",
                "Test Subject",
                "sender@example.com",
                "Original email body.",
                "2024-06-01");

        when(gmailService.getRecentEmails())
                .thenReturn(List.of(message));

        when(emailBodyExtractorService.extractEmailMessage(message))
                .thenReturn(emailMessage);

        when(textFilterService.clean(emailMessage.getBody()))
                .thenThrow(new IllegalStateException("Text cleaning failed"));

        // Act
        WorkflowReport result = emailExportService.processExport(format);

        // Assert
        assertEquals(1, result.getEmailsFound());
        assertEquals(0, result.getFilesSaved());
        assertEquals(1, result.getFilesFailed());
        assertFalse(result.isWorkflowCompleted());
        assertEquals(
                "Export completed with file failures.",
                result.getStatusMessage());
        verify(gmailService).updateEmailLabels(message, false);
        verify(fileExportService, never())
                .saveFile(any(EmailMessage.class), eq(format));
    }

    @Test
    void processExport_whenFileSaveThrowsException_marksEmailAsFailed() throws Exception {

        // Arrange
        String format = "Text";

        Message message = new Message().setId("test-message-id");

        EmailMessage emailMessage = new EmailMessage(
                "test-message-id",
                "Test Subject",
                "sender@example.com",
                "Original email body.",
                "2024-06-01");

        when(gmailService.getRecentEmails())
                .thenReturn(List.of(message));

        when(emailBodyExtractorService.extractEmailMessage(message))
                .thenReturn(emailMessage);

        when(textFilterService.clean(emailMessage.getBody()))
                .thenReturn(emailMessage.getBody());

        when(fileExportService.saveFile(
                any(EmailMessage.class),
                eq(format)))
                .thenThrow(new IllegalStateException("Disk write failed"));

        // Act
        WorkflowReport result = emailExportService.processExport(format);

        // Assert
        assertEquals(1, result.getEmailsFound());
        assertEquals(0, result.getFilesSaved());
        assertEquals(1, result.getFilesFailed());
        assertFalse(result.isWorkflowCompleted());
        verify(fileExportService).saveFile(
                any(EmailMessage.class),
                eq(format));

        verify(gmailService).updateEmailLabels(message, false);
    }
}
