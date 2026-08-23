package org.example.service;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DirectoryProcessor {
    private final JsonStreamParser parser;

    public DirectoryProcessor() {
        this.parser = new JsonStreamParser();
    }

    public void processDirectory(String directoryPath, String attribute, int threadCount) throws InterruptedException, IOException, XMLStreamException {
        File folder = new File(directoryPath);
        if (!folder.exists() || !folder.isDirectory()) {
            throw new IllegalArgumentException("Вказаний шлях не є директорією: " + directoryPath);
        }

        List<File> jsonFiles;
        try (var stream = Files.list(folder.toPath())) {
            jsonFiles = stream
                    .filter(path -> path.toString().endsWith(".json"))
                    .map(Path::toFile)
                    .toList();
        }

        if (jsonFiles.isEmpty()) {
            System.out.println("JSON-файлів у папці не знайдено.");
            return;
        }

        StatisticsAggregator aggregator = new StatisticsAggregator(attribute);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (File file : jsonFiles) {
            executor.submit(() -> {
                try {
                    parser.parseFile(file, aggregator::process);
                } catch (IOException e) {
                    System.err.println("Помилка обробки файлу " + file.getName() + ": " + e.getMessage());
                }
            });
        }

        executor.shutdown();
        if (!executor.awaitTermination(1, TimeUnit.HOURS)) {
            System.err.println("Час очікування обробки файлів вичерпано!");
        }

        Map<String, Long> resultMap = aggregator.getResultMap();

        XmlReportWriter xmlWriter = new XmlReportWriter();
        xmlWriter.generateReport(resultMap, attribute);
    }
}