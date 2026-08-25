package org.example.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Random;

public class DataGenerator {

    private static final String[] TAGS = {
            "metal", "heavy", "paint", "urgent", "wood", "fragile", "chem"
    };

    private static final String[] STATUSES = {
            "PENDING", "APPROVED", "PURCHASED", "CANCELED"
    };

    private static final String[] UNITS = {
            "KG", "METERS", "PCS", "LITERS"
    };

    private static final int FILES_COUNT = 10;
    private static final int RECORDS_PER_FILE = 10_000;
    private static final int MATERIALS_COUNT = 500;

    public static void generateData() {
        File dataDirectory = new File("data");

        if (!dataDirectory.exists() && !dataDirectory.mkdirs()) {
            System.err.println("Не вдалося створити папку data.");
            return;
        }

        Random random = new Random();

        for (int fileNumber = 1; fileNumber <= FILES_COUNT; fileNumber++) {
            File file = new File(dataDirectory, "data_part_" + fileNumber + ".json");

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("[\n");

                for (int recordNumber = 1; recordNumber <= RECORDS_PER_FILE; recordNumber++) {
                    int materialId = random.nextInt(MATERIALS_COUNT) + 1;
                    String status = STATUSES[random.nextInt(STATUSES.length)];
                    String unit = UNITS[random.nextInt(UNITS.length)];
                    String tags = generateTags(random);

                    long transactionId = (long) (fileNumber - 1) * RECORDS_PER_FILE + recordNumber;
                    double quantity = 1.0 + random.nextDouble() * 100;
                    String comma = (recordNumber == RECORDS_PER_FILE) ? "" : ",";

                    String jsonObject = String.format(
                            Locale.US,
                            """
                            {
                              "id": %d,
                              "material": {
                                "id": %d,
                                "name": "Material_%d",
                                "unit": "%s"
                              },
                              "quantity": %.1f,
                              "status": "%s",
                              "tags": "%s"
                            }%s
                            """,
                            transactionId,
                            materialId,
                            materialId,
                            unit,
                            quantity,
                            status,
                            tags,
                            comma
                    );

                    writer.write(jsonObject);
                }

                writer.write("]\n");
                System.out.println("Згенеровано файл: " + file.getName());

            } catch (IOException e) {
                System.err.println("Помилка генерації файлу " + file.getName() + ": " + e.getMessage());
            }
        }

        System.out.println("Генерація завершена! Папка 'data' готова до тестування.");
    }

    private static String generateTags(Random random) {
        int tagCount = random.nextInt(2) + 1;
        String firstTag = TAGS[random.nextInt(TAGS.length)];

        if (tagCount == 1) {
            return firstTag;
        }

        String secondTag;
        do {
            secondTag = TAGS[random.nextInt(TAGS.length)];
        } while (firstTag.equals(secondTag));

        return firstTag + ", " + secondTag;
    }
}