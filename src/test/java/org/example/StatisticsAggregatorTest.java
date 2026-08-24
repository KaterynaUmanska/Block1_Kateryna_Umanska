package org.example;

import org.example.model.Material;
import org.example.model.Unit;
import org.example.service.StatisticsAggregator;
import org.example.model.PurchaseRecord;
import org.example.model.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class StatisticsAggregatorTest {

    @Test
    @DisplayName("Повинен правильно розділяти теги за комою та очищати від пробілів")
    void shouldCorrectlyAggregateTags() {
        StatisticsAggregator aggregator = new StatisticsAggregator("tags");

        PurchaseRecord req1 = new PurchaseRecord();
        req1.setTags("metal, heavy");

        PurchaseRecord req2 = new PurchaseRecord();
        req2.setTags("heavy , paint ");

        aggregator.process(req1);
        aggregator.process(req2);

        Map<String, Long> result = aggregator.getResultMap();

        assertEquals(2, result.get("heavy"));
        assertEquals(1, result.get("metal"));
        assertEquals(1, result.get("paint"));
    }

    @Test
    @DisplayName("Повинен правильно підраховувати статистику за enum-статусом")
    void shouldCorrectlyAggregateStatus() {
        StatisticsAggregator aggregator = new StatisticsAggregator("status");

        PurchaseRecord req1 = new PurchaseRecord();
        req1.setStatus(Status.APPROVED);

        PurchaseRecord req2 = new PurchaseRecord();
        req2.setStatus(Status.APPROVED);

        PurchaseRecord req3 = new PurchaseRecord();
        req3.setStatus(Status.CANCELED);

        aggregator.process(req1);
        aggregator.process(req2);
        aggregator.process(req3);

        Map<String, Long> result = aggregator.getResultMap();

        assertEquals(2, result.get("APPROVED"));
        assertEquals(1, result.get("CANCELED"));
    }

    @Test
    @DisplayName("Повинен ігнорувати порожні теги або null")
    void shouldHandleNullOrEmptyValuesGracefully() {
        StatisticsAggregator aggregator = new StatisticsAggregator("tags");

        PurchaseRecord req1 = new PurchaseRecord();
        req1.setTags(null);

        PurchaseRecord req2 = new PurchaseRecord();
        req2.setTags("  ,  ");

        assertDoesNotThrow(() -> aggregator.process(req1));
        assertDoesNotThrow(() -> aggregator.process(req2));

        assertTrue(aggregator.getResultMap().isEmpty());
    }

    @Test
    @DisplayName("Повинен правильно підраховувати статистику за одиницею вимірювання")
    void shouldCorrectlyAggregateUnit() {
        StatisticsAggregator aggregator = new StatisticsAggregator("unit");

        PurchaseRecord req1 = new PurchaseRecord();
        Material material1 = new Material();
        material1.setUnit(Unit.KG);
        req1.setMaterial(material1);

        PurchaseRecord req2 = new PurchaseRecord();
        Material material2 = new Material();
        material2.setUnit(Unit.KG);
        req2.setMaterial(material2);

        PurchaseRecord req3 = new PurchaseRecord();
        Material material3 = new Material();
        material3.setUnit(Unit.PCS);
        req3.setMaterial(material3);

        aggregator.process(req1);
        aggregator.process(req2);
        aggregator.process(req3);

        Map<String, Long> result = aggregator.getResultMap();

        assertEquals(2L, result.get("KG"));
        assertEquals(1L, result.get("PCS"));
    }

    @Test
    @DisplayName("Повинен правильно підраховувати статистику за матеріалом")
    void shouldCorrectlyAggregateMaterial() {
        StatisticsAggregator aggregator = new StatisticsAggregator("material");

        PurchaseRecord req1 = new PurchaseRecord();
        Material material1 = new Material();
        material1.setName("Steel");
        req1.setMaterial(material1);

        PurchaseRecord req2 = new PurchaseRecord();
        Material material2 = new Material();
        material2.setName("Steel");
        req2.setMaterial(material2);

        PurchaseRecord req3 = new PurchaseRecord();
        Material material3 = new Material();
        material3.setName("Wood");
        req3.setMaterial(material3);

        aggregator.process(req1);
        aggregator.process(req2);
        aggregator.process(req3);

        Map<String, Long> result = aggregator.getResultMap();

        assertEquals(2L, result.get("Steel"));
        assertEquals(1L, result.get("Wood"));
    }

    @Test
    @DisplayName("Повинен відхиляти непідтримуваний атрибут")
    void shouldRejectUnsupportedAttribute() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new StatisticsAggregator("unknown")
        );
    }

    @Test
    @DisplayName("Повинен безпечно обробляти транзакцію без material")
    void shouldHandleNullMaterial() {
        StatisticsAggregator aggregator = new StatisticsAggregator("material");
        PurchaseRecord transaction = new PurchaseRecord();

        assertDoesNotThrow(() -> aggregator.process(transaction));
        assertTrue(aggregator.getResultMap().isEmpty());
    }
}