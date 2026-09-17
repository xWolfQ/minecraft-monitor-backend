package pl.xwolfq.monitor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.List;

/**
 * Metryki pojedynczego świata z sekcji {@code worlds[]} prawdziwego
 * kontraktu agenta Paper/Java.
 */
public record WorldMetricsDto(

        // Nazwa świata (np. "world").
        @NotBlank(message = "worlds[].world is required")
        String world,

        // Liczba załadowanych chunków w tym świecie.
        @JsonProperty("loaded_chunks")
        @PositiveOrZero(message = "worlds[].loaded_chunks must not be negative")
        Integer loadedChunks,

        // Liczba encji w tym świecie.
        @JsonProperty("entity_count")
        @PositiveOrZero(message = "worlds[].entity_count must not be negative")
        Integer entityCount,

        // Liczba bloków-tile entities (np. skrzynie) w tym świecie.
        @JsonProperty("tile_entity_count")
        @PositiveOrZero(message = "worlds[].tile_entity_count must not be negative")
        Integer tileEntityCount,

        // Liczba graczy online w tym świecie.
        @JsonProperty("players_online")
        @PositiveOrZero(message = "worlds[].players_online must not be negative")
        Integer playersOnline,

        // Minimalny ping w ms.
        @JsonProperty("ping_min")
        @PositiveOrZero(message = "worlds[].ping_min must not be negative")
        Long pingMin,

        // Średni ping w ms.
        @JsonProperty("ping_avg")
        @PositiveOrZero(message = "worlds[].ping_avg must not be negative")
        BigDecimal pingAvg,

        // Maksymalny ping w ms.
        @JsonProperty("ping_max")
        @PositiveOrZero(message = "worlds[].ping_max must not be negative")
        Long pingMax,

        // Statystyki liczebności encji z podziałem na typy.
        List<@Valid EntityTypeCountDto> entities
) {
}