package org.example.service;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;

public class XmlReportWriter {

    public void generateReport(Map<String, Long> statistics, String attribute)
            throws IOException, XMLStreamException {

        String fileName = "statistics_by_" + attribute.toLowerCase() + ".xml";
        Path outputFile = Path.of(fileName);

        var sortedList = statistics.entrySet()
                .stream()
                .sorted(
                        Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder())
                                .thenComparing(Map.Entry.comparingByKey())
                )
                .toList();

        XMLOutputFactory factory = XMLOutputFactory.newInstance();

        try (OutputStream outputStream = Files.newOutputStream(outputFile)) {
            XMLStreamWriter writer = factory.createXMLStreamWriter(
                    outputStream,
                    StandardCharsets.UTF_8.name()
            );

            try {
                writer.writeStartDocument(StandardCharsets.UTF_8.name(), "1.0");
                writer.writeCharacters("\n");

                writer.writeStartElement("statistics");
                writer.writeCharacters("\n");

                for (Map.Entry<String, Long> entry : sortedList) {
                    writer.writeCharacters("  ");
                    writer.writeStartElement("item");
                    writer.writeCharacters("\n");

                    writer.writeCharacters("    ");
                    writer.writeStartElement("value");
                    writer.writeCharacters(entry.getKey());
                    writer.writeEndElement();
                    writer.writeCharacters("\n");

                    writer.writeCharacters("    ");
                    writer.writeStartElement("count");
                    writer.writeCharacters(String.valueOf(entry.getValue()));
                    writer.writeEndElement();
                    writer.writeCharacters("\n");

                    writer.writeCharacters("  ");
                    writer.writeEndElement();
                    writer.writeCharacters("\n");
                }

                writer.writeEndElement();
                writer.writeCharacters("\n");

                writer.writeEndDocument();
                writer.flush();

            } finally {
                writer.close();
            }
        }

        System.out.println("Звіт успішно збережено у файл: " + outputFile.toAbsolutePath());
    }
}