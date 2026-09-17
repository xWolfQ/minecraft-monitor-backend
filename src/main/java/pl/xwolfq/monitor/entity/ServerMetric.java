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
 * Encja przechowująca pojedynczy pomiar metryk serwera Minecraft.
 * Mapuje tabelę {@code server_metrics}; indeks {{@code recorded_at DESC}}
 * przyspiesza zapytania o historię (time-series).
 */
@Entity
@Table(
        name = "server_metrics",
        indexes = @Index(name = "idx_server_metrics_recorded_at", columnList = "recorded_at DESC")
)
@Getter
@Setter
@NoArgsConstructor
public class ServerMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recorded_at", nullable = false, updatable = false)
    private Instant recordedAt;

    /** TPS serwera — precyzja 4, skala 2 (nominalnie 20.00). */
    @Column(name = "tps", nullable = false, precision = 4, scale = 2)
    private BigDecimal tps;

    /** Średni czas ticka w ms — precyzja 6, skala 2 (próg 50.00). */
    @Column(name = "mspt", nullable = false, precision = 6, scale = 2)
    private BigDecimal mspt;

    /** Użycie CPU w procentach — precyzja 5, skala 2. */
    @Column(name = "cpu_usage", nullable = false, precision = 5, scale = 2)
    private BigDecimal cpuUsage;

    /** Użyta pamięć RAM w MB. */
    @Column(name = "ram_used_mb", nullable = false)
    private Long ramUsedMb;

    /** Maksymalna pamięć RAM w MB. */
    @Column(name = "ram_max_mb", nullable = false)
    private Long ramMaxMb;

    /** Liczba graczy online. */
    @Column(name = "players_online", nullable = false)
    private Integer playersOnline;

    /** Liczba załadowanych chunków. */
    @Column(name = "loaded_chunks", nullable = false)
    private Integer loadedChunks;

    /** Liczba encji na serwerze. */
    @Column(name = "entities_count", nullable = false)
    private Integer entitiesCount;

    /** Średni ping graczy w ms. */
    @Column(name = "ping_avg", nullable = false)
    private Integer pingAvg;

    /** Liczba młodych cykli GC (Young). */
    @Column(name = "gc_young_count")
    private Long gcYoungCount;

    /** Czas spędzony na GC Young w ms. */
    @Column(name = "gc_young_time")
    private Long gcYoungTime;

    /** Liczba cykli GC Old (Full). */
    @Column(name = "gc_old_count")
    private Long gcOldCount;

    /** Czas spędzony na GC Old w ms. */
    @Column(name = "gc_old_time")
    private Long gcOldTime;

    /** Liczba aktywnych wątków JVM. */
    @Column(name = "thread_count")
    private Integer threadCount;

    /** Liczba załadowanych klas JVM. */
    @Column(name = "loaded_classes")
    private Integer loadedClasses;

    /** Ruch sieciowy przychodzący. */
    @Column(name = "network_in")
    private Long networkIn;

    /** Ruch sieciowy wychodzący. */
    @Column(name = "network_out")
    private Long networkOut;

    /** Sumaryczna liczba tile entities we wszystkich światach. */
    @Column(name = "tile_entity_count")
    private Integer tileEntityCount;
}