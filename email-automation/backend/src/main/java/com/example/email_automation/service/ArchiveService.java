package com.example.email_automation.service;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ArchiveService {

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

    public boolean archiveFiles(String format) {

        String extension;

        if ("text".equalsIgnoreCase(format)) {
            extension = ".txt";
        } else if ("word".equalsIgnoreCase(format)) {
            extension = ".docx";
        } else {
            return false;
        }

        try {
            createArchiveDirectory();

            Path processedPath = Path.of(processedDirectory);

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
                }
            }

            return true;

        } catch (IOException e) {
            return false;
        }
    }
}