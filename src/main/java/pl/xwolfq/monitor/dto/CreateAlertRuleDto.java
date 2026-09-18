package pl.xwolfq.monitor.dto;

import jakarta.validation.constraints.NotNull;
import pl.xwolfq.monitor.entity.AlertOperator;
import pl.xwolfq.monitor.entity.MetricType;

import java.math.BigDecimal;

/**
 * Payload tworzenia nowej reguły alarmowej ({@code POST /api/v1/alerts/rules}).
 */
public record CreateAlertRuleDto(
        @NotNull(message = "metricType is required")
        MetricType metricType,

        @NotNull(message = "operator is required")
        AlertOperator operator,

        @NotNull(message = "threshold is required")
        BigDecimal threshold,

        String description
) {
}