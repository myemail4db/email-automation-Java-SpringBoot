package com.example.email_automation.service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;

import com.example.email_automation.model.EmailMessage;

@Service
public class FileExportService {

    //private final BatchSendService batchSendService;

    // FileExportService(BatchSendService batchSendService) {
    //     this.batchSendService = batchSendService;
    // }

    FileExportService() {}

    public String saveFile(EmailMessage content, String format) {

        String extension;
        format = format.toLowerCase();

        if (format.equals("text")) {
            extension = ".txt";
        } else if (format.equals("word")) {
            extension = ".docx";
        } else {
            return "Unsupported format: " + format;
        }

        // Clean the subject to create a safe filename
        String filenameString = createSafeFilename(content.getSubject());

            String directory = "processed_review/";
            Path dirPath = Paths.get(directory);
            Path pathFilename = dirPath.resolve(filenameString + extension);

        try {
            // Check if the directory exists, and if not, create it
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
                System.out.println(directory + " created.");
            }

            // Check if the file already exists, and if so, append a number to the filename until we find a unique name
            int count = 1;
            while (Files.exists(pathFilename)) {

                // If the file already exists, append a number to the filename
                pathFilename = dirPath.resolve(filenameString + "_" + count + extension);
                count++;
            }

            String header = "============================================================\n" +
                            "EMAIL DETAILS\n" +
                            "============================================================\n" +
                            "Subject           : " + content.getSubject() + "\n" +
                            "From              : " + content.getFrom() + "\n" +
                            "Date              : " + content.getReceivedDate() + "\n" +
                            "Workflow Status   : " + "processed_review\n" +
                            "Export Format     : " + format + "\n" +
                            "File Name         : " + pathFilename.getFileName() + "\n" +
                            "============================================================\n\n";

            try (BufferedWriter writer = Files.newBufferedWriter(pathFilename)) {
                writer.write(header);
                writer.write(content.getBody());
            }

            System.out.println(pathFilename.getFileName() + " created.");

        } catch (IOException e) {
            e.printStackTrace();
        }

        // For now, just return a message. In the future, implement actual file saving logic.
        return pathFilename.getFileName().toString();
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
}
