package org.example;

import org.example.model.Material;
import org.example.model.Unit;
import org.example.service.StatisticsAggregator;
import org.example.model.PurchaseTransaction;
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

        PurchaseTransaction req1 = new PurchaseTransaction();
        req1.setTags("metal, heavy");

        PurchaseTransaction req2 = new PurchaseTransaction();
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

        PurchaseTransaction req1 = new PurchaseTransaction();
        req1.setStatus(Status.APPROVED);

        PurchaseTransaction req2 = new PurchaseTransaction();
        req2.setStatus(Status.APPROVED);

        PurchaseTransaction req3 = new PurchaseTransaction();
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

        PurchaseTransaction req1 = new PurchaseTransaction();
        req1.setTags(null);

        PurchaseTransaction req2 = new PurchaseTransaction();
        req2.setTags("  ,  ");

        assertDoesNotThrow(() -> aggregator.process(req1));
        assertDoesNotThrow(() -> aggregator.process(req2));

        assertTrue(aggregator.getResultMap().isEmpty());
    }
    @Test
    @DisplayName("Повинен правильно підраховувати статистику за одиницею вимірювання")
    void shouldCorrectlyAggregateUnit() {

        StatisticsAggregator aggregator =
                new StatisticsAggregator("unit");

        PurchaseTransaction req1 =
                new PurchaseTransaction();

        Material material1 =
                new Material();

        material1.setUnit(Unit.KG);
        req1.setMaterial(material1);

        PurchaseTransaction req2 =
                new PurchaseTransaction();

        Material material2 =
                new Material();

        material2.setUnit(Unit.KG);
        req2.setMaterial(material2);

        PurchaseTransaction req3 =
                new PurchaseTransaction();

        Material material3 =
                new Material();

        material3.setUnit(Unit.PCS);
        req3.setMaterial(material3);

        aggregator.process(req1);
        aggregator.process(req2);
        aggregator.process(req3);

        Map<String, Long> result =
                aggregator.getResultMap();

        assertEquals(2L, result.get("KG"));
        assertEquals(1L, result.get("PCS"));
    }

    @Test
    @DisplayName("Повинен правильно підраховувати статистику за матеріалом")
    void shouldCorrectlyAggregateMaterial() {

        StatisticsAggregator aggregator =
                new StatisticsAggregator("material");

        PurchaseTransaction req1 =
                new PurchaseTransaction();

        Material material1 =
                new Material();

        material1.setName("Steel");
        req1.setMaterial(material1);

        PurchaseTransaction req2 =
                new PurchaseTransaction();

        Material material2 =
                new Material();

        material2.setName("Steel");
        req2.setMaterial(material2);

        PurchaseTransaction req3 =
                new PurchaseTransaction();

        Material material3 =
                new Material();

        material3.setName("Wood");
        req3.setMaterial(material3);

        aggregator.process(req1);
        aggregator.process(req2);
        aggregator.process(req3);

        Map<String, Long> result =
                aggregator.getResultMap();

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

        StatisticsAggregator aggregator =
                new StatisticsAggregator("material");

        PurchaseTransaction transaction =
                new PurchaseTransaction();

        assertDoesNotThrow(
                () -> aggregator.process(transaction)
        );

        assertTrue(
                aggregator.getResultMap().isEmpty()
        );
    }
}