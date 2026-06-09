package com.example.email_automation.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

/**
 *  The Text Cleaning process involves:
 * 
 *  1. Making line breaks consistent for Windows, Mac, and Linux.
 *  2. Removing reply markers that indicate the start of a quoted email thread.
 *  3. Removing noise lines that are common in email replies but don't necessarily indicate the start of a thread, such as "From:", "Sent:", "To:", "Subject:", etc.
 *  4. Remove multi-line blocks such as legal disclaimers or survey requests.
 *  5. Stripping out non-printable characters that may cause issues in text processing or exporting.
 *  6. Removing unnecessary whitespace and blank lines to improve readability.
 *  7. Trimming leading and trailing whitespace from the email body to ensure there are no unnecessary spaces at the beginning or end of the content.
 * 
 */

@Service
public class TextFilterService {

    public String clean(String body) {

        if (body == null || body.isEmpty()) {
            return "";
        }

        // 1. Normalize line breaks consistent for Windows, Mac, and Linux
        body = body.replaceAll("\\r\\n", "\\n") // Handle Windows-style line breaks
                   .replaceAll("\\r", "\\n");   // Handle old Mac-style line breaks

        // 2. Remove Reply Markers like "On [date], [name] wrote:" and similar patterns, 
        body = removeReplyMarkers(body);

        // 3. Remove Noise Lines such as "From:", "Sent:", "To:", "Subject:", etc.
        body = removeNoiseLines(body);

        // 4. Remove multi-line blocks to remove, such as legal disclaimers or survey requests.
        body = removeMultiLineBlocks(body);

        // 5. Strip out non-printable characters that may cause issues in text processing or exporting.
        // Strips 0-8, 11-12, and 14-31, bypassing 9 (\t), 10 (\n), and 13 (\r)
        body = body.replaceAll("[\u0000-\u0008\u000B\u000C\u000E-\u001F]", "");

        // 6. Consolidate multiple consecutive blank lines into a single blank line to improve readability.
        body = body.replaceAll("(?m)(^\\s*$\\n){2,}", "\\n"); 

        // 7. Remove leading and trailing whitespace
        body = body.trim(); 

        return body;
    }

    private String removeMultiLineBlocks(String body) {

        // Multi-line blocks to remove, such as legal disclaimers or survey requests.
        List<Pattern> BLOCK_PATTERNS = Arrays.asList(
                Pattern.compile("CONFIDENTIALITY NOTICE:.*?(?=\\n\\n|\\Z)", Pattern.CASE_INSENSITIVE),
                Pattern.compile("How am I doing\\?.*?(?=\\n\\n|\\Z)", Pattern.CASE_INSENSITIVE),
                Pattern.compile("(CCPA|CCCPA)\\s+Privacy Notice.*?(?=\\n\\n|\\Z)", Pattern.CASE_INSENSITIVE),
                Pattern.compile("All qualified applicants will receive consideration for employment.*?(?=\\n\\n|\\Z)", Pattern.CASE_INSENSITIVE),
                Pattern.compile("To unsubscribe.*?(?=\\n\\n|\\Z)", Pattern.CASE_INSENSITIVE),
                Pattern.compile("unsubscribe.*?(?=\\n\\n|\\Z)", Pattern.CASE_INSENSITIVE),
                Pattern.compile("(equal opportunity employer|reasonable accommodation|protected veteran|characteristic protected by law).*?(?=\\n\\n|\\Z)", Pattern.CASE_INSENSITIVE),
                Pattern.compile("(confidentiality notice|privileged and confidential information).*?(?=\\n\\n|\\Z)", Pattern.CASE_INSENSITIVE),
                Pattern.compile("(privacy notice|email confidentiality and privacy).*?(?=\\n\\n|\\Z)", Pattern.CASE_INSENSITIVE),
                Pattern.compile("Confidentiality Notice:.*?(?=\\n\\n|\\Z)", Pattern.CASE_INSENSITIVE),
                Pattern.compile("The information contained in this message may be privileged and confidential and protected from disclosure.*?(?=\\n\\n|\\Z)", Pattern.CASE_INSENSITIVE)
        );
        // Remove multi-line blocks to remove, such as legal disclaimers or survey requests.
        for (Pattern blockPattern : BLOCK_PATTERNS) {
            body = blockPattern.matcher(body).replaceAll("");
        }
        return body;
    }

    private String removeNoiseLines(String body) {

        // Lines that are common in email replies but don't necessarily indicate the start of a thread, such as "From:", "Sent:", "To:", "Subject:", etc.
        List<Pattern> NOISE_LINE_PATTERNS = Arrays.asList(
            Pattern.compile("^Sent from Yahoo Mail.*$", Pattern.CASE_INSENSITIVE),
            Pattern.compile("^Get Outlook for .*?$", Pattern.CASE_INSENSITIVE),
            Pattern.compile("^CAUTION:.*$", Pattern.CASE_INSENSITIVE),
            Pattern.compile("^WARNING:.*$", Pattern.CASE_INSENSITIVE),
            Pattern.compile("^-+Original Message-+$", Pattern.CASE_INSENSITIVE),
            Pattern.compile("^Begin forwarded message:\\s*$", Pattern.CASE_INSENSITIVE),
            Pattern.compile("^-+\\s*Forwarded Message\\s*-+.*$", Pattern.CASE_INSENSITIVE),
            Pattern.compile("^From:\\s+.*$", Pattern.CASE_INSENSITIVE),
            Pattern.compile("^Sent:\\s+.*$", Pattern.CASE_INSENSITIVE),
            Pattern.compile("^To:\\s+.*$", Pattern.CASE_INSENSITIVE),
            Pattern.compile("^Subject:\\s+.*$", Pattern.CASE_INSENSITIVE),
            Pattern.compile("^Cc:\\s+.*$", Pattern.CASE_INSENSITIVE),
            Pattern.compile("^Bcc:\\s+.*$", Pattern.CASE_INSENSITIVE),
            Pattern.compile("^From my iPhone\\s*$", Pattern.CASE_INSENSITIVE),
            Pattern.compile("^Sent from my iPhone\\s*$", Pattern.CASE_INSENSITIVE),
            Pattern.compile("^External Email.*$", Pattern.CASE_INSENSITIVE),
            Pattern.compile("^Need help\\?\\s*Click for assistance\\s*$", Pattern.CASE_INSENSITIVE),
            Pattern.compile("^Confidentiality Notice:\\s*$", Pattern.CASE_INSENSITIVE)
        );

        // Remove noise lines, such as "From:", "Sent:", "To:", "Subject:", etc.
        for (Pattern noiseLinePattern : NOISE_LINE_PATTERNS) {
            body = noiseLinePattern.matcher(body).replaceAll("");
        }
        return body;
    }

    // Remove the On_DATE_PATTERN followed by WROTE_PATTERN,
    // looping through lines to handle cases where there are multiple reply markers in the email body.
    private String removeReplyMarkers(String body) {

        // Patterns to identify single-line and two-line email reply markers.
        Pattern REPLY_MARKER_PATTERN = Pattern.compile("^On .+ wrote:\\s*$", Pattern.CASE_INSENSITIVE);
        Pattern ON_DATE_PATTERN = Pattern.compile("^On\\s.+$", Pattern.CASE_INSENSITIVE);
        Pattern WROTE_PATTERN = Pattern.compile("^wrote:\\s*$", Pattern.CASE_INSENSITIVE);

        int i = 0;

        List<String> lines = Arrays.asList(body.split("\\n"));
        List<String> cleanedLines = new ArrayList<>();

        // Loop through lines and skip reply markers
        while (i < lines.size()) {

            // Check for reply markers like "On [date], [name] wrote:"

            if (i + 1 <= lines.size() - 1 &&

                ON_DATE_PATTERN.matcher(lines.get(i)).find() && WROTE_PATTERN.matcher(lines.get(i + 1)).find()) {
                i += 2; // Skip the "On [date]" line and the following "wrote:" line
    
            } else if (REPLY_MARKER_PATTERN.matcher(lines.get(i)).find()) {
    
                i++; // Skip the "On [date]" followed by "wrote:"
    
            } else {
    
                cleanedLines.add(lines.get(i));
                i++;

            }
        }
    
        // Join the cleaned lines back into a single string
        body = String.join("\n", cleanedLines);
    
        return body;
    }
}