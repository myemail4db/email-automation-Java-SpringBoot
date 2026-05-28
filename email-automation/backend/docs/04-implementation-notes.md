# Iteration Notes

## Python Prototype Iteration

The original Python version was created to reduce the manual work involved in organizing recruiter and job application emails.

The workflow focused on retrieving recruiter emails from Gmail, cleaning the email content, exporting the processed information locally, and supporting batch email workflows.

As the workflow expanded, additional improvements were added to support larger email batches, cleaner exports, and more reliable processing.

The Python version validated the overall workflow and demonstrated the practical value of automating the recruiter email process. The automation reduced a manual 12-hour process down to about 90 minutes.

---

## Java Spring Boot Iteration

After validating the Python workflow, the project continued evolving into its next iteration using Java Spring Boot.

The Java version focuses on rebuilding the workflow using a cleaner backend architecture and more structured service-layer organization.

The current architecture separates:
- Gmail authentication
- Gmail processing
- DTO handling
- text filtering
- export workflows

The Java Spring Boot iteration also expands the long-term direction of the project to support:
- workflow automation
- analytics
- frontend integration
- future database persistence

The goal of the current iteration is to create a more scalable and maintainable backend workflow system while continuing to improve the recruiter email processing pipeline.