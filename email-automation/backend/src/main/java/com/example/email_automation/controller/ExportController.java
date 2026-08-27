package com.example.email_automation.controller;

import java.time.Duration;

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

        long startTime = System.nanoTime();

        // Run your function here
        String result = emailExportService.exportEmails(format);    

        long endTime = System.nanoTime();
        long elapsedNanos = endTime - startTime;

        // Convert nanoseconds to hh:mm:ss
        Duration duration = Duration.ofNanos(elapsedNanos);
        String timeElapsed = String.format("%02d:%02d:%02d",
                duration.toHours(),
                duration.toMinutesPart(),
                duration.toSecondsPart());

        System.out.println("Execution time: " + timeElapsed + "\n");
        result += "Execution time: " + timeElapsed + "\n";

        return result;
    }
}
