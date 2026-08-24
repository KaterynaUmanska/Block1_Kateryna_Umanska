package org.example.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.PurchaseTransaction;

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

    public void parseFile(File file, Consumer<PurchaseTransaction> consumer) throws IOException {
        try (JsonParser parser = jsonFactory.createParser(file)) {
            JsonToken firstToken = parser.nextToken();

            if (firstToken != JsonToken.START_ARRAY) {
                throw new IllegalStateException("Очікувався JSON-масив у файлі: " + file.getName());
            }

            while (true) {
                JsonToken token = parser.nextToken();

                if (token == JsonToken.END_ARRAY) {
                    break;
                }

                if (token != JsonToken.START_OBJECT) {
                    throw new IllegalStateException("Очікувався JSON-об'єкт у масиві файлу: " + file.getName());
                }

                PurchaseTransaction transaction = objectMapper.readValue(parser, PurchaseTransaction.class);
                consumer.accept(transaction);
            }

            if (parser.nextToken() != null) {
                throw new IllegalStateException("Після завершення JSON-масиву знайдено додаткові дані у файлі: " + file.getName());
            }
        }
    }
}