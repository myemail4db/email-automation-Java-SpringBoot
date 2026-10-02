package com.example.email_automation.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
    public void exportEmails_withNullFormat_returnsErrorMessage() {
        // Act
        WorkflowReport result = emailExportService.exportEmails(null);

        // Assert
        assertEquals(
                "Format parameter is required. Use text or word.",
                result.getStatusMessage()
        );
    }

    @Test
    public void exportEmails_withTextFormat_emailsEmpty_returnsNoEmailsMessage() {

        // Purpose: Verify export exits cleanly when no emails are available

        // Arrange
        String format = "Text";
        // Mock the GmailService to return an empty list of emails
        when(gmailService.getRecentEmails()).thenReturn(Collections.emptyList());

        // Act
        WorkflowReport result = emailExportService.exportEmails(format);

        // Assert
        assertEquals(
                "No emails found to export.",
                result.getStatusMessage()
        );
    }
    
    @Test
    public void exportEmails_withTextFormat_emailsNull_returnNoEmailsMessage() {

        // Purpose: Defensive test
        // Verify export returns a safe message when Gmail returns a list containing a null email.    

        // Arrange
        String format = "Text";
        // Mock the GmailService to return a list with a null email
        when(gmailService.getRecentEmails()).thenReturn(Collections.singletonList(null));

        // Act
        WorkflowReport result = emailExportService.exportEmails(format);

        // Assert
        assertEquals(
                "Export completed with 1 file(s) failed.",
                result.getStatusMessage()
        );

    }

    @Test
    void exportEmails_withTextFormat_emailsPresent_returnsExportSummary() throws Exception {
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
            "2024-06-01"
        );

        List<Message> emailList = new ArrayList<>();
        emailList.add(message);

        // Mock the services to return the expected values
        when(gmailService.getRecentEmails()).thenReturn(emailList);
        when(emailBodyExtractorService.extractEmailMessage(message)).thenReturn(emailMessage);
        when(textFilterService.clean(emailMessage.getBody())).thenReturn(emailMessage.getBody());
        when(fileExportService.saveFile(any(EmailMessage.class), eq(format))).thenReturn(true);

        // Act
        WorkflowReport result = emailExportService.exportEmails(format);

        // Assert
        assertEquals(1, result.getEmailsFound());
        assertEquals(1, result.getFilesSaved());
        assertEquals(0, result.getFilesFailed());
        assertTrue(result.isWorkflowCompleted());

        assertEquals(
                "Export completed successfully.",
                result.getStatusMessage()
        );

        verify(gmailService).moveEmailToLabel(message, true);
    }

    @Test
    void exportEmails_whenFileSaveFails_returnsFailureSummary() throws Exception {

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
                "2024-06-01"
        );

        List<Message> emailList = new ArrayList<>();
        emailList.add(message);

        when(gmailService.getRecentEmails()).thenReturn(emailList);
        when(emailBodyExtractorService.extractEmailMessage(message))
                .thenReturn(emailMessage);
        when(textFilterService.clean(emailMessage.getBody()))
                .thenReturn(emailMessage.getBody());

        when(fileExportService.saveFile(
                any(EmailMessage.class),
                eq(format)
        )).thenReturn(false);

        // Act
        WorkflowReport result =
                emailExportService.exportEmails(format);

        // Assert
        assertEquals(1, result.getEmailsFound());
        assertEquals(0, result.getFilesSaved());
        assertEquals(1, result.getFilesFailed());
        assertEquals(false, result.isWorkflowCompleted());

        assertEquals(
                "Export completed with 1 file(s) failed.",
                result.getStatusMessage()
        );

        verify(gmailService).moveEmailToLabel(message, false);
    }
}
