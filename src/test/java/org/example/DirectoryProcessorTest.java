package org.example;

import org.example.service.DirectoryProcessor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DirectoryProcessorTest {
    @Test
    @DisplayName("Повинен успішно обробляти директорію з файлами та генерувати XML-звіт")
    void shouldProcessDirectoryAndGenerateReport(@TempDir Path tempDir) throws Exception {

        File jsonFile = tempDir.resolve("test_data.json").toFile();
        try (FileWriter writer = new FileWriter(jsonFile)) {
            writer.write("""
                [
                  {
                    "id": 1,
                    "quantity": 10.0,
                    "status": "APPROVED",
                    "tags": "paint, wood"
                  },
                  {
                    "id": 2,
                    "quantity": 20.0,
                    "status": "DRAFT",
                    "tags": "paint"
                  }
                ]
                """);
        }

        DirectoryProcessor processor = new DirectoryProcessor();
        processor.processDirectory(tempDir.toString(), "tags", 2);

        File reportFile = new File("statistics_by_tags.xml");
        assertTrue(reportFile.exists(), "Звіт statistics_by_tags.xml має бути згенерований");

        String content = Files.readString(reportFile.toPath());
        assertTrue(content.contains("<value>paint</value>"));
        assertTrue(content.contains("<count>2</count>"));
        assertTrue(content.contains("<value>wood</value>"));
        assertTrue(content.contains("<count>1</count>"));

        if (reportFile.exists()) {
            reportFile.delete();
        }
    }
}