package com.example.email_automation.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.example.email_automation.model.WorkflowReport;
import com.example.email_automation.service.EmailExportService;

@RestController
public class ExportController {

    private final EmailExportService emailExportService;

    public ExportController(EmailExportService emailExportService) {
        this.emailExportService = emailExportService;
    }

    @GetMapping("/api/export")
    public String processExport(@RequestParam String format) {

        if (!format.equalsIgnoreCase("text")
                && !format.equalsIgnoreCase("word")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid format. Use text or word.");
        }

        WorkflowReport workflowReport = emailExportService.processExport(format);

        return workflowReport.getStatusMessage();
    }
}
