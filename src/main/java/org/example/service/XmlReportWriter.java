package org.example.service;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.Map;

public class XmlReportWriter {
    public void generateReport(Map<String, Long> statistics, String attribute) throws IOException, XMLStreamException {
        String fileName = "statistics_by_" + attribute.toLowerCase() + ".xml";

        var sortedList = statistics.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                .toList();

        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            XMLStreamWriter writer = factory.createXMLStreamWriter(fileWriter);

            writer.writeStartDocument("UTF-8", "1.0");
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
                writer.writeEndElement(); // </item>
                writer.writeCharacters("\n");
            }

            writer.writeEndElement();
            writer.writeEndDocument();

            writer.flush();
            writer.close();
        }
        System.out.println("Звіт успішно збережено у файл: " + fileName);
    }
}
