package pl.xwolfq.monitor.dto;

import pl.xwolfq.monitor.entity.MetricType;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Zdarzenie alertu rozsyłane klientom SSE pod nazwą {@code alert-event}.
 */
public record AlertEventDto(
        Long id,
        Long ruleId,
        MetricType metricType,
        BigDecimal metricValue,
        BigDecimal threshold,
        String message,
        Instant triggeredAt
) {
}