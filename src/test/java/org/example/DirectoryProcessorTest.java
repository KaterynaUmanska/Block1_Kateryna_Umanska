package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.PurchaseRecord;
import org.example.service.DirectoryProcessor;
import org.example.service.JsonStreamParser;
import org.example.service.XmlReportWriter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DirectoryProcessorTest {

    @Mock
    private JsonStreamParser parser;

    @Mock
    private XmlReportWriter xmlWriter;

    @Test
    @DisplayName("Успішна координація парсингу та передача даних у XmlReportWriter")
    void shouldCoordinateParsingAndReportGeneration(@TempDir Path tempDir) throws Exception {
        Files.createFile(tempDir.resolve("test.json"));

        DirectoryProcessor processor = new DirectoryProcessor(parser, xmlWriter);
        PurchaseRecord transaction = new PurchaseRecord();

        doAnswer(invocation -> {
            Consumer<PurchaseRecord> consumer = invocation.getArgument(1);
            consumer.accept(transaction);
            return null;
        }).when(parser).parseFile(any(File.class), any());

        processor.processDirectory(tempDir.toString(), "tags", 2);

        verify(parser).parseFile(any(File.class), any());
        verify(xmlWriter).generateReport(anyMap(), eq("tags"));
    }

    @Test
    @DisplayName("Викидання IllegalArgumentException, якщо директорія не існує")
    void shouldThrowExceptionWhenDirectoryDoesNotExist() {
        DirectoryProcessor processor = new DirectoryProcessor(parser, xmlWriter);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> processor.processDirectory("non_existent_folder_xyz", "tags", 2)
        );

        assertEquals("Вказаний шлях не є директорією: non_existent_folder_xyz", exception.getMessage());
        verifyNoInteractions(parser, xmlWriter);
    }

    @Test
    @DisplayName("Пропуск обробки, якщо в директорії немає JSON-файлів")
    void shouldDoNothingWhenNoJsonFilesFound(@TempDir Path tempDir) throws Exception {
        DirectoryProcessor processor = new DirectoryProcessor(parser, xmlWriter);

        processor.processDirectory(tempDir.toString(), "tags", 2);

        verifyNoInteractions(parser, xmlWriter);
    }

    @Test
    @DisplayName("Повинен обробляти всі JSON-файли у директорії")
    void shouldProcessAllJsonFiles(@TempDir Path tempDir) throws Exception {
        Files.createFile(tempDir.resolve("first.json"));
        Files.createFile(tempDir.resolve("second.json"));
        Files.createFile(tempDir.resolve("third.json"));

        DirectoryProcessor processor = new DirectoryProcessor(parser, xmlWriter);

        doAnswer(invocation -> {
            Consumer<PurchaseRecord> consumer = invocation.getArgument(1);
            PurchaseRecord transaction = new PurchaseRecord();
            transaction.setTags("metal");
            consumer.accept(transaction);
            return null;
        }).when(parser).parseFile(any(File.class), any());

        processor.processDirectory(tempDir.toString(), "tags", 2);

        verify(parser, times(3)).parseFile(any(File.class), any());
        verify(xmlWriter).generateReport(eq(Map.of("metal", 3L)), eq("tags"));
    }
    @Test
    @DisplayName("Повинен коректно агрегувати дані з кількох JSON-файлів при паралельній обробці")
    void shouldCorrectlyProcessMultipleFilesConcurrently() throws Exception {
        Path dataDirectory = Path.of("data");
        assertTrue(Files.exists(dataDirectory), "Папка data повинна існувати в корені проєкту");

        List<Path> jsonFiles;
        try (Stream<Path> files = Files.list(dataDirectory)) {
            jsonFiles = files.filter(path -> path.toString().endsWith(".json")).sorted().toList();
        }

        assertEquals(20, jsonFiles.size(), "У папці data повинно бути 20 JSON-файлів");

        Map<String, Integer> expectedStatistics = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();

        for (Path jsonFile : jsonFiles) {
            JsonNode root = objectMapper.readTree(jsonFile.toFile());
            for (JsonNode record : root) {
                JsonNode tagsNode = record.get("tags");
                if (tagsNode == null || tagsNode.isNull()) continue;

                for (String tag : tagsNode.asText().split(",")) {
                    String normalizedTag = tag.trim();
                    if (!normalizedTag.isEmpty()) {
                        expectedStatistics.merge(normalizedTag, 1, Integer::sum);
                    }
                }
            }
        }

        //expectedStatistics.put("paint", 999999);

        File resultFile = new File("statistics_by_tags.xml");
        try {
            DirectoryProcessor processor = new DirectoryProcessor();
            processor.processDirectory(dataDirectory.toString(), "tags", 4);

            assertTrue(resultFile.exists(), "XML-файл зі статистикою повинен бути створений");
            String content = Files.readString(resultFile.toPath());

            Pattern pattern = Pattern.compile(
                    "<value>(.*?)</value>\\s*<count>(\\d+)</count>"
            );

            Matcher matcher = pattern.matcher(content);

            Map<String, Integer> actualStatistics = new HashMap<>();

            while (matcher.find()) {
                String tag = matcher.group(1).trim();
                int count = Integer.parseInt(matcher.group(2));
                actualStatistics.put(tag, count);
            }

            assertEquals(
                    expectedStatistics,
                    actualStatistics,
                    "Результат паралельної обробки не відповідає очікуваній статистиці"
            );
        } finally {
            Files.deleteIfExists(resultFile.toPath());
        }
    }
}