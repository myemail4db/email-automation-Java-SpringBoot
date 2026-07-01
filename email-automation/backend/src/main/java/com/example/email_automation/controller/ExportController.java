package com.example.email_automation.controller;

import java.time.Duration;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.email_automation.service.EmailExportService;

@RestController
public class ExportController {

    private final EmailExportService emailExportService;

    public ExportController(EmailExportService emailExportService) {
        this.emailExportService = emailExportService;
    }

    @GetMapping(value = "/api/export")
    public String getEmails(@RequestParam String format) {

        long startTime = System.nanoTime();

        // Run your function here
        String result = emailExportService.exportEmails(format);    

        try {
        //     TimeUnit.MINUTES.sleep(1); // sleep for 1 minute
                Thread.sleep(5000); // sleep for 5 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

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
