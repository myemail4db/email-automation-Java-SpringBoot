package com.example.email_automation.service;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

class ArchiveServiceTest {

    @Value("${email.files.processed-dir}")
    private String processedDirectory;

    @Value("${email.files.archive-dir}")
    private String archiveDirectory;

    @TempDir
    Path tempDirectory;

    @Test
    void archiveServiceTestSetup() {
    }

    @Test
    void shouldDetectDuplicateWhenGmailIdsMatch() throws Exception {

        Path processedDirectory = tempDirectory.resolve("processed_review");
        Path archiveDirectory = tempDirectory.resolve("sent_archive");
        Path duplicateDirectory = tempDirectory.resolve("duplicate");

        Files.createDirectories(processedDirectory);
        Files.createDirectories(archiveDirectory);
        Files.createDirectories(duplicateDirectory);

        Files.writeString(
                processedDirectory.resolve("New Job_GMAIL-ABC123.txt"),
                "new file content"
        );

        Files.writeString(
                archiveDirectory.resolve("Old Job_GMAIL-ABC123.txt"),
                "completely different archived content"
        );

        ArchiveService archiveService = new ArchiveService();

        ReflectionTestUtils.setField(
                archiveService,
                "processedDirectory",
                processedDirectory.toString()
        );

        ReflectionTestUtils.setField(
                archiveService,
                "archiveDirectory",
                archiveDirectory.toString()
        );

        ReflectionTestUtils.setField(
                archiveService,
                "duplicateDirectory",
                duplicateDirectory.toString()
        );

        int duplicates = archiveService.archiveDuplicateFiles("text");

        assertEquals(1, duplicates);
        assertTrue(
                Files.exists(
                        duplicateDirectory.resolve("New Job_GMAIL-ABC123.txt")
                )
        );

        assertFalse(
                Files.exists(
                        processedDirectory.resolve("New Job_GMAIL-ABC123.txt")
                )
        );
    }

    @Test
    void shouldNumberDuplicateFileWhenDuplicateFilenameAlreadyExists() throws Exception {

        Path processedDirectory = tempDirectory.resolve("processed_review");
        Path archiveDirectory = tempDirectory.resolve("sent_archive");
        Path duplicateDirectory = tempDirectory.resolve("duplicate");

        Files.createDirectories(processedDirectory);
        Files.createDirectories(archiveDirectory);
        Files.createDirectories(duplicateDirectory);

        Files.writeString(
                processedDirectory.resolve("Job_GMAIL-ABC123.txt"),
                "new duplicate content"
        );

        Files.writeString(
                archiveDirectory.resolve("Original Job_GMAIL-ABC123.txt"),
                "previously sent content"
        );

        Files.writeString(
                duplicateDirectory.resolve("Job_GMAIL-ABC123.txt"),
                "previous duplicate content"
        );

        ArchiveService archiveService = new ArchiveService();

        ReflectionTestUtils.setField(
                archiveService,
                "processedDirectory",
                processedDirectory.toString()
        );

        ReflectionTestUtils.setField(
                archiveService,
                "archiveDirectory",
                archiveDirectory.toString()
        );

        ReflectionTestUtils.setField(
                archiveService,
                "duplicateDirectory",
                duplicateDirectory.toString()
        );

        int duplicates = archiveService.archiveDuplicateFiles("text");

        assertEquals(1, duplicates);

        assertTrue(
                Files.exists(
                        duplicateDirectory.resolve("Job_GMAIL-ABC123_2.txt")
                )
        );
    }

    @Test
    void shouldNotDetectDuplicateWhenGmailIdsAreDifferent() throws Exception {

        Path processedDirectory = tempDirectory.resolve("processed_review");
        Path archiveDirectory = tempDirectory.resolve("sent_archive");
        Path duplicateDirectory = tempDirectory.resolve("duplicate");

        Files.createDirectories(processedDirectory);
        Files.createDirectories(archiveDirectory);
        Files.createDirectories(duplicateDirectory);

        Files.writeString(
                processedDirectory.resolve("New Job_GMAIL-ABC123.txt"),
                "identical content"
        );

        Files.writeString(
                archiveDirectory.resolve("Old Job_GMAIL-XYZ789.txt"),
                "identical content"
        );

        ArchiveService archiveService = new ArchiveService();

        ReflectionTestUtils.setField(
                archiveService,
                "processedDirectory",
                processedDirectory.toString()
        );

        ReflectionTestUtils.setField(
                archiveService,
                "archiveDirectory",
                archiveDirectory.toString()
        );

        ReflectionTestUtils.setField(
                archiveService,
                "duplicateDirectory",
                duplicateDirectory.toString()
        );

        int duplicates = archiveService.archiveDuplicateFiles("text");

        assertEquals(0, duplicates);

        assertTrue(
                Files.exists(
                        processedDirectory.resolve("New Job_GMAIL-ABC123.txt")
                )
        );

        assertFalse(
                Files.exists(
                        duplicateDirectory.resolve("New Job_GMAIL-ABC123.txt")
                )
        );
    }
}