package com.example.email_automation.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.email_automation.service.GmailService;
import com.google.api.services.gmail.model.Message;

@RestController
public class GmailController {

    private final GmailService gmailService;

    // Constructor injection of GmailService
    public GmailController(GmailService gmailService) {
        this.gmailService = gmailService;
    }

    @RequestMapping("/api/gmail/status")
    public String getStatus() {
        // Return the response
        return gmailService.checkGmailStatus();
    }

    @RequestMapping("/api/gmail/emails")
    public List<Message> getRecentEmails() {
        // Return the response
        return gmailService.getRecentEmails();
    }
}
