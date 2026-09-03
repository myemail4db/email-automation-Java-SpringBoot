package com.example.email_automation.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.email_automation.service.EmailExportService;

@RestController
public class ExportController {

    private final EmailExportService emailExportService;

    @Value("${email.export.default-format}")
    private String defaultFormat;

    public ExportController(EmailExportService emailExportService) {
        this.emailExportService = emailExportService;
    }
    
    @GetMapping(value = "/api/export")
    public String getEmails(@RequestParam(required = false) String format) {

        if (format == null || format.isEmpty()) {
            format = defaultFormat;
        }

        return emailExportService.exportEmails(format);
    }
}
