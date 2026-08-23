package org.example.service;

import org.example.model.PurchaseTransaction;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

public class StatisticsAggregator {
    private final String targetAttribute;
    private final Map<String, LongAdder> statistics = new ConcurrentHashMap<>();

    public StatisticsAggregator(String targetAttribute) {
        this.targetAttribute = targetAttribute;
    }

    public void process(PurchaseTransaction req) {
        if (req == null) return;

        switch (targetAttribute.toLowerCase()) {
            case "tags":
                processMultiValueAttribute(req.getTags());
                break;
            case "status":
                if (req.getStatus() != null) {
                    addCount(req.getStatus().name());
                }
                break;
            case "unit":
                if(req.getMaterial() != null && req.getMaterial().getUnit() != null) {
                    addCount(String.valueOf(req.getMaterial().getUnit()));
                }
                break;
            case "material":
                if (req.getMaterial() != null && req.getMaterial().getName() != null) {
                    addCount(req.getMaterial().getName());
                }
                break;
            default:
                throw new IllegalArgumentException("Непідтримуваний атрибут: " + targetAttribute);
        }
    }

    private void processMultiValueAttribute(String rawValue) {
        if (rawValue == null || rawValue.trim().isEmpty()) {
            return;
        }

        String[] values = rawValue.split(",");
        for (String val : values) {
            String cleanedValue = val.trim();
            if (!cleanedValue.isEmpty()) {
                addCount(cleanedValue);
            }
        }
    }

    private void addCount(String key) {

        statistics.computeIfAbsent(key, k -> new LongAdder()).increment();
    }

    public Map<String, Long> getResultMap() {
        Map<String, Long> result = new ConcurrentHashMap<>();
        statistics.forEach((key, adder) -> result.put(key, adder.sum()));
        return result;
    }
}
