# Implementation Notes

## Service Responsibilities

The Java implementation separates external API access, processing logic, file generation, ZIP processing, and outbound delivery into dedicated services.

Controllers remain focused on HTTP interaction while the service layer performs the workflow.

## Defensive Processing

The workflow includes defensive checks for conditions such as:

- missing export formats
- null service results
- empty email collections
- invalid or missing email content
- failed file saves

The goal is to return a meaningful workflow result rather than allowing common processing problems to cause an uncontrolled application failure.

## Workflow Results

The export workflow tracks processing results such as:

- emails found
- files successfully created
- failed file exports

Workflow reporting is being expanded so that ZIP creation and outbound email delivery can also contribute their results.

## ZIP Processing

ZIP processing is currently under development.

Current ZIP processing includes:

- creating the destination directory when necessary
- generating the ZIP file
- selecting the correct exported files
- adding files to the archive
- preventing resource leaks
- returning the ZIP result to the remaining workflow

ZIP processing should not be considered complete until the full workflow has been verified.

## Outbound Email

Outbound email delivery is also under development.

The intent is to reuse the existing authenticated Gmail integration to send the completed export archive to the configured destination address.

Email sending is kept in a dedicated service rather than adding outbound-email responsibilities to Gmail retrieval logic.

## Testing Approach

JUnit 5 and Mockito have been introduced for automated testing.

Current tests demonstrate:

- Spring Boot context loading
- dependency mocking
- defensive workflow behavior
- successful export workflow behavior

Additional testing is planned around important workflow boundaries.

## Error Handling and Logging

Services should log useful processing and failure information while avoiding unnecessary exposure of sensitive Gmail or OAuth data.

## Security Considerations

OAuth credentials, access tokens, refresh tokens, and other sensitive configuration should not be committed to source control.

Additional security hardening is planned, including stronger credential protection, integrity checks, protected export archives, and secure backup and recovery options.
