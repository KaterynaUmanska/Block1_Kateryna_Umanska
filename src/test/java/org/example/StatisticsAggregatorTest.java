package org.example;

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
    @DisplayName("Повинен відхиляти непідтримуваний атрибут")
    void shouldRejectUnsupportedAttribute() {
        StatisticsAggregator aggregator =
                new StatisticsAggregator("unknown");

        PurchaseTransaction transaction = new PurchaseTransaction();

        assertThrows(
                IllegalArgumentException.class,
                () -> aggregator.process(transaction)
        );
    }
}