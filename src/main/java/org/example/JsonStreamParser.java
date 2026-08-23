package org.example;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.PurchaseRequisition;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

public class JsonStreamParser {
    private final ObjectMapper objectMapper;
    private final JsonFactory jsonFactory;

    public JsonStreamParser() {
        this.objectMapper = new ObjectMapper();
        this.jsonFactory = objectMapper.getFactory();
    }

    public void parseFile(File file, Consumer<PurchaseRequisition> consumer) throws IOException {
        // Відкриваємо файл як потік для потокового читання
        try (JsonParser parser = jsonFactory.createParser(file)) {

            // Перевіряємо, що файл починається з масиву '['
            if (parser.nextToken() != JsonToken.START_ARRAY) {
                throw new IllegalStateException("Очікувався JSON-масив у файлі: " + file.getName());
            }

            // Просуваємося по файлу токен за токеном
            while (parser.nextToken() == JsonToken.START_OBJECT) {
                // Мапимо поточний JSON-об'єкт у наш Java-клас
                PurchaseRequisition requisition = objectMapper.readValue(parser, PurchaseRequisition.class);

                // Передаємо прочитаний об'єкт слухачу (калькулятору)
                consumer.accept(requisition);
            }
        }
    }
}
