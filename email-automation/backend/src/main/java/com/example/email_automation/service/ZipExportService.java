package com.example.email_automation.service;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ZipExportService {
    
    private static final Logger logger = LoggerFactory.getLogger(ZipExportService.class);

    @Value("${email.export.output-dir}")
    private String sourceDirPath;

    @Value("${email.zip.export-dir}")
    private String zipDirPath;

    public boolean createZipEmails(String format) {

        String extension;
        
        if (format.equals("text")) {
            extension = ".txt";
        } else if(format.equals("word")) {
            extension = ".docx";
        } else {
            return false;
        } 

        try {

            // Check if the zipDirPath exists; if it doesn't exist, create it
            createDestinationPath(zipDirPath);

            // create the zip filename with path
            Path zipFile = createZipPath(Path.of(zipDirPath), format);
            
            return addFiles(zipFile, extension);

        } catch (IOException e) {
            logger.error("Failed to create ZIP export.", e);
            return false;
        }

    }

    private void createDestinationPath(String zipDirectoryPath) throws IOException {
        Files.createDirectories(Path.of(zipDirectoryPath));
    }

    private Path createZipPath(Path sourceDirectory, String format) throws IOException {

        String timestamp = formatReceivedDateForFilename();
        String zipFileName = "exported_emails_" + format + "_" + timestamp + ".zip";
        Path zipFilePath = sourceDirectory.resolve(zipFileName);
        
        // if zip file already exists, add a _number suffix to avoid overwriting
        int counter = 1;
        while (Files.exists(zipFilePath)) {
            zipFileName = "exported_emails_" + format + "_" + timestamp + "_" + counter + ".zip";

            zipFilePath = sourceDirectory.resolve(zipFileName);
            counter++;
        }

        return zipFilePath;
    }

    private boolean addFiles(Path zipFile, String extension) {

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Path.of(sourceDirPath))) {

            try (OutputStream os = Files.newOutputStream(zipFile, StandardOpenOption.CREATE);
                 ZipOutputStream zos = new ZipOutputStream(os);) {
                
                for (Path file : stream) {
                    
                    if (!Files.isRegularFile(file)) {
                        continue;
                    }
                    
                    if (!file.getFileName().toString().toLowerCase().endsWith(extension)) {
                        continue;
                    }
                    
                    // Create a new entry inside the ZIP archive
                    ZipEntry zipEntry = new ZipEntry(file.getFileName().toString());

                    // Add the file into the zip file
                    zos.putNextEntry(zipEntry);
                    
                    // Copy file content to the ZIP stream
                    Files.copy(file, zos);
                    
                    logger.info("files selected to be zipped: {}", file);
                    //logger.info("ZIP export completed: {}", file);
                    
                    // Close the current entry
                    zos.closeEntry();
                    
                }

                return true;

            }
            } catch (IOException e) {
                logger.error("Failed to create ZIP export.", e);
                return false;
            }
    }

    //String date = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

    // Format the email received date for filename
    // Example input: "Wed, 2 Aug 2023 15:04:05"
    // Desired output: "20230802_150405"
    private String formatReceivedDateForFilename() {

        // Convert "Wed, 2 Aug 2023 15:04:05 +0000" to "20230802_150405"ß
        try {

            LocalDateTime receivedDate = LocalDateTime.now();

            // // Input formatter (defines how to read the string)
            // DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("EEE, d MMM yyyy HH:mm:ss a", Locale.ENGLISH);
            
            // Output formatter (defines how you want the string to look)
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            
            // Parse and format
            //ZonedDateTime zonedDateTime = ZonedDateTime.parse(receivedDate, inputFormatter);
            //String result = zonedDateTime.format(outputFormatter);
            
            // LocalDateTime localDateTime = LocalDateTime.parse(receivedDate, inputFormatter);
            // Outputs: 20230802_150405
            String result = receivedDate.format(outputFormatter);

            logger.debug("Generated ZIP timestamp: {}", result); 

            return result;

        } catch (Exception e) {
            logger.error("Unable to format ZIP timestamp.", e);
            return "unknown_date";
        }

    }
}
