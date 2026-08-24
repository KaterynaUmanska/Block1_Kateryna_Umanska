package org.example;

import org.example.service.XmlReportWriter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class XmlReportWriterTest {

    private final File expectedFile = new File("statistics_by_tags.xml");

    @AfterEach
    void cleanup() {
        if (expectedFile.exists()) {
            expectedFile.delete();
        }
    }

    @Test
    @DisplayName("Повинен правильно записувати статистику у XML-файл")
    void shouldWriteStatisticsToXml() throws Exception {
        Map<String, Long> data = new LinkedHashMap<>();
        data.put("paint", 15L);
        data.put("wood", 5L);

        XmlReportWriter writer = new XmlReportWriter();
        writer.generateReport(data, "tags");

        assertTrue(expectedFile.exists(), "Файл statistics_by_tags.xml мав бути створений");

        Document document = parseXml(expectedFile);
        assertEquals("statistics", document.getDocumentElement().getNodeName());

        NodeList items = document.getElementsByTagName("item");
        assertEquals(2, items.getLength());

        NodeList values = document.getElementsByTagName("value");
        NodeList counts = document.getElementsByTagName("count");

        assertEquals("paint", values.item(0).getTextContent());
        assertEquals("15", counts.item(0).getTextContent());
        assertEquals("wood", values.item(1).getTextContent());
        assertEquals("5", counts.item(1).getTextContent());
    }

    @Test
    @DisplayName("Повинен стабільно сортувати елементи з однаковою кількістю")
    void shouldSortEqualCountsByValue() throws Exception {
        Map<String, Long> data = new LinkedHashMap<>();
        data.put("wood", 5L);
        data.put("paint", 5L);
        data.put("metal", 5L);

        XmlReportWriter writer = new XmlReportWriter();
        writer.generateReport(data, "tags");

        Document document = parseXml(expectedFile);
        NodeList values = document.getElementsByTagName("value");

        assertEquals("metal", values.item(0).getTextContent());
        assertEquals("paint", values.item(1).getTextContent());
        assertEquals("wood", values.item(2).getTextContent());
    }

    private Document parseXml(File file) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        var builder = factory.newDocumentBuilder();
        return builder.parse(file);
    }
}