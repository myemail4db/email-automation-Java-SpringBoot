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
    
    private static final Logger logger = 
        LoggerFactory.getLogger(ZipExportService.class);

    @Value("${email.files.processed-dir}")
    private String emailFilesProcessedDir;

    @Value("${email.files.zip-dir}")
    private String zipFilesZipDir;

    @Value("${email.files.zip-prefix}")
    private String zipFilesZipPrefix;

    public Path createZipEmail(String format) {

        String extension;
        
        if (format.equals("text")) {
            extension = ".txt";
        } else if(format.equals("word")) {
            extension = ".docx";
        } else {
            return null;
        } 

        try {

            // Check if the zipDirPath exists; if it doesn't exist, create it
            createDestinationPath(zipFilesZipDir);

            // create the zip filename with path
            Path zipFile = createZipPath(Path.of(zipFilesZipDir), format);
            
            boolean zipCreated = addFiles(zipFile, extension);

            if (zipCreated) {
                return zipFile;
            }

            Files.deleteIfExists(zipFile);
            return null;

        } catch (IOException e) {
            logger.error("Failed to create ZIP export.", e);
            return null;
        }

    }

    private void createDestinationPath(String zipDirectoryPath) throws IOException {
        Files.createDirectories(Path.of(zipDirectoryPath));
    }

    private Path createZipPath(Path sourceDirectory, String format) throws IOException {

        String timestamp = formatReceivedDateForFilename();
        String zipFileName = zipFilesZipPrefix + "_" + format + "_" + timestamp + ".zip";
        Path zipFilePath = sourceDirectory.resolve(zipFileName);
        
        // if zip file already exists, add a _number suffix to avoid overwriting
        int counter = 1;
        while (Files.exists(zipFilePath)) {
            zipFileName = zipFilesZipPrefix + "_" + format + "_" + timestamp + "_" + counter + ".zip";

            zipFilePath = sourceDirectory.resolve(zipFileName);
            counter++;
        }

        return zipFilePath;
    }

    private boolean addFiles(Path zipFile, String extension) {

        // Open the directory stream to read files from the processed directory
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Path.of(emailFilesProcessedDir))) {

            // Create the ZIP output stream to write to the zip file
            try (OutputStream os = Files.newOutputStream(zipFile, StandardOpenOption.CREATE);
                 ZipOutputStream zos = new ZipOutputStream(os);) {
                
                int filesAdded = 0;

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
                    filesAdded++;
                    
                }

                return filesAdded > 0;

            } catch (IOException e) {
                logger.error("Failed to create ZIP export.", e);
                return false;
            }

        } catch (IOException e) {
            logger.error(
                    "Failed to access processed files directory: {}",
                    emailFilesProcessedDir,
                    e);
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
           
            // Output formatter (defines how you want the string to look)
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
                       
            // LocalDateTime localDateTime = LocalDateTime.parse(receivedDate, inputFormatter);
            // Outputs: 20230802_150405
            String result = receivedDate.format(outputFormatter);

            return result;

        } catch (Exception e) {
            logger.error("Unable to format ZIP timestamp.", e);
            return "unknown_date";
        }

    }
}
