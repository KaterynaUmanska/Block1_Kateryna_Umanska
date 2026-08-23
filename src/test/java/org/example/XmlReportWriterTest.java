package org.example;

import org.example.service.XmlReportWriter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class XmlReportWriterTest {
    private final File expectedFile = new File("examples/statistics_by_tags.xml");

    @AfterEach
    void cleanup() {
        // Видаляємо згенерований тестом файл після кожного запуску
        if (expectedFile.exists()) {
            expectedFile.delete();
        }
    }

    @Test
    @DisplayName("Повинен правильно записувати статистику у XML-файл")
    void shouldWriteStatisticsToXml() throws IOException, XMLStreamException {
        // Створюємо тестові дані
        Map<String, Long> sortedData = new LinkedHashMap<>();
        sortedData.put("paint", 15L);
        sortedData.put("wood", 5L);

        XmlReportWriter writer = new XmlReportWriter();

        writer.generateReport(sortedData, "tags");

        assertTrue(expectedFile.exists(), "Файл statistics_by_tags.xml мав бути створений");

        String content = Files.readString(expectedFile.toPath());

        assertTrue(content.contains("<value>paint</value>"));
        assertTrue(content.contains("<count>15</count>"));
        assertTrue(content.contains("<value>wood</value>"));
        assertTrue(content.contains("<count>5</count>"));

        int paintIndex = content.indexOf("<value>paint</value>");
        int woodIndex = content.indexOf("<value>wood</value>");

        assertTrue(paintIndex < woodIndex, "Елемент paint має бути в файлі раніше за wood");
    }
}
