package com.example.email_automation.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {

    // future: list, preview, fetch, labels
    @RequestMapping("/api/emails")
    public String getEmails() {
        return "For future implementation: list, preview, fetch, labels.";
    }

}
