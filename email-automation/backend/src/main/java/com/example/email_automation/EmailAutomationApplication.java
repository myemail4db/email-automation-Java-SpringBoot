package com.example.email_automation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EmailAutomationApplication {

	public static void main(String[] args) {

		SpringApplication application =
				new SpringApplication(EmailAutomationApplication.class);

		if (args.length > 0) {
			// If command-line arguments are provided, run the CLI
			application.setWebApplicationType(WebApplicationType.NONE);
		} else {
			// If no command-line arguments, run the web application
			application.setWebApplicationType(WebApplicationType.SERVLET);
		}
		application.run(args);
	}
}
