package pl.xwolfq.monitor.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.xwolfq.monitor.dto.CreateAlertRuleDto;
import pl.xwolfq.monitor.entity.AlertHistory;
import pl.xwolfq.monitor.entity.AlertRule;
import pl.xwolfq.monitor.repository.AlertHistoryRepository;
import pl.xwolfq.monitor.repository.AlertRuleRepository;

import java.util.List;

/**
 * Kontroler REST modułu alerting.
 * <ul>
 *   <li>{@code GET/DELETE /api/v1/alerts/rules[/{id}]} — zarządzanie regułami.</li>
 *   <li>{@code GET /api/v1/alerts/history} — ostatnie wyzwolone alerty.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertRuleRepository alertRuleRepository;
    private final AlertHistoryRepository alertHistoryRepository;

    /** Zwraca wszystkie reguły alarmowe. */
    @GetMapping("/rules")
    public List<AlertRule> listRules() {
        return alertRuleRepository.findAll();
    }

    /** Dodaje nową regułę alarmową (domyślnie aktywną). */
    @PostMapping("/rules")
    @ResponseStatus(HttpStatus.CREATED)
    public AlertRule createRule(@Valid @RequestBody CreateAlertRuleDto dto) {
        AlertRule rule = new AlertRule();
        rule.setMetricType(dto.metricType());
        rule.setOperator(dto.operator());
        rule.setThreshold(dto.threshold());
        rule.setDescription(dto.description());
        return alertRuleRepository.save(rule);
    }

    /** Usuwa regułę alarmową po identyfikatorze. */
    @DeleteMapping("/rules/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRule(@PathVariable Long id) {
        alertRuleRepository.deleteById(id);
    }

    /** Zwraca 20 ostatnich wyzwolonych alertów. */
    @GetMapping("/history")
    public List<AlertHistory> getHistory() {
        return alertHistoryRepository.findTop20ByOrderByTriggeredAtDesc();
    }
}