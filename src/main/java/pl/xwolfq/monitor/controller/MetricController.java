package pl.xwolfq.monitor.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.xwolfq.monitor.dto.MetricPayloadDto;
import pl.xwolfq.monitor.entity.ServerMetric;
import pl.xwolfq.monitor.service.MetricService;

import java.util.List;

/**
 * Kontroler REST modułu ingestion &amp; persistence.
 * <ul>
 *   <li>{@code POST /api/v1/metrics} — przyjęcie i zapis metryk od agenta (HTTP 201).</li>
 *   <li>{@code GET /api/v1/metrics/history} — ostatnie metryki dla inicjalizacji wykresów.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/metrics")
@RequiredArgsConstructor
public class MetricController {

    private final MetricService metricService;

    /**
     * Przyjmuje pojedynczy pomiar metryk od agenta, waliduje (jakarta.validation)
     * i zapisuje w PostgreSQL. Zwraca HTTP 201 Created wraz z zapisaną encją.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServerMetric ingestMetric(@Valid @RequestBody MetricPayloadDto payload) {
        return metricService.saveMetric(payload);
    }

    /**
     * Zwraca listę 50 najnowszych metryk posortowanych malejąco po dacie pomiaru.
     */
    @GetMapping("/history")
    public List<ServerMetric> getHistory() {
        return metricService.getRecentMetrics();
    }
}