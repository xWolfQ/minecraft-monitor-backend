package pl.xwolfq.monitor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

/**
 * Metryki globalne serwera Minecraft przesyłane w sekcji {@code global}
 * prawdziwego kontraktu agenta Paper/Java.
 *
 * <p>Uwagi na temat jednostek: RAM podawany jest w <strong>bajtach</strong>
 * ({@code ram_used}, {@code ram_max}), a CPU jako <strong>ułamek</strong>
 * 0..1 ({@code cpu} = 0.586 oznacza 58.6%). Konwersje do encji wykonuje
 * {@code MetricService}.</p>
 */
public record GlobalMetricsDto(

        // TPS serwera (pełna precyzja typów zmiennoprzecinkowych agenta).
        @NotNull(message = "global.tps is required")
        @DecimalMin(value = "0", message = "global.tps must not be negative")
        BigDecimal tps,

        // Średni czas ticka w ms.
        @NotNull(message = "global.mspt is required")
        @DecimalMin(value = "0", message = "global.mspt must not be negative")
        BigDecimal mspt,

        // Użyta pamięć RAM w bajtach.
        @JsonProperty("ram_used")
        @NotNull(message = "global.ram_used is required")
        @PositiveOrZero(message = "global.ram_used must not be negative")
        Long ramUsed,

        // Maksymalna dostępna pamięć RAM w bajtach.
        @JsonProperty("ram_max")
        @NotNull(message = "global.ram_max is required")
        @Positive(message = "global.ram_max must be positive")
        Long ramMax,

        // Użycie procesora jako ułamek 0..1 (np. 0.5861 = 58.61%).
        @NotNull(message = "global.cpu is required")
        @DecimalMin(value = "0", message = "global.cpu must not be negative")
        BigDecimal cpu,

        // Liczba młodych cykli GC (Young).
        @JsonProperty("gc_young_count")
        Long gcYoungCount,

        // Czas spędzony na GC Young w ms.
        @JsonProperty("gc_young_time")
        Long gcYoungTime,

        // Liczba cykli GC Old (Full).
        @JsonProperty("gc_old_count")
        Long gcOldCount,

        // Czas spędzony na GC Old w ms.
        @JsonProperty("gc_old_time")
        Long gcOldTime,

        // Liczba aktywnych wątków.
        @JsonProperty("thread_count")
        Integer threadCount,

        // Liczba załadowanych klas.
        @JsonProperty("loaded_classes")
        Integer loadedClasses,

        // Ruch przychodzący sieci.
        @JsonProperty("network_in")
        Long networkIn,

        // Ruch wychodzący sieci.
        @JsonProperty("network_out")
        Long networkOut
) {
}