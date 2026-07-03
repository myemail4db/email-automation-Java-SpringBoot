package com.example.email_automation.service;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    @Value("${email.export.output-dir}")
    private String exportDirectory;

    public String getExportDirectory() {
        return exportDirectory;
    }

    public boolean saveFile(EmailMessage content, String format) {

        if (content == null || content.getSubject() == null || content.getBody() == null) {
            logger.error("EmailMessage content is null or missing required fields.");
            return false;
        }

        if (format.equalsIgnoreCase("text")) {
            
            return saveTextFile(content, format);

        } else if (format.equalsIgnoreCase("word")) {

            return saveWordFile(content, format);

        } else {

            logger.error("Unsupported format: {}. Supported formats are 'text' and 'word';.", format);
            return false;

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

            } else {

                logger.error("Failed to create {}.", pathFilename.getFileName());

            }

            return isSaved;

        } catch (IOException e) {
            e.printStackTrace(); 
            logger.error("IOException occurred while saving file: {}", e.getMessage());
        }

        return isSaved;
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

        } catch (IOException e) {
            e.printStackTrace();
            logger.error("IOException occurred while saving Word file: {}", e.getMessage());
        }

        return isSaved;
    }

    private void addWordBody(XWPFDocument document, EmailMessage content) {

        String[] lines = content.getBody().split("\\R", -1);

        for (String line : lines) {
            XWPFParagraph paragraph = document.createParagraph();   
            XWPFRun bodyRun = paragraph.createRun();
            bodyRun.setText(line);

            if (line.isBlank()) {
                bodyRun.addBreak();
            } else {
                
            }
        };
    }

    private String createTextHeader(EmailMessage content, String format, Path pathFilename) {

        String header = "============================================================\n" +
                "EMAIL DETAILS\n" +
                "============================================================\n" +
                "Subject           : " + content.getSubject() + "\n" +
                "From              : " + content.getFrom() + "\n" +
                "Date              : " + content.getReceivedDate() + "\n" +
                "Workflow Status   : " + exportDirectory + "\n" +
                "Export Format     : " + format + "\n" +
                "File Name         : " + pathFilename.getFileName() + "\n" +
                "============================================================\n\n";
        return header;
    }

    /**
     * Helper to add a nicely formatted metadata header to the memo.
     */
    private void addWordHeader(XWPFDocument document, EmailMessage content, Path pathFilename, String format) {

        XWPFRun labelRun;
        XWPFRun valueRun;

        XWPFParagraph paragraph = document.createParagraph();
        labelRun = paragraph.createRun();
        labelRun.setText("============================================================");
        
        paragraph = document.createParagraph();
        labelRun = paragraph.createRun();
        labelRun.setBold(true);
        labelRun.setText("Subject           : ");
        valueRun = paragraph.createRun();
        valueRun.setText(content.getSubject());
        

        paragraph = document.createParagraph();
        labelRun = paragraph.createRun();
        labelRun.setBold(true);
        labelRun.setText("From              : ");
        valueRun = paragraph.createRun();
        valueRun.setText(content.getFrom());

        paragraph = document.createParagraph();
        labelRun = paragraph.createRun();
        labelRun.setBold(true);
        labelRun.setText("Date              : ");
        valueRun = paragraph.createRun();
        valueRun.setText(content.getReceivedDate());

        paragraph = document.createParagraph();
        labelRun = paragraph.createRun();
        labelRun.setBold(true);
        labelRun.setText("Workflow Status   : ");
        valueRun = paragraph.createRun();
        valueRun.setText(getExportDirectory());

        paragraph = document.createParagraph();
        labelRun = paragraph.createRun();
        labelRun.setBold(true);
        labelRun.setText("Export Format     : ");
        valueRun = paragraph.createRun();
        valueRun.setText(format);

        paragraph = document.createParagraph();
        labelRun = paragraph.createRun();
        labelRun.setBold(true);
        labelRun.setText("File Name         : ");
        valueRun = paragraph.createRun();
        valueRun.setText(pathFilename.getFileName().toString());

        paragraph = document.createParagraph();
        labelRun = paragraph.createRun();
        labelRun.setText("============================================================");
    }

    private void addWordHeaderLine(XWPFDocument document, String label, String value) {
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun labelRun = paragraph.createRun();
        labelRun.setBold(true);
        labelRun.setText(label + " : ");
        XWPFRun valueRun = paragraph.createRun();
        valueRun.setText(value);
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

        // Clean the subject to create a safe filename    

        Path dirPath = Paths.get(exportDirectory);
        Path pathFilename = dirPath.resolve(filenameString + extension);

        // Check if the directory exists, and if not, create it
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        // Check if the file already exists, and if so, append a number to the filename until we find a unique name
        int count = 1;
        while (Files.exists(pathFilename)) {

            // If the file already exists, append a number to the filename
            pathFilename = dirPath.resolve(filenameString + "_" + count + extension);
            count++;
        }

        return pathFilename;
    }
}
