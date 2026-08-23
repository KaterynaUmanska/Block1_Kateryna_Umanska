import org.example.service.JsonStreamParser;
import org.example.model.PurchaseRequisition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonStreamParserTest {
    @Test
    @DisplayName("Повинен коректно парсити JSON-файл потоком")
    void shouldParseJsonFile(@TempDir Path tempDir) throws IOException {
        // Створюємо тимчасовий файл
        File tempFile = tempDir.resolve("test.json").toFile();
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("""
                [
                  {
                    "id": 1,
                    "quantity": 50.0,
                    "status": "APPROVED",
                    "tags": "paint"
                  }
                ]
                """);
        }

        JsonStreamParser parser = new JsonStreamParser();
        List<PurchaseRequisition> list = new ArrayList<>();

        parser.parseFile(tempFile, list::add);

        assertEquals(1, list.size());
        assertEquals(1L, list.get(0).getId());
        assertEquals(50.0, list.get(0).getQuantity());
        assertEquals("paint", list.get(0).getTags());
    }
}
