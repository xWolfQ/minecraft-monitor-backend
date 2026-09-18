package pl.xwolfq.monitor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Reguła alarmowa definiująca warunek monitorowania konkretnej metryki.
 * <p>Przykłady: {@code TPS LESS_THAN 15.00}, {@code MSPT GREATER_THAN 50.00},
 * {@code CPU_USAGE GREATER_THAN 90.00}.</p>
 */
@Entity
@Table(name = "alert_rules")
@Getter
@Setter
@NoArgsConstructor
public class AlertRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Metryka, której dotyczy reguła. */
    @Enumerated(EnumType.STRING)
    @Column(name = "metric_type", nullable = false, length = 32)
    private MetricType metricType;

    /** Operator porównania (LESS_THAN / GREATER_THAN). */
    @Enumerated(EnumType.STRING)
    @Column(name = "operator", nullable = false, length = 32)
    private AlertOperator operator;

    /** Próg wyzwolenia alertu — precyzja 10, skala 2. */
    @Column(name = "threshold", nullable = false, precision = 10, scale = 2)
    private BigDecimal threshold;

    /** Czy reguła jest aktywna (domyślnie tak). */
    @Column(name = "active", nullable = false)
    private boolean active = true;

    /** Opis reguły (opcjonalny). */
    @Column(name = "description")
    private String description;
}