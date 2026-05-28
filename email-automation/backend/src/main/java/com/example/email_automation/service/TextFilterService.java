package com.example.email_automation.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

/**
 * This service cleans the raw email
 */

@Service
public class TextFilterService {

    private static final List<Pattern> REPLY_MARKER_PATTERNS = List.of(
            Pattern.compile("^On .+ wrote:\\s*$", Pattern.CASE_INSENSITIVE)
    );

    private static final List<Pattern> NOISE_LINE_PATTERNS = List.of(
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

    private static final List<Pattern> BLOCK_PATTERNS = List.of(
            Pattern.compile("CONFIDENTIALITY NOTICE:.*?(?=\\n\\n|\\z)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
            Pattern.compile("How am I doing\\?.*?(?=\\n\\n|\\z)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
            Pattern.compile("(CCPA|CCCPA)\\s+Privacy Notice.*?(?=\\n\\n|\\z)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
            Pattern.compile("All qualified applicants will receive consideration for employment.*?(?=\\n\\n|\\z)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
            Pattern.compile("To unsubscribe.*?(?=\\n\\n|\\z)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
            Pattern.compile("unsubscribe.*?(?=\\n\\n|\\z)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
            Pattern.compile("(equal opportunity employer|reasonable accommodation|protected veteran|characteristic protected by law).*?(?=\\n\\n|\\z)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
            Pattern.compile("(confidentiality notice|privileged and confidential information).*?(?=\\n\\n|\\z)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
            Pattern.compile("(privacy notice|email confidentiality and privacy).*?(?=\\n\\n|\\z)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
            Pattern.compile("The information contained in this message may be privileged and confidential and protected from disclosure.*?(?=\\n\\n|\\z)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
    );

    public String clean(String body) {
        if (body == null || body.isBlank()) {
            return "";
        }

        String text = body
                .replace("&nbsp;", " ")
                .replace("\r\n", "\n")
                .replace("\r", "\n");

        text = removePrivateUnicode(text);

        List<String> cleanedLines = new ArrayList<>();

        for (String line : text.split("\n")) {
            String stripped = line.trim();

            if (matchesAny(stripped, REPLY_MARKER_PATTERNS)) {
                continue;
            }

            if (matchesAny(stripped, NOISE_LINE_PATTERNS)) {
                continue;
            }

            cleanedLines.add(line);
        }

        text = String.join("\n", cleanedLines);
        text = collapseBlankLines(text);

        for (Pattern pattern : BLOCK_PATTERNS) {
            text = pattern.matcher(text).replaceAll("");
        }

        text = normalizeLines(text);
        text = text.replaceAll("\\n\\s*-\\s*", "\n- ");
        text = collapseBlankLines(text);

        return text.trim();
    }

    private boolean matchesAny(String text, List<Pattern> patterns) {
        return patterns.stream().anyMatch(pattern -> pattern.matcher(text).matches());
    }

    private String removePrivateUnicode(String text) {
        return text.replaceAll("[\\uE000-\\uF8FF]", "");
    }

    private String collapseBlankLines(String text) {
        return text.replaceAll("\\n(?:\\s*\\n)+", "\n\n");
    }

    private String normalizeLines(String text) {
        return Arrays.stream(text.split("\n"))
                .map(line -> line
                        .replace("\u00A0", " ")
                        .replace("\u2007", " ")
                        .replace("\u202F", " ")
                        .replace("\u200B", "")
                        .replace("\u200C", "")
                        .replace("\u200D", "")
                        .replace("\uFEFF", "")
                        .replace("   ", " ")
                        .replace(" :", ":")
                        .replace(" ,", ",")
                        .replace("::", " ")
                        .replace("!!", "!")
                        .replace("Job Title", "Title")
                        .replace("Job Location", "Location")
                        .replace("Job Type", "Type")
                        .replace("Full stack", "Full Stack")
                        .replace("Best Regards", "Best regards")
                        .replace("Thanks & Regards", "\nThanks and regards")
                        .replaceAll("[ \\t]+", " ")
                        .replaceAll("^[|\\s]+", "")
                        .trim()
                )
                .filter(line -> !line.matches("[|_\\-=~ ]+"))
                .collect(Collectors.joining("\n"));
    }
}