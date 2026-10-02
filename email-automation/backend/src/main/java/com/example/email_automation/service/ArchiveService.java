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

    @Value("${email.files.duplicate-dir}")
    private String duplicateDirectory;

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

    private String extractGmailId(Path file) {

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

    private boolean isDuplicateFile(Path file) throws IOException {

        String gmailId = extractGmailId(file);

        if (gmailId == null) {
            return false;
        }

        Path archivePath = Path.of(archiveDirectory);

        if (!Files.isDirectory(archivePath)) {
            return false;
        }

        try (DirectoryStream<Path> stream =
                Files.newDirectoryStream(archivePath)) {

            for (Path archivedFile : stream) {

                if (!Files.isRegularFile(archivedFile)) {
                    continue;
                }

                String archivedGmailId = extractGmailId(archivedFile);

                if (gmailId.equals(archivedGmailId)) {
                    return true;
                }
            }
        }

        return false;
    }

    private void archiveDuplicateFile(Path file) throws IOException {

        Path duplicatePath = Path.of(duplicateDirectory);

        Files.createDirectories(duplicatePath);

        String fileName = file.getFileName().toString();

        int extensionIndex = fileName.lastIndexOf('.');

        String baseName;
        String extension;

        if (extensionIndex != -1) {
            baseName = fileName.substring(0, extensionIndex);
            extension = fileName.substring(extensionIndex);
        } else {
            baseName = fileName;
            extension = "";
        }

        Path destination = duplicatePath.resolve(fileName);

        int duplicateNumber = 2;

        while (Files.exists(destination)) {

            destination = duplicatePath.resolve(
                    baseName + "_" + duplicateNumber + extension
            );

            duplicateNumber++;
        }

        Files.move(file, destination);

        logger.info(
                "Duplicate file moved to duplicate directory. File={}",
                destination.getFileName()
        );
    }

    public int archiveDuplicateFiles(String format) {

        logger.info("Checking processed files for previously sent duplicates. Format={}", format);

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

            int duplicatesArchived = 0;

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(processedPath)) {

                for (Path file : stream) {

                    if (!Files.isRegularFile(file)) {
                        continue;
                    }

                    if (!file.getFileName().toString().toLowerCase().endsWith(extension)) {
                        continue;
                    }

                    if (isDuplicateFile(file)) {

                        archiveDuplicateFile(file);

                        duplicatesArchived++;
                    }
                }
            }

            return duplicatesArchived;

        } catch (IOException e) {
            logger.error("Failed while checking processed files for duplicates.", e);
            return -1;
        }
    }
}