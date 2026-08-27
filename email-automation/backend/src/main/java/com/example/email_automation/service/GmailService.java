package com.example.email_automation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.Profile;
import com.google.api.services.gmail.model.ModifyMessageRequest;

/**
 * This service gets the raw emails from Gmail
 */

@Service
public class GmailService {

    private static final Logger logger = LoggerFactory.getLogger(GmailService.class);

    private final GmailAuthService authService;

    @Value("${email.gmail.source-label}")
    private String emailGmailSourceLabel;

    @Value("${email.gmail.success-label}")
    private String emailGmailSuccessLabel;

    @Value("${email.gmail.fail-label}")
    private String emailGmailFailLabel;

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
            } 
            else {
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
            // Log the error for debugging
            logger.error("Error accessing Gmail API", e);
        }
        
        return new ArrayList<>();
    }

    public void moveEmailToLabel(Message message, boolean isSuccess) throws Exception {
    
        // Determine the label to apply based on success or failure
        String userId = "me";

        Gmail service = authService.getGmailClient();

        String AddLabelId;
        String RemoveLabelId;

        if (isSuccess) {
            AddLabelId = emailGmailSuccessLabel;
            RemoveLabelId = emailGmailSourceLabel;
        } else {
            AddLabelId = emailGmailFailLabel;
            RemoveLabelId = emailGmailSourceLabel;
        }

        ModifyMessageRequest mods = new ModifyMessageRequest()
            .setAddLabelIds(Collections.singletonList(AddLabelId))
            .setRemoveLabelIds(Collections.singletonList(RemoveLabelId));

        Message response = service.users().messages().modify(userId, message.getId(), mods).execute();

    }

}
