package com.example.email_automation.service;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ArchiveService {

    private static final Logger logger = LoggerFactory.getLogger(ArchiveService.class);

    @Value("${email.files.processed-dir}")
    private String processedDirectory;

    @Value("${email.files.archive-dir}")
    private String archiveDirectory;

    private void createArchiveDirectory() throws IOException {

        Path archivePath = Path.of(archiveDirectory);

        if (!Files.isDirectory(archivePath)) {
            Files.createDirectories(archivePath);
        }
    }

    public int archiveFiles(String format) {

        String extension;

        if ("text".equalsIgnoreCase(format)) {
            extension = ".txt";
        } else if ("word".equalsIgnoreCase(format)) {
            extension = ".docx";
        } else {
            return -1;
        }

        try {
            createArchiveDirectory();

            Path processedPath = Path.of(processedDirectory);

            int filesArchived = 0;

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(processedPath)) {

                for (Path file : stream) {

                    if (!Files.isRegularFile(file)) {
                        continue;
                    }

                    if (!file.getFileName().toString().toLowerCase().endsWith(extension)) {
                        continue;
                    }

                    Path destination = Path.of(archiveDirectory)
                            .resolve(file.getFileName());

                    Files.move(file, destination);

                    filesArchived++;
                }
            }

            return filesArchived;

        } catch (IOException e) {
            logger.error("Failed to archive processed files.", e);
            return -1;
        }
    }
}