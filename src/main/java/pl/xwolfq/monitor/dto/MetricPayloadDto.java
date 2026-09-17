package pl.xwolfq.monitor.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;

/**
 * Payload wysyłany przez agenta (plugin Paper/Java) w żądaniu
 * {@code POST /api/v1/metrics}.
 *
 * <p>Rzeczywisty kontrakt agenta jest zagnieżdżony: sekcja {@code global}
 * zawiera metryki globalne serwera (TPS, MSPT, RAM w bajtach, CPU jako ułamek,
 * GC, wątki, klasy, sieć), a sekcja {@code worlds} listę per-światowych
 * wskaźników (chunki, encje, gracze, ping) wraz z rozbiciem encji na typy.</p>
 *
 * @param timestamp opcjonalny — przy braku wartości serwer przypisze {@link Instant#now()}
 * @param global    metryki globalne (obowiązkowe)
 * @param worlds    metryki poszczególnych światów (co najmniej jeden)
 */
public record MetricPayloadDto(

        // Data/godzina pomiaru (ISO-8601, np. 2026-09-17T19:02:54.503808300Z).
        // Opcjonalne — przy braku wartości serwer użyje Instant.now().
        Instant timestamp,

        @NotNull(message = "global is required")
        @Valid
        GlobalMetricsDto global,

        @NotNull(message = "worlds is required")
        @NotEmpty(message = "worlds must not be empty")
        List<@Valid WorldMetricsDto> worlds
) {
}