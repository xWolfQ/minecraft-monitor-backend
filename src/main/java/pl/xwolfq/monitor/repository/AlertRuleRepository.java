package pl.xwolfq.monitor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.xwolfq.monitor.entity.AlertRule;

import java.util.List;

/**
 * Repozytorium reguł alarmowych.
 */
public interface AlertRuleRepository extends JpaRepository<AlertRule, Long> {

    /**
     * Zwraca wszystkie aktywne reguły alarmowe.
     */
    List<AlertRule> findByActiveTrue();
}