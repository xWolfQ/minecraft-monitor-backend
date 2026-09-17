package pl.xwolfq.monitor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Statystyka liczebności encji danego typu w świecie
 * (sekcja {@code worlds[].entities[]} kontraktu agenta).
 */
public record EntityTypeCountDto(

        // Typ encji (np. FALLING_BLOCK, ZOMBIE, PLAYER).
        @NotBlank(message = "worlds[].entities[].type is required")
        String type,

        // Liczba encji tego typu.
        @PositiveOrZero(message = "worlds[].entities[].count must not be negative")
        Long count
) {
}