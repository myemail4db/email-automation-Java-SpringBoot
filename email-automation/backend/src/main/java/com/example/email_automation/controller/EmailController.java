package com.example.email_automation.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.email_automation.service.EmailExportService;

@RestController
public class EmailController {

    private final EmailExportService emailExportService;

    public EmailController(EmailExportService emailExportService) {
        this.emailExportService = emailExportService;
    }

    // future: list, preview, fetch, labels
    @RequestMapping("/api/emails")
    public String getEmails(@RequestParam String format) {
        return "For future implementation: list, preview, fetch, labels. Currently exporting emails in format: " + format;
    }

}
