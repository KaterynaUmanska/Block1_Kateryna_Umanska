package org.example.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Random;

public class DataGenerator {

    private static final String[] TAG = {
            "metal", "heavy", "paint", "urgent", "wood", "fragile", "chem"
    };

    private static final String[] STATUS = {
            "PENDING", "APPROVED", "PURCHASED", "CANCELED"
    };

    private static final String[] UNIT = {
            "KG", "METERS", "PCS", "LITERS"
    };

    private static final int FILES_COUNT = 20;
    private static final int RECORDS_PER_FILE = 50_000;
    private static final int MATERIALS_COUNT = 1_000;
    public static void generateData() {
        File dataDirectory = new File("data");

        if (!dataDirectory.exists() && !dataDirectory.mkdirs()) {
            System.err.println("Не вдалося створити папку data.");
            return;
        }

        Random random = new Random();

        for (int fileNumber = 1; fileNumber <= FILES_COUNT; fileNumber++) {
            File file = new File(dataDirectory, "data_" + fileNumber + ".json");

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("[\n");

                for (int recordNumber = 1; recordNumber <= RECORDS_PER_FILE; recordNumber++) {
                    int materialId = random.nextInt(MATERIALS_COUNT) + 1;
                    String status = STATUS[random.nextInt(STATUS.length)];
                    String unit = UNIT[random.nextInt(UNIT.length)];
                    String tags = generateTags(random);

                    long recordId = (long) (fileNumber - 1) * RECORDS_PER_FILE + recordNumber;
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
                            recordId,
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
        String firstTag = TAG[random.nextInt(TAG.length)];

        if (tagCount == 1) {
            return firstTag;
        }

        String secondTag;
        do {
            secondTag = TAG[random.nextInt(TAG.length)];
        } while (firstTag.equals(secondTag));

        return firstTag + ", " + secondTag;
    }
}