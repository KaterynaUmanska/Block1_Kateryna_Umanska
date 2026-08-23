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

            if (parser.nextToken() != JsonToken.START_ARRAY) {
                throw new IllegalStateException("Очікувався JSON-масив у файлі: " + file.getName());
            }

            while (parser.nextToken() == JsonToken.START_OBJECT) {
                PurchaseTransaction requisition = objectMapper.readValue(parser, PurchaseTransaction.class);
                consumer.accept(requisition);
            }
        }
    }
}
