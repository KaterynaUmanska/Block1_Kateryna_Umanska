package org.example;

import org.example.service.DirectoryProcessor;

import java.io.IOException;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws IOException {
        String directoryPath;
        String attribute;
        int threadCount;

        if (args.length < 2) {
            Scanner scanner = new Scanner(System.in);

            System.out.println("=== Консольна програма збору статистики ===");

            System.out.print("1. Введіть шлях до папки з JSON-файлами: ");
            directoryPath = scanner.nextLine().trim();

            System.out.print("2. Введіть назву атрибута для статистики: ");
            attribute = scanner.nextLine().trim();

            int availableCores = Runtime.getRuntime().availableProcessors();
            System.out.print("3. Введіть кількість потоків (натисніть Enter для значення за замовчуванням: " + availableCores + "): ");
            String threadsInput = scanner.nextLine().trim();

            if (threadsInput.isEmpty()) {
                threadCount = availableCores;
            } else {
                try {
                    threadCount = Integer.parseInt(threadsInput);
                } catch (NumberFormatException e) {
                    System.out.println("Некоректне значення потоків. Використовуємо за замовчуванням: " + availableCores);
                    threadCount = availableCores;
                }
            }
        } else {

            directoryPath = args[0];
            attribute = args[1];
            threadCount = args.length > 2 ? Integer.parseInt(args[2]) : Runtime.getRuntime().availableProcessors();
        }

        try {
            System.out.println("\nЗапуск обробки...");
            long startTime = System.currentTimeMillis();

            DirectoryProcessor processor = new DirectoryProcessor();
            processor.processDirectory(directoryPath, attribute, threadCount);

            long endTime = System.currentTimeMillis();
            System.out.println("Загальний час виконання (" + threadCount + " потоків): " + (endTime - startTime) + " ms");

        } catch (Exception e) {
            System.err.println("Помилка під час виконання програми: " + e.getMessage());
            e.printStackTrace();
        }
    }
}