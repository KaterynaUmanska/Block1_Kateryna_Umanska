package org.example;

import org.example.model.PurchaseTransaction;
import org.example.model.Status;
import org.example.model.Unit;
import org.example.service.JsonStreamParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonStreamParserTest {

    @Test
    @DisplayName("Повинен коректно парсити JSON-файл потоком")
    void shouldParseJsonFile(
            @TempDir Path tempDir
    ) throws IOException {

        File tempFile =
                tempDir.resolve("test.json").toFile();

        try (FileWriter writer =
                     new FileWriter(tempFile)) {

            writer.write("""
                    [
                      {
                        "id": 1,
                        "material": {
                          "id": 10,
                          "name": "Material_10",
                          "unit": "KG"
                        },
                        "quantity": 50.0,
                        "status": "APPROVED",
                        "tags": "paint"
                      }
                    ]
                    """);
        }

        JsonStreamParser parser =
                new JsonStreamParser();

        List<PurchaseTransaction> list =
                new ArrayList<>();

        parser.parseFile(
                tempFile,
                list::add
        );

        assertEquals(1, list.size());

        PurchaseTransaction transaction =
                list.get(0);

        assertEquals(
                1L,
                transaction.getId()
        );

        assertEquals(
                50.0,
                transaction.getQuantity()
        );

        assertEquals(
                Status.APPROVED,
                transaction.getStatus()
        );

        assertEquals(
                "paint",
                transaction.getTags()
        );

        assertNotNull(
                transaction.getMaterial()
        );

        assertEquals(
                "Material_10",
                transaction.getMaterial().getName()
        );

        assertEquals(
                Unit.KG,
                transaction.getMaterial().getUnit()
        );
    }
    @Test
    @DisplayName("Повинен відхиляти JSON, який не є масивом")
    void shouldRejectJsonWithoutArray(@TempDir Path tempDir) throws IOException {

        File file = tempDir.resolve("invalid.json").toFile();

        try (FileWriter writer = new FileWriter(file)) {
            writer.write("""
                {
                  "id": 1
                }
                """);
        }

        JsonStreamParser parser = new JsonStreamParser();

        assertThrows(
                IllegalStateException.class,
                () -> parser.parseFile(file, transaction -> {})
        );
    }

}