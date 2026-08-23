package org.example.service;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class DirectoryProcessor {

    private final JsonStreamParser parser;
    private final XmlReportWriter xmlWriter;

    public DirectoryProcessor() {
        this(new JsonStreamParser(), new XmlReportWriter());
    }

    public DirectoryProcessor(JsonStreamParser parser, XmlReportWriter xmlWriter) {
        this.parser = parser;
        this.xmlWriter = xmlWriter;
    }
    public void processDirectory(
            String directoryPath,
            String attribute,
            int threadCount
    ) throws InterruptedException, IOException, XMLStreamException {

        File folder = new File(directoryPath);

        validateDirectory(folder);

        List<File> jsonFiles = findJsonFiles(folder);

        if (jsonFiles.isEmpty()) {
            System.out.println("JSON-файлів у папці не знайдено.");
            return;
        }

        StatisticsAggregator aggregator =
                new StatisticsAggregator(attribute);

        ExecutorService executor =
                Executors.newFixedThreadPool(threadCount);

        List<Future<Void>> futures = new ArrayList<>();

        try {
            for (File file : jsonFiles) {
                Future<Void> future = executor.submit(() -> {
                    parser.parseFile(file, aggregator::process);
                    return null;
                });

                futures.add(future);
            }

            executor.shutdown();

            waitForTasks(executor);

            checkTaskResults(futures);

        } finally {
            if (!executor.isTerminated()) {
                executor.shutdownNow();
            }
        }

        Map<String, Long> resultMap = aggregator.getResultMap();

        this.xmlWriter.generateReport(
                resultMap,
                attribute
        );
    }

    private void validateDirectory(File folder) {
        if (!folder.exists() || !folder.isDirectory()) {
            throw new IllegalArgumentException(
                    "Вказаний шлях не є директорією: "
                            + folder.getPath()
            );
        }
    }

    private List<File> findJsonFiles(File folder) throws IOException {
        try (var stream = Files.list(folder.toPath())) {
            return stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().toLowerCase().endsWith(".json"))
                    .map(Path::toFile)
                    .toList();
        }
    }

    private void waitForTasks(
            ExecutorService executor
    ) throws InterruptedException {

        boolean completed = executor.awaitTermination(
                1,
                TimeUnit.HOURS
        );

        if (!completed) {
            executor.shutdownNow();

            throw new IllegalStateException(
                    "Час очікування обробки файлів вичерпано"
            );
        }
    }

    private void checkTaskResults(
            List<Future<Void>> futures
    ) throws IOException {

        for (Future<Void> future : futures) {
            try {
                future.get();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();

                throw new IOException(
                        "Обробку файлів було перервано",
                        e
                );

            } catch (ExecutionException e) {
                Throwable cause = e.getCause();

                throw new IOException(
                        "Помилка під час обробки JSON-файлу",
                        cause
                );
            }
        }
    }
}