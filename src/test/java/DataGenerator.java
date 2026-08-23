import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Locale;

public class DataGenerator {
    private static final String[] TAGS = {
            "metal", "heavy", "paint", "urgent", "wood", "fragile", "chem"
    };

    private static final String[] STATUSES = {"DRAFT", "IN_APPROVAL", "APPROVED", "PURCHASED", "CANCELED"};
    private static final String[] UNIT ={"KG", "METERS", "PCS", "LITERS" };

    public static void main(String[] args) {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdir();
        }

        int filesCount = 10;
        int recordsPerFile = 10000;
        Random random = new Random();

        for (int i = 1; i <= filesCount; i++) {
            File file = new File(dataDir, "data_part_" + i + ".json");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("[\n");

                for (int j = 1; j <= recordsPerFile; j++) {
                    String status = STATUSES[random.nextInt(STATUSES.length)];
                    String unit = UNIT[random.nextInt(UNIT.length)];

                    String tag;
                    int tagCount = random.nextInt(2) + 1; // 1 або 2
                    if (tagCount == 1) {
                        tag = TAGS[random.nextInt(TAGS.length)];
                    } else {
                        String tag1 = TAGS[random.nextInt(TAGS.length)];
                        String tag2;
                        do {
                            tag2 = TAGS[random.nextInt(TAGS.length)];
                        } while (tag1.equals(tag2)); // Гарантуємо, що теги різні
                        tag = tag1 + ", " + tag2;
                    }

                    String jsonObject = String.format(Locale.US,"""
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
                            (i * 100000L + j), random.nextInt(500), random.nextInt(100), unit,
                            1.0 + random.nextDouble() * 100, status, tag, (j == recordsPerFile ? "" : ",")
                    );

                    writer.write(jsonObject);
                }

                writer.write("]\n");
                System.out.println("Згенеровано файл: " + file.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Генерація завершена! Папка 'data' готова до тестування.");
    }
}
