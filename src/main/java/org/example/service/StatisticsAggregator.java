package org.example.service;

import org.example.model.PurchaseTransaction;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

public class StatisticsAggregator {

    private static final Set<String> SUPPORTED_ATTRIBUTES = Set.of(
            "tags",
            "status",
            "unit",
            "material"
    );

    private final String targetAttribute;
    private final Map<String, LongAdder> statistics = new ConcurrentHashMap<>();

    public StatisticsAggregator(String targetAttribute) {
        if (targetAttribute == null || targetAttribute.isBlank()) {
            throw new IllegalArgumentException("Атрибут для статистики не може бути порожнім");
        }

        this.targetAttribute = targetAttribute.toLowerCase(Locale.ROOT);

        if (!SUPPORTED_ATTRIBUTES.contains(this.targetAttribute)) {
            throw new IllegalArgumentException("Непідтримуваний атрибут: " + targetAttribute);
        }
    }

    public void process(PurchaseTransaction transaction) {
        if (transaction == null) {
            return;
        }

        switch (targetAttribute) {
            case "tags" -> processMultiValueAttribute(transaction.getTags());

            case "status" -> {
                if (transaction.getStatus() != null) {
                    addCount(transaction.getStatus().name());
                }
            }

            case "unit" -> {
                if (transaction.getMaterial() != null && transaction.getMaterial().getUnit() != null) {
                    addCount(transaction.getMaterial().getUnit().name());
                }
            }

            case "material" -> {
                if (transaction.getMaterial() != null && transaction.getMaterial().getName() != null) {
                    addCount(transaction.getMaterial().getName());
                }
            }

            default -> throw new IllegalStateException("Невідомий атрибут: " + targetAttribute);
        }
    }

    private void processMultiValueAttribute(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return;
        }

        String[] values = rawValue.split(",");

        for (String value : values) {
            String cleanedValue = value.trim();

            if (!cleanedValue.isEmpty()) {
                addCount(cleanedValue);
            }
        }
    }

    private void addCount(String key) {
        statistics.computeIfAbsent(key, ignored -> new LongAdder()).increment();
    }

    public Map<String, Long> getResultMap() {
        Map<String, Long> result = new ConcurrentHashMap<>();

        statistics.forEach((key, adder) -> result.put(key, adder.sum()));

        return result;
    }
}