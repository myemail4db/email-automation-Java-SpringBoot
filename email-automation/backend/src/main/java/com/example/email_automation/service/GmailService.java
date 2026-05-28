package com.example.email_automation.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.Profile;

/**
 * This service gets the raw emails from Gmail
 */

@Service
public class GmailService {

    private static final Logger logger = LoggerFactory.getLogger(GmailService.class);

    private final GmailAuthService authService;

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
                .setQ("label: for_friend")
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
}
