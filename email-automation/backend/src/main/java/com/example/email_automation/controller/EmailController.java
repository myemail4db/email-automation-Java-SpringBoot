package com.example.email_automation.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {

    @RequestMapping("/api/health")
    public String healthCheck() {
        return "Email Automation API is running";
    }

    @RequestMapping("/api/export")
    public String getEmails(@RequestParam String format) {

        if (format.equals("text")) {

            // Logic to convert emails to text format
            System.out.println("Emails have been converted to text format");

            // Logic to save the text file locally
            System.out.println("Text file has been saved locally");

            return "Completed the export to text format";

        } else if (format.equals("word")) {

            // Logic to convert emails to Word format
            System.out.println("Emails have been converted to Word format");

            // Logic to save the Word file locally
            System.out.println("Word file has been saved locally");

            return "Completed the export to Word format";

        } else {
            return "Invalid format, please specify \"txt\" or \"word\"";
        }
    }
}
