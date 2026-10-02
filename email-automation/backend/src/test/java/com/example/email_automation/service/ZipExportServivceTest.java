package com.example.email_automation.service;

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
                zipArchiveDirectory.toString()
        );

        zipExportService.archiveZipFile(zipFile);

        assertFalse(Files.exists(zipFile));

        assertTrue(
                Files.exists(
                        zipArchiveDirectory.resolve("job_batch_test.zip")
                )
        );
    }
}