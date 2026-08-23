package org.example;

import org.example.service.DirectoryProcessor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DirectoryProcessorTest {

    private static final Path REPORT_PATH = Path.of("statistics_by_tags.xml");

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(REPORT_PATH);
    }

    @Test
    @DisplayName("Повинен успішно обробляти директорію з файлами та генерувати XML-звіт")
    void shouldProcessDirectoryAndGenerateReport(@TempDir Path tempDir) throws Exception {
        createJsonFile(tempDir, "test_data.json", """
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

        DirectoryProcessor processor = new DirectoryProcessor();
        processor.processDirectory(tempDir.toString(), "tags", 2);

        assertTrue(Files.exists(REPORT_PATH), "Звіт statistics_by_tags.xml має бути згенерований");

        String content = Files.readString(REPORT_PATH);
        assertTrue(content.contains("<value>paint</value>"));
        assertTrue(content.contains("<count>2</count>"));
        assertTrue(content.contains("<value>wood</value>"));
        assertTrue(content.contains("<count>1</count>"));
    }

    @Test
    @DisplayName("Повинен обробляти декілька JSON-файлів з однієї директорії")
    void shouldProcessMultipleJsonFiles(@TempDir Path tempDir) throws Exception {
        createJsonFile(tempDir, "first.json", """
                [
                  {
                    "id": 1,
                    "quantity": 10.0,
                    "status": "APPROVED",
                    "tags": "paint, wood"
                  }
                ]
                """);

        createJsonFile(tempDir, "second.json", """
                [
                  {
                    "id": 2,
                    "quantity": 20.0,
                    "status": "DRAFT",
                    "tags": "paint, metal"
                  }
                ]
                """);

        DirectoryProcessor processor = new DirectoryProcessor();
        processor.processDirectory(tempDir.toString(), "tags", 2);

        assertTrue(Files.exists(REPORT_PATH));

        String content = Files.readString(REPORT_PATH);
        assertTrue(content.contains("<value>paint</value>"));
        assertTrue(content.contains("<count>2</count>"));
        assertTrue(content.contains("<value>wood</value>"));
        assertTrue(content.contains("<count>1</count>"));
        assertTrue(content.contains("<value>metal</value>"));
        assertTrue(content.contains("<count>1</count>"));
    }

    @Test
    @DisplayName("Повинен коректно обробляти порожню директорію")
    void shouldHandleEmptyDirectory(@TempDir Path tempDir) {
        DirectoryProcessor processor = new DirectoryProcessor();

        assertDoesNotThrow(() ->
                processor.processDirectory(tempDir.toString(), "tags", 2)
        );
    }

    private void createJsonFile(Path dir, String fileName, String content) throws IOException {
        Files.writeString(dir.resolve(fileName), content);
    }
}