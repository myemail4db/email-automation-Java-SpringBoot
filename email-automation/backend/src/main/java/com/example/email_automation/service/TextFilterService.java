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
        body = body.replace("\r\n", "\n") // Handle Windows-style line breaks
                   .replace("\r", "\n");   // Handle old Mac-style line breaks

        System.out.println("""
                           //////////////////////////////////////////////////////////// \n
                           After normalizing line breaks: \n
                           //////////////////////////////////////////////////////////// \n
                           """ +
                            body); // --- IGNORE ---


        // 2. Remove Reply Markers like "On [date], [name] wrote:" and similar patterns, 
        body = removeReplyMarkers(body);

        System.out.println("""
                           //////////////////////////////////////////////////////////// \n
                           After removing reply markers: \n
                           //////////////////////////////////////////////////////////// \n
                           """ +
                            body); // --- IGNORE ---

        // 3. Remove multi-line blocks to remove, such as legal disclaimers or survey requests.
        body = removeMultiLineBlocks(body);

        System.out.println("""
                           //////////////////////////////////////////////////////////// \n
                           After removing multi-line blocks: \n
                           //////////////////////////////////////////////////////////// \n
                           """ +
                            body); // --- IGNORE ---

        // 4. Remove Noise Lines such as "From:", "Sent:", "To:", "Subject:", etc.
        body = removeNoiseLines(body);

        System.out.println("""
                           //////////////////////////////////////////////////////////// \n
                           After removing noise lines: \n
                           //////////////////////////////////////////////////////////// \n
                           """ +
                            body); // --- IGNORE ---
        body = removeUnicodeCharacters(body);

        System.out.println("""
                           //////////////////////////////////////////////////////////// \n
                           After removing non-printable characters: \n
                           //////////////////////////////////////////////////////////// \n
                           """ +
                            body); // --- IGNORE ---

        // 7. Remove leading and trailing whitespace
        body = body.trim(); 

        System.out.println("""
                           //////////////////////////////////////////////////////////// \n
                           After trimming whitespace: \n
                           //////////////////////////////////////////////////////////// \n
                           """ +
                            body); // --- IGNORE ---

        // 8. Normalize bullet lines and regular spaces, and collapse multiple blank lines into a single blank line.
        body = normalizeLines(body);

        System.out.println("""
                            //////////////////////////////////////////////////////////// \n
                            Normalizing lines: \n
                            //////////////////////////////////////////////////////////// \n
                            """ +
                            body); // --- IGNORE ---

        // 10. Consolidate multiple consecutive blank lines into a single blank line to improve readability.
        body = body.replaceAll("(?m)^[ \\t]+$", "");
        body = body.replaceAll("\\n\\s*\\n+", "\n\n").trim();

        System.out.println("""
                           //////////////////////////////////////////////////////////// \n
                           After consolidating blank lines: \n
                           //////////////////////////////////////////////////////////// \n
                           """ +
                            body); // --- IGNORE ---        


        return body;
    }

    private String normalizeLines(String body) {

        StringBuilder normalizedBody = new StringBuilder();

        for (String line : body.split("\\n")) {

            // 7. Trim leading and trailing whitespace from each line to ensure there are no unnecessary spaces at the beginning or end of the line.
            line = line.replaceAll("^\\s*\\|\\s*", "");

            // 8. Normalize bullet lines that start with a pipe character (|) followed by optional whitespace, ensuring they are formatted consistently.
            line = line.replaceAll("\\s*\\|\\s*$", "");

            // 9. Normalize bullet lines
            line = line.replaceAll("^\\s*-\\s+", "- ");

            // 10. Normalize regular spaces
            line = line.replaceAll("[ \\t]+", " ").trim();

            normalizedBody.append(line).append("\n");
        }

        return normalizedBody.toString();
    }

    private String removeUnicodeCharacters(String body) {

        // 5. Strip out non-printable characters that may cause issues in text processing or exporting.
        // Strips 0-8, 11-12, and 14-31, bypassing 9 (\t), 10 (\n), and 13 (\r)
        
        // Remove only private-use glyphs
        body = body.replaceAll("[\\uE000-\\uF8FF]", "");
        body = body.replace('\u00A0', ' ');

        return body;
    }

    private String removeMultiLineBlocks(String body) {

        // Multi-line blocks to remove, such as legal disclaimers or survey requests.
        List<Pattern> BLOCK_PATTERNS = Arrays.asList(
                Pattern.compile("-+\\s*Forwarded Message\\s*-+.*$", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE),
                Pattern.compile("CONFIDENTIALITY NOTICE:.*$", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE),
                Pattern.compile("How am I doing.*$", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE),
                Pattern.compile("^(CCPA|CCCPA).*$", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE),
                Pattern.compile("All qualified applicants will receive consideration for employment.*$", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE),
                Pattern.compile("To unsubscribe.*$", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE),
                Pattern.compile("unsubscribe.*$", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE),
                Pattern.compile("(equal opportunity employer|reasonable accommodation|protected veteran|characteristic protected by law).*?(?=\\n\\n|\\Z)", Pattern.CASE_INSENSITIVE),
                Pattern.compile("(confidentiality notice|privileged and confidential information).*?(?=\\n\\n|\\Z)", Pattern.CASE_INSENSITIVE),
                Pattern.compile("(privacy notice|email confidentiality and privacy).*?(?=\\n\\n|\\Z)", Pattern.CASE_INSENSITIVE),
                Pattern.compile("Confidentiality Notice:.*?(?=\\n\\n|\\Z)", Pattern.CASE_INSENSITIVE),
                Pattern.compile("The information contained in this message.*?(?=\\n\\n|\\Z)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
                Pattern.compile("If you have any.*?(?=\\n\\n|\\Z)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
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

            // Single-line patterns (Pattern.MULTILINE)
            Pattern.compile("^From:\\s+.*$", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE),
            Pattern.compile("^Sent:\\s+.*$", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE),
            Pattern.compile("^To:\\s+.*$", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE),
            Pattern.compile("^Subject:\\s+.*$", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE),
            Pattern.compile("^Cc:\\s+.*$", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE),
            Pattern.compile("^Bcc:\\s+.*$", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE),
            Pattern.compile("^From my iPhone\\s*$", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE),
            Pattern.compile("^Sent from my iPhone\\s*$", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE),

            // Multi-line patterns (Pattern.Dotall)
            Pattern.compile("^Sent from Yahoo Mail.*?(?=\\n\\n|\\Z)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("^Get Outlook for.*?(?=\\n\\n|\\Z)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("^CAUTION:.*?(?=\\n\\n|\\Z)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("^WARNING:.*?(?=\\n\\n|\\Z)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("^-+Original Message-+.*?(?=\\n\\n|\\Z)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("^Begin forwarded message:\\s*.*?(?=\\n\\n|\\Z)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("^-+\\s*Forwarded Message\\s*-+.*?(?=\\n\\n|\\Z)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("^External Email.*?(?=\\n\\n|\\Z)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("^Need help\\?\\s*Click for assistance\\s*.*?(?=\\n\\n|\\Z)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("^Confidentiality Notice:\\s*.*?(?=\\n\\n|\\Z)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),

            // Patterns to identify multi-line blocks that start with a specific marker and continue until the end of the line or until a character is seen.
            Pattern.compile("\\s*\\|\\s*$", Pattern.MULTILINE)
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