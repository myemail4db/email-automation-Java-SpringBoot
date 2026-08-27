package com.example.email_automation.service;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.email_automation.model.EmailMessage;

@Service
public class FileExportService {

    private static final Logger logger = LoggerFactory.getLogger(FileExportService.class);

    @Value("${email.files.processed-dir}")
    private String emailFilesProcessedDir;

    public boolean saveFile(EmailMessage content, String format) {

        if (content == null || content.getSubject() == null || content.getBody() == null) {
            logger.error("EmailMessage content is null or missing required fields.");
            throw new IllegalArgumentException("EmailMessage content is null or missing required fields.");
        }

        if (format.equalsIgnoreCase("text")) {
            
            return saveTextFile(content, format);

        } else if (format.equalsIgnoreCase("word")) {

            return saveWordFile(content, format);

        } else {

            logger.error("Unsupported format: {}. Supported formats are 'text' and 'word';.", format);
            throw new IllegalArgumentException("Unsupported format: " + format + ". Supported formats are 'text' and 'word'.");
        }
    }

    private boolean saveTextFile(EmailMessage content, String format) {

        boolean isSaved = false;

        try {
            // Create a safe filename based on the email subject
            String filenameString = createSafeFilename(content.getSubject());
            
            // Create the file path with the appropriate extension
            // Possibly throws IOException
            Path pathFilename = createFilePath(filenameString, format);
            
            // Create the header for the text file
            // Possibly throws IOException
            String header = createTextHeader(content, format, pathFilename);

            try (BufferedWriter writer = Files.newBufferedWriter(pathFilename)) {
                writer.write(header);
                writer.write(content.getBody());
                isSaved = true;
            }

            if (isSaved) {

                logger.info("Created {}.", pathFilename.getFileName());
                return true;

            } else {

                logger.error("Failed to create {}.", pathFilename.getFileName());
                return false;

            }

        } catch (IOException e) {
            logger.error("IOException occurred while saving text file: {}", e);
            throw new RuntimeException("IOException occurred while saving text file: " + e.getMessage(), e);
        }

    }

    private boolean saveWordFile(EmailMessage content, String format) {
        
        boolean isSaved = false;
            
        // Create and format the DOCX file

        // Create a safe filename based on the email subject
        String filenameString = createSafeFilename(content.getSubject());

        try {            
            // Create the file path with the appropriate extension
            // Possibly throws IOException
            Path pathFilename = createFilePath(filenameString, format);          
        
            try (XWPFDocument document = new XWPFDocument();
                 FileOutputStream out = new FileOutputStream(pathFilename.toFile())) {

                // Header Block
                addWordHeader(document, content, pathFilename, format);
                
                // Body Block
                addWordBody(document, content);

                // Save the document
                document.write(out);
                logger.info("Email saved successfully to {}.", pathFilename);
                isSaved = true;
            }

            if (isSaved) {

                logger.info("Created {}.", pathFilename.getFileName());
                return true;                

            } else {

                logger.error("Failed to create {}.", pathFilename.getFileName());
                return false;

            }
        } catch (IOException e) {
            logger.error("IOException occurred while saving Word file: {}", e);
            throw new RuntimeException("IOException occurred while saving Word file: " + e.getMessage(), e);
        }

    }
  
    private void addWordBody(XWPFDocument document, EmailMessage content) {

        XWPFParagraph paragraph = createWordParagraph(document);

        XWPFRun bodyRun = createWordLabelRun(paragraph);

        String[] lines = content.getBody().split("\\R", -1);

        for (int i = 0; i < lines.length; i++) {

            if (!lines[i].isBlank()) {
                bodyRun.setText(lines[i]);
            }

            if (i < lines.length - 1) {
                bodyRun.addBreak();
            }
        }
    }

    private String createTextHeader(EmailMessage content, String format, Path pathFilename) {

        String header = "============================================================\n" +
                "EMAIL DETAILS\n" +
                "============================================================\n" +
                "Subject           : " + content.getSubject() + "\n" +
                "From              : " + content.getFrom() + "\n" +
                "Date              : " + content.getReceivedDate() + "\n" +
                /**"Workflow Status   : " + gmailSuccessfulLabel + "\n" + */
                "Export Format     : " + format + "\n" +
                "File Name         : " + pathFilename.getFileName() + "\n" +
                "============================================================\n\n";
        return header;
    }

    private XWPFParagraph createWordParagraph(XWPFDocument document) {

        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setSpacingBefore(0);
        paragraph.setSpacingAfter(0);

        return paragraph;
    }

    private XWPFRun createWordLabelRun(XWPFParagraph paragraph) {

        XWPFRun labelRun = paragraph.createRun();
        labelRun.setFontFamily("Courier New");
        labelRun.setFontSize(10);

        return labelRun;
    }

    /**
     * Helper to add a nicely formatted metadata header to the memo.
     */
    private void addWordHeader(XWPFDocument document, EmailMessage content, Path pathFilename, String format) {

        addWordHeaderTitle(document);
        addWordHeaderLine(document, "Subject", content.getSubject());
        addWordHeaderLine(document, "From", content.getFrom());
        addWordHeaderLine(document, "Date", content.getReceivedDate());
        /** addWordHeaderLine(document, "Workflow Status", gmailSuccessfulLabel); */
        addWordHeaderLine(document, "Export Format", format);
        addWordHeaderLine(document, "File Name", pathFilename.getFileName().toString());
        addWordSeparator(document);

    }

    private void addWordHeaderLine(XWPFDocument document, String label, String value) {

        XWPFParagraph paragraph = createWordParagraph(document);

        XWPFRun labelRun = createWordLabelRun(paragraph);
        labelRun.setBold(true);
        labelRun.setText(String.format("%-18s: ", label));

        XWPFRun valueRun = createWordLabelRun(paragraph);
        valueRun.setText(value);

    }

    private void addWordHeaderTitle(XWPFDocument document) {

        XWPFParagraph paragraph = createWordParagraph(document);
        XWPFRun labelRun = createWordLabelRun(paragraph);
        labelRun.setBold(true);
        labelRun.setText("============================================================");

        paragraph = createWordParagraph(document);
        labelRun = createWordLabelRun(paragraph);
        labelRun.setBold(true);
        labelRun.setText("EMAIL DETAILS");

        paragraph = createWordParagraph(document);
        labelRun = createWordLabelRun(paragraph);
        labelRun.setBold(true);
        labelRun.setText("============================================================");

    }


    private void addWordSeparator(XWPFDocument document) {

        XWPFParagraph paragraph = createWordParagraph(document);
        XWPFRun labelRun = createWordLabelRun(paragraph);
        labelRun.setBold(true);
        labelRun.setText("============================================================");

    }

    private String createSafeFilename(String input) {

        // Remove non-alphanumeric characters
        input = input.replaceAll("[^a-zA-Z0-9\\s_]", ""); 
        
        // Remove leading and trailing spaces
        input = input.trim();

         // Replace multiple spaces with a single space
        input = input.replaceAll("\\s+", " "); 
        return input;
    }

    private Path createFilePath(String filenameString, String format) throws IOException {

        String extension;
        format = format.toLowerCase();

        // Determine the file extension based on the format
        if (format.equals("text")) {
            extension = ".txt";
        } else if (format.equals("word")) {
            extension = ".docx";
        } else {
            throw new IllegalArgumentException("Unsupported format: " + format);
        }

        // Check if the directory exists, and if not, create it
        Path directoryPath = Path.of(emailFilesProcessedDir);

        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }

        // Check if the file already exists, and if so, append a number to the filename until we find a unique name
        Path pathFilename = directoryPath.resolve(filenameString + extension);

        int count = 1;

        while (Files.exists(pathFilename)) {

            pathFilename =
                    directoryPath.resolve(filenameString + "_" + count + extension);

            count++;
        }

        return pathFilename;
    }
}
