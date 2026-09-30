package com.example.email_automation.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.email_automation.model.WorkflowReport;
import com.example.email_automation.service.EmailSendService;

@RestController
public class SendController {

    private final EmailSendService emailSendService;

    public SendController(EmailSendService emailSendService) {
        this.emailSendService = emailSendService;
    }

    @GetMapping("/api/send")
    public String sendEmails(@RequestParam(required = false, defaultValue = "text") String format) {

        WorkflowReport result = emailSendService.processSend(format);

        return result.getStatusMessage();
    }
}