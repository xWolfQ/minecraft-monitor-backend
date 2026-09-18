package pl.xwolfq.monitor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Historia wyzwolonych alertów.
 */
@Entity
@Table(
        name = "alert_history",
        indexes = @Index(name = "idx_alert_history_triggered_at", columnList = "triggered_at DESC")
)
@Getter
@Setter
@NoArgsConstructor
public class AlertHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Moment wyzwolenia alertu. */
    @Column(name = "triggered_at", nullable = false, updatable = false)
    private Instant triggeredAt;

    /** Identyfikator reguły, która wyzwoliła alert. */
    @Column(name = "rule_id", nullable = false)
    private Long ruleId;

    /** Wartość metryki w momencie naruszenia — precyzja 10, skala 2. */
    @Column(name = "metric_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal metricValue;

    /** Czytelny komunikat alertu. */
    @Column(name = "message", nullable = false)
    private String message;
}