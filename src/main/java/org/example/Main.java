package org.example;

import org.example.service.DirectoryProcessor;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try {
            String directoryPath;
            String attribute;
            int threadCount;

            if (args.length < 2) {
                Scanner scanner = new Scanner(System.in);

                System.out.println("Консольна програма збору статистики");

                System.out.print("1. Введіть шлях до папки з JSON-файлами: ");
                directoryPath = scanner.nextLine().trim();

                System.out.print(
                        "2. Введіть назву атрибута для статистики " +
                                "(material, status, tags, unit): "
                );
                attribute = scanner.nextLine().trim();

                int availableCores = Runtime.getRuntime().availableProcessors();

                System.out.print(
                        "3. Введіть кількість потоків " +
                                "(натисніть Enter для значення за замовчуванням: "
                                + availableCores + "): "
                );

                String threadsInput = scanner.nextLine().trim();

                threadCount = parseThreadCount(threadsInput, availableCores);

            } else {
                directoryPath = args[0].trim();
                attribute = args[1].trim();

                int availableCores = Runtime.getRuntime().availableProcessors();

                threadCount = args.length >= 3
                        ? parseThreadCount(args[2], availableCores)
                        : availableCores;
            }

            System.out.println("\nЗапуск обробки...");

            long startTime = System.currentTimeMillis();

            DirectoryProcessor processor = new DirectoryProcessor();

            processor.processDirectory(
                    directoryPath,
                    attribute,
                    threadCount
            );

            long endTime = System.currentTimeMillis();

            System.out.println(
                    "Загальний час виконання (" +
                            threadCount +
                            " потоків): " +
                            (endTime - startTime) +
                            " ms"
            );

        } catch (Exception e) {
            System.err.println(
                    "Помилка під час виконання програми: "
                            + e.getMessage()
            );
        }
    }

    private static int parseThreadCount(
            String input,
            int defaultValue
    ) {
        if (input == null || input.isBlank()) {
            return defaultValue;
        }

        try {
            int threadCount = Integer.parseInt(input);

            if (threadCount <= 0) {
                System.out.println(
                        "Кількість потоків повинна бути більшою за 0. "
                                + "Використовуємо значення за замовчуванням: "
                                + defaultValue
                );

                return defaultValue;
            }

            return threadCount;

        } catch (NumberFormatException e) {
            System.out.println(
                    "Некоректне значення потоків. "
                            + "Використовуємо значення за замовчуванням: "
                            + defaultValue
            );

            return defaultValue;
        }
    }
}