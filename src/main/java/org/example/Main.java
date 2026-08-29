package org.example;

import org.example.service.DataGenerator;
import org.example.service.DirectoryProcessor;

import java.io.File;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try {
            int availableCores = Runtime.getRuntime().availableProcessors();
            String directoryPath;
            String attribute;
            int threadCount;

            if (args.length >= 2) {
                directoryPath = args[0];
                attribute = args[1];
                threadCount = args.length >= 3 ? parseThreadCount(args[2], availableCores) : availableCores;
            } else {
                generateDataIfNeeded();
                Scanner scanner = new Scanner(System.in);

                System.out.println("Консольна програма збору статистики");
                System.out.print("Введіть шлях до папки з JSON-файлами: ");
                directoryPath = scanner.nextLine().trim();

                System.out.print("Введіть назву атрибута для статистики (material, status, tags, unit): ");
                attribute = scanner.nextLine().trim();

                System.out.print("Введіть кількість потоків (Максимальна кількість: " + availableCores + "): ");
                threadCount = parseThreadCount(scanner.nextLine().trim(), availableCores);
            }

            System.out.println("\nЗапуск обробки...");
            long startTime = System.currentTimeMillis();

            DirectoryProcessor processor = new DirectoryProcessor();
            processor.processDirectory(directoryPath, attribute, threadCount);

            long endTime = System.currentTimeMillis();
            System.out.println("Загальний час виконання (" + threadCount + " потоків): " + (endTime - startTime) + " ms");

        } catch (Exception e) {
            System.err.println("Помилка під час виконання програми: " + e.getMessage());
        }
    }

    private static void generateDataIfNeeded() {
        File dataDirectory = new File("data");
        if (!hasJsonFiles(dataDirectory)) {
            System.out.println("Вхідні дані не знайдено. Запускаємо генерацію тестових даних...");
            DataGenerator.generateData();
        }
    }

    private static boolean hasJsonFiles(File dataDirectory) {
        if (!dataDirectory.isDirectory()) {
            return false;
        }
        File[] jsonFiles = dataDirectory.listFiles((directory, name) -> name.toLowerCase().endsWith(".json"));
        return jsonFiles != null && jsonFiles.length > 0;
    }

    private static int parseThreadCount(String input, int defaultValue) {
        if (input == null || input.isBlank()) {
            return defaultValue;
        }
        try {
            int threadCount = Integer.parseInt(input);
            if (threadCount <= 0 || threadCount > defaultValue) {
                System.out.println("Некоректна кількість потоків (повинна бути від 1 до " + defaultValue + "). Використовуємо значення за замовчуванням: " + defaultValue);
                return defaultValue;
            }
            return threadCount;
        } catch (NumberFormatException e) {
            System.out.println("Некоректне значення потоків. Використовуємо значення за замовчуванням: " + defaultValue);
            return defaultValue;
        }
    }

}