package com.example.email_automation.cli;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import com.example.email_automation.model.WorkflowReport;
import com.example.email_automation.service.EmailExportService;
import com.example.email_automation.service.EmailSendService;

@Component
public class EmailAutomationCli implements CommandLineRunner {

    private final ConfigurableApplicationContext context;
    private final EmailSendService emailSendService;
    private final EmailExportService emailExportService;

    public EmailAutomationCli(
            ConfigurableApplicationContext context, 
            EmailSendService emailSendService,
            EmailExportService emailExportService) {
        this.context = context;
        this.emailExportService = emailExportService;
        this.emailSendService = emailSendService;
    }
    
    @Override
    public void run(String... args) throws Exception {

        // Implement your CLI logic here
        if (args.length == 0) {
            return;
        }

        System.out.println("Email Automation CLI is running...");

        // Example: Handle different commands

        if (args.length < 3) {
            System.out.println("Missing required format. Use --format text or --format word.");
            triggerGracefulShutdown();
            return;
        }

        String command = args[0];
        if ("send".equalsIgnoreCase(command)) {
            // sends the emails
            System.out.println("Sending emails...");
        } else if ("export".equalsIgnoreCase(command)) {
            // exports the email data
            System.out.println("Exporting email data...");
        } else {
            System.out.println("Unknown command: " + command);
           
            // Programmatically shut down the running web server and JVM
            triggerGracefulShutdown();
            return;
        }

        String option = args[1];
        if ("--format".equalsIgnoreCase(option)) {

        } else {
            System.out.println("Unknown option: " + option);
            triggerGracefulShutdown();
            return;
        }

        String format = args[2];
        if ("text".equalsIgnoreCase(format)) {

            // sends the emails
            System.out.println("Using text Format ...");

        } else if ("word".equalsIgnoreCase(format)) {
            // exports the email data
            System.out.println("Using word Format ...");

        } else {
            System.out.println("Unknown format: " + format);
            triggerGracefulShutdown();
            return;
        }

        WorkflowReport workflowReport;

        if ("send".equalsIgnoreCase(command)) {
            workflowReport = emailSendService.processSend(format);
        } else {
            workflowReport = emailExportService.processExport(format);
        }   

        System.out.println(workflowReport.getStatusMessage());
        triggerGracefulShutdown();
        return;

    }

    private void triggerGracefulShutdown() {
        int exitCode = SpringApplication.exit(context, () -> 0);
        System.exit(exitCode);
    }

}
