package pl.xwolfq.monitor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.xwolfq.monitor.dto.AlertEventDto;
import pl.xwolfq.monitor.entity.AlertHistory;
import pl.xwolfq.monitor.entity.AlertOperator;
import pl.xwolfq.monitor.entity.AlertRule;
import pl.xwolfq.monitor.entity.MetricType;
import pl.xwolfq.monitor.entity.ServerMetric;
import pl.xwolfq.monitor.repository.AlertHistoryRepository;
import pl.xwolfq.monitor.repository.AlertRuleRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Serwis ewaluacji metryk względem aktywnych reguł alarmowych.
 * <p>Przy naruszeniu progu tworzy wpis w {@code alert_history}
 * i rozsyła zdarzenie SSE {@code alert-event}.</p>
 */
@Service
@RequiredArgsConstructor
public class AlertEvaluatorService {

    private final AlertRuleRepository alertRuleRepository;
    private final AlertHistoryRepository alertHistoryRepository;
    private final MetricStreamService metricStreamService;

    /**
     * Ewaluuje pojedynczy pomiar metryk względem wszystkich aktywnych reguł.
     *
     * @param metric zapisana w bazie metryka do oceny
     */
    @Transactional
    public void evaluate(ServerMetric metric) {
        List<AlertRule> rules = alertRuleRepository.findByActiveTrue();
        for (AlertRule rule : rules) {
            evaluateRule(rule, metric);
        }
    }

    private void evaluateRule(AlertRule rule, ServerMetric metric) {
        BigDecimal metricValue = metricValue(rule.getMetricType(), metric);
        if (metricValue == null) {
            return;
        }

        BigDecimal threshold = rule.getThreshold();
        boolean breached = switch (rule.getOperator()) {
            case LESS_THAN -> metricValue.compareTo(threshold) < 0;
            case GREATER_THAN -> metricValue.compareTo(threshold) > 0;
        };
        if (!breached) {
            return;
        }

        String message = buildMessage(rule.getMetricType(), metricValue, rule.getOperator(), threshold);

        AlertHistory history = new AlertHistory();
        history.setRuleId(rule.getId());
        history.setMetricValue(metricValue);
        history.setTriggeredAt(Instant.now());
        history.setMessage(message);
        AlertHistory saved = alertHistoryRepository.save(history);

        AlertEventDto event = new AlertEventDto(
                saved.getId(),
                rule.getId(),
                rule.getMetricType(),
                metricValue,
                threshold,
                saved.getMessage(),
                saved.getTriggeredAt()
        );
        metricStreamService.broadcastAlert(event);
    }

    private static BigDecimal metricValue(MetricType metricType, ServerMetric metric) {
        return switch (metricType) {
            case TPS -> metric.getTps();
            case MSPT -> metric.getMspt();
            case CPU_USAGE -> metric.getCpuUsage();
            case RAM_USED_MB -> metric.getRamUsedMb() != null
                    ? BigDecimal.valueOf(metric.getRamUsedMb())
                    : null;
        };
    }

    private static String buildMessage(MetricType metricType, BigDecimal metricValue,
                                       AlertOperator operator, BigDecimal threshold) {
        String relation = operator == AlertOperator.LESS_THAN ? "below" : "above";
        return metricType + " " + metricValue.stripTrailingZeros().toPlainString()
                + " is " + relation + " threshold "
                + threshold.stripTrailingZeros().toPlainString();
    }
}