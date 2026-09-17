package pl.xwolfq.monitor.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.xwolfq.monitor.service.MetricStreamService;

/**
 * Kontroler REST modułu real-time streaming (SSE).
 * <p>{@code GET /api/v1/stream/metrics} — strumień Server-Sent Events
 * z bieżącymi metrykami serwera Minecraft (timeout połączenia: 30 minut).</p>
 */
@RestController
@RequestMapping("/api/v1/stream")
@RequiredArgsConstructor
public class MetricStreamController {

    private final MetricStreamService metricStreamService;

    /**
     * Rejestruje nowego klienta strumienia i zwraca aktywne połączenie SSE
     * w formacie {@code text/event-stream}.
     */
    @GetMapping(value = "/metrics", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamMetrics() {
        return metricStreamService.register();
    }
}