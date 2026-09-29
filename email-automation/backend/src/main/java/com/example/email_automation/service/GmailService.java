package com.example.email_automation.service;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.ModifyMessageRequest;
import com.google.api.services.gmail.model.Profile;

import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

/**
 * This service gets the raw emails from Gmail
 */
@Service
public class GmailService {

    private static final Logger logger = LoggerFactory.getLogger(GmailService.class);

    private final GmailAuthService authService;

    @Value("${email.gmail.recipient}")
    private String emailGmailSourceLabel;

    @Value("${email.gmail.success-label}")
    private String emailGmailSuccessLabel;

    @Value("${email.gmail.fail-label}")
    private String emailGmailFailLabel;

    @Value("${email.gmail.recipient}")
    private String emailGmailRecipient;

    public GmailService(GmailAuthService authService) {
        this.authService = authService;
    }

    public String checkGmailStatus() {

        // Call Gmail profile endpoint
        try {

            // Use Gmail AuthService
            Gmail service = authService.getGmailClient();

            // "me" refers to the currently authenticaed user
            Profile profile = service.users().getProfile("me").execute();
            return "Gmail API is working! User email: " + profile.getEmailAddress();

        } catch (Exception e) {
            // Log the error for debugging
            logger.error("Error accessing Gmail API", e);
            return "Error accessing Gmail API: " + e.getMessage();
        }

    }

    public List<Message> getRecentEmails() {

        // Call Gmail profile endpoint
        try {

            // Use Gmail AuthService
            Gmail service = authService.getGmailClient();

            // return the result
            List<Message> messages = service.users().messages()
                    .list("me")
                    .setQ("label: " + emailGmailSourceLabel)
                    .setMaxResults(5L)
                    .execute()
                    .getMessages();

            // return the messge IDs and body
            if (messages == null) {
                return new ArrayList<>();
            } else {
                List<Message> emailDetails = new ArrayList<>();
                for (Message message : messages) {
                    Message fullMessage = service.users().messages()
                            .get("me", message.getId())
                            .execute();
                    emailDetails.add(fullMessage);
                }
                return emailDetails;
            }

        } catch (Exception e) {
            logger.error("Error accessing Gmail API", e);
            throw new RuntimeException("Unable to retrieve emails from Gmail.", e);
        }
    }

    public void moveEmailToLabel(Message message, boolean isSuccess) throws Exception {

        Gmail service = authService.getGmailClient();

        String addLabelId;
        String removeLabelId;

        if (isSuccess) {
            addLabelId = getLabelId(service, emailGmailSuccessLabel);
            removeLabelId = getLabelId(service, emailGmailSourceLabel);
        } else {
            addLabelId = getLabelId(service, emailGmailFailLabel);
            removeLabelId = getLabelId(service, emailGmailSourceLabel);
        }

        ModifyMessageRequest mods = new ModifyMessageRequest()
                .setAddLabelIds(Collections.singletonList(addLabelId))
                .setRemoveLabelIds(Collections.singletonList(removeLabelId));

        service.users().messages()
                .modify("me", message.getId(), mods)
                .execute();

    }

    private String getLabelId(Gmail service, String labelName) throws Exception {

        List<Label> labels = service.users()
                .labels()
                .list("me")
                .execute().getLabels();

        for (Label label : labels) {
            if (labelName.equals(label.getName())) {
                return label.getId();
            }
        }
        throw new IllegalArgumentException(
                "Gmail label not found: " + labelName
        );
    }

    public boolean sendZipFile(Path zipFile) {

        if (zipFile == null || !Files.exists(zipFile)) {
            logger.error("ZIP file does not exist: {}", zipFile);
            return false;
        }

        try {

            Properties properties = new Properties();
            Session session = Session.getDefaultInstance(properties);

            MimeMessage emailMessage = new MimeMessage(session);

            emailMessage.setRecipient(
                    jakarta.mail.Message.RecipientType.TO,
                    new InternetAddress(emailGmailRecipient)
            );

            emailMessage.setSubject(
                    "Job Batch - " + zipFile.getFileName().toString()
            );

            MimeMultipart multipart = new MimeMultipart();

            MimeBodyPart textPart = new MimeBodyPart();

            textPart.setText(
                    "Please find the reviewed job batch attached.\n\nRegards"
            );

            multipart.addBodyPart(textPart);

            MimeBodyPart attachmentPart = new MimeBodyPart();

            attachmentPart.attachFile(zipFile.toFile());

            multipart.addBodyPart(attachmentPart);

            emailMessage.setContent(multipart);

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            emailMessage.writeTo(buffer);

            String encodedEmail = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(buffer.toByteArray());

            Message gmailMessage = new Message();
            gmailMessage.setRaw(encodedEmail);

            Gmail service = authService.getGmailClient();

            service.users()
                    .messages()
                    .send("me", gmailMessage)
                    .execute();

            logger.info("ZIP file sent successfully through Gmail: {}", zipFile);

            return true;

        } catch (Exception e) {
            logger.error("Failed to send ZIP file through Gmail.", e);
            return false;
        }
    }
}
