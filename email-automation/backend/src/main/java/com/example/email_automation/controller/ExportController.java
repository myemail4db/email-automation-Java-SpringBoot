package com.example.email_automation.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.email_automation.service.EmailExportService;

@RestController
public class ExportController {

    private final EmailExportService emailExportService;

    public ExportController(EmailExportService emailExportService) {
        this.emailExportService = emailExportService;
    }

    @RequestMapping("/api/export")
    public String getEmails(@RequestParam String format) {
        return emailExportService.exportEmails(format);
    }
    
}
