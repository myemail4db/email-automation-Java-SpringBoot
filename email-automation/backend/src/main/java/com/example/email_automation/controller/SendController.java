package com.example.email_automation.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.email_automation.model.SendWorkflowResult;
import com.example.email_automation.service.EmailSendService;

@RestController
public class SendController {

    private final EmailSendService emailSendService;

    public SendController(EmailSendService emailSendService) {
        this.emailSendService = emailSendService;
    }

    @GetMapping("/api/send")
    public String sendEmails(@RequestParam(required = false, defaultValue = "text") String format) {

        SendWorkflowResult result = emailSendService.processSend(format);

        if (!result.isZipCreated()) {
            return "ZIP file was not created.";
        }

        if (!result.isEmailSent()) {
            return "ZIP file was created, but the email was not sent.";
        }

        if (!result.isFilesArchived()) {
            return "Email was sent successfully, but the files were not archived.";
        }

        return "Email sent successfully and files archived.";
    }
}