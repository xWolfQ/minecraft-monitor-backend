package pl.xwolfq.monitor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.xwolfq.monitor.dto.MetricPayloadDto;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Serwis obsługujący strumień Server-Sent Events (SSE) dla metryk.
 * <ul>
 *   <li>utrzymuje połączonych klientów w wątkowej kolekcji {@link CopyOnWriteArrayList};</li>
 *   <li>rejestruje nowe połączenia ({@link SseEmitter}) z timeoutem 30 minut;</li>
 *   <li>czyści martwe emmitery przez lifecycle hooks: {@code onCompletion},
 *       {@code onTimeout}, {@code onError};</li>
 *   <li>rozsyła nowe metryki jako nazwane eventy {@code metric-update}.</li>
 * </ul>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MetricStreamService {

    /** Maksymalny czas życia połączenia SSE — 30 minut, zgodnie ze specyfikacją. */
    private static final Duration SSE_TIMEOUT = Duration.ofMinutes(30);

    /** Zbiór aktywnych klientów SSE (bezpieczny współbieżnie). */
    private final List<SseEmitter> clients = new CopyOnWriteArrayList<>();

    /**
     * Rejestruje nowego klienta SSE i zwraca jego emiter.
     * Połączenie jest automatycznie usuwane z listy po zakończeniu,
     * timeoutcie lub błędzie.
     *
     * @return emiter gotowy do ustawienia jako odpowiedź HTTP (text/event-stream)
     */
    public SseEmitter register() {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT.toMillis());
        clients.add(emitter);

        emitter.onCompletion(() -> clients.remove(emitter));
        emitter.onTimeout(() -> clients.remove(emitter));
        emitter.onError(error -> clients.remove(emitter));

        log.debug("SSE client registered, active clients: {}", clients.size());
        return emitter;
    }

    /**
     * Rozsyła payload metryki do wszystkich aktywnych klientów.
     * Event przesyłany jest pod nazwą {@code metric-update}.
     *
     * @param metric dane metryki do przesłania (po udanym zapisie w bazie)
     */
    public void broadcast(MetricPayloadDto metric) {
        for (SseEmitter emitter : clients) {
            try {
                emitter.send(SseEmitter.event()
                        .name("metric-update")
                        .data(metric));
            } catch (IOException | IllegalStateException ex) {
                log.warn("Removing dead SSE client ({}, active before: {})",
                        ex.getMessage(), clients.size());
                clients.remove(emitter);
            }
        }
    }
}