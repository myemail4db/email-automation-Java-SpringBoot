package com.example.email_automation.service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.email_automation.model.EmailMessage;

@Service
public class FileExportService {

    private static final Logger logger = LoggerFactory.getLogger(FileExportService.class);

    private final String exportDirectory = "processed_review/";

    public String getExportDirectory() {
        return exportDirectory;
    }

    public boolean saveFile(EmailMessage content, String format) {

        try {
            String filenameString = createSafeFilename(content.getSubject());
            Path pathFilename = createFilePath(filenameString, format);
            String header = createHeader(content, format, pathFilename);
            boolean isSaved = writeToFile(pathFilename, header, content);

            if (isSaved) {
                System.out.println("Created " + pathFilename.getFileName() + ".");
                logger.info("Created {}.", pathFilename.getFileName());
            } else {
                System.out.println("Failed to create " + pathFilename.getFileName() + ".");
                logger.error("Failed to create {}.", pathFilename.getFileName());
            }

            return isSaved;
        } catch (IOException e) {
            e.printStackTrace(); 
            logger.error("IOException occurred while saving file: {}", e.getMessage());
            return false;
        }
    }

    private boolean writeToFile(Path pathFilename, String header, EmailMessage content) {

        try (BufferedWriter writer = Files.newBufferedWriter(pathFilename)) {
            writer.write(header);
            writer.write(content.getBody());
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("IOException occurred while writing to file: {}", e.getMessage());
            return false;
        }
        return true;
    }

    private String createHeader(EmailMessage content, String format, Path pathFilename) {

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

        try {
            // Check if the directory exists, and if not, create it
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
                //System.out.println(exportDirectory + " created.");
            }

            // Check if the file already exists, and if so, append a number to the filename until we find a unique name
            int count = 1;
            while (Files.exists(pathFilename)) {

                // If the file already exists, append a number to the filename
                pathFilename = dirPath.resolve(filenameString + "_" + count + extension);
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("IOException occurred while creating file path: {}", e.getMessage());
        }

        return pathFilename;
    }
}
