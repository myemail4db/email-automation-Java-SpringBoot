package com.example.email_automation.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.example.email_automation.model.EmailMessage;
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
        String result = emailExportService.exportEmails(null);

        // Assert
        assertEquals("Format parameter is required. Use text or word.", result);
    }

    @Test
    public void exportEmails_withTextFormat_emailsEmpty_returnsNoEmailsMessage() {

        // Purpose: Verify export exits cleanly when no emails are available

        // Arrange
        String format = "Text";
        // Mock the GmailService to return an empty list of emails
        when(gmailService.getRecentEmails()).thenReturn(Collections.emptyList());

        // Act
        String result = emailExportService.exportEmails(format);

        // Assert
        assertEquals("No emails found to export.", result);
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
        String result = emailExportService.exportEmails(format);

        // Assert
        assertEquals("No emails found to export.", result);
    }

    @Test
    public void exportEmails_withTextFormat_emailsPresent_returnsExportSummary() {
        // Purpose: Verify export returns a summary when emails are present. 
        // This is the happy path test for the export workflow.

        // Arrange
        String format = "Text";

        Message message = new Message();
        message.setId("Test Subject");
        message.setThreadId("sender@gmail.com");
        message.setSnippet("This is the full email body.");

        EmailMessage emailMessage = new EmailMessage(
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
        when(fileExportService.getExportDirectory()).thenReturn("processed_review/");

        // Act
        String result = emailExportService.exportEmails(format);

        // Assert
        assertEquals(true, result.contains("Emails found: 1"));
        assertEquals(true, result.contains("Files saved: 1"));
        assertEquals(true, result.contains("Files failed: 0"));
    }
}
