package com.example.email_automation.service;

import java.util.zip.ZipFile;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import org.springframework.test.util.ReflectionTestUtils;

class ZipExportServiceTest {

    @TempDir
    Path tempDirectory;

    @Test
    void shouldArchiveSuccessfullySentZip() throws Exception {

        Path readyToSendDirectory = tempDirectory.resolve("ready_to_send");
        Path zipArchiveDirectory = tempDirectory.resolve("sent_archive_zip");

        Files.createDirectories(readyToSendDirectory);

        Path zipFile = readyToSendDirectory.resolve("job_batch_test.zip");
        Files.writeString(zipFile, "test zip content");

        ZipExportService zipExportService = new ZipExportService();

        ReflectionTestUtils.setField(
                zipExportService,
                "zipArchiveDirectory",
                zipArchiveDirectory.toString());

        zipExportService.archiveZipFile(zipFile);

        assertFalse(Files.exists(zipFile));

        assertTrue(
                Files.exists(
                        zipArchiveDirectory.resolve("job_batch_test.zip")));
    }

    @Test
    void shouldExcludeFilesWithoutGmailIdFromZip() throws Exception {

        Path processedDirectory = tempDirectory.resolve("processed_review");
        Path readyToSendDirectory = tempDirectory.resolve("ready_to_send");

        Files.createDirectories(processedDirectory);
        Files.createDirectories(readyToSendDirectory);

        Path validFile = processedDirectory.resolve("Software_Engineer_GMAIL-abc123.txt");
        Path invalidFile = processedDirectory.resolve("Manual_Notes.txt");

        Files.writeString(validFile, "Software_Engineer_GMAIL-abc123.txt");
        Files.writeString(invalidFile, "Manual notes for review");

        ZipExportService zipExportService = new ZipExportService();

        ReflectionTestUtils.setField(
                zipExportService,
                "zipFilesZipDir",
                readyToSendDirectory.toString());

        ReflectionTestUtils.setField(
                zipExportService,
                "emailFilesProcessedDir",
                processedDirectory.toString());

        Path zipFile = zipExportService.createZipEmail("text");

        assertTrue(Files.exists(zipFile));

        try (ZipFile zip = new ZipFile(zipFile.toFile())) {

            assertTrue(zip.getEntry("Software_Engineer_GMAIL-abc123.txt") != null);
            assertFalse(zip.getEntry("Manual_Notes.txt") != null);
        }
    }
}