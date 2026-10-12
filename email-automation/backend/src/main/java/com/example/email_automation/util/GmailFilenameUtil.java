package com.example.email_automation.util;

import java.nio.file.Path;

public class GmailFilenameUtil {

    public static String extractGmailId(Path file) {

        String fileName = file.getFileName().toString();

        int gmailIndex = fileName.indexOf("_GMAIL-");

        if (gmailIndex == -1) {
            return null;
        }

        int idStart = gmailIndex + "_GMAIL-".length();
        int idEnd = fileName.indexOf('_', idStart);

        if (idEnd == -1) {
            idEnd = fileName.lastIndexOf('.');
        }

        if (idEnd == -1 || idEnd <= idStart) {
            return null;
        }

        return fileName.substring(idStart, idEnd);
    }

}
