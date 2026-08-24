package org.example;

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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
}