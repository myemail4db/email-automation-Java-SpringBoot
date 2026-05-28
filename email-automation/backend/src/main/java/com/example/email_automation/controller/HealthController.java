package com.example.email_automation.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    
    @RequestMapping("/api/health")
    public String healthCheck() {
        return "Email Automation API is running";
    }
}
