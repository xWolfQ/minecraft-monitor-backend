package pl.xwolfq.monitor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.xwolfq.monitor.dto.GlobalMetricsDto;
import pl.xwolfq.monitor.dto.MetricPayloadDto;
import pl.xwolfq.monitor.dto.WorldMetricsDto;
import pl.xwolfq.monitor.entity.ServerMetric;
import pl.xwolfq.monitor.repository.MetricRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Serwis odpowiedzialny za konwersję {@link MetricPayloadDto} na encję
 * {@link ServerMetric} oraz zapis (persist) w bazie danych.
 */
@Service
@RequiredArgsConstructor
public class MetricService {

    private final MetricRepository metricRepository;
    private final MetricStreamService metricStreamService;
    private final AlertEvaluatorService alertEvaluatorService;

    /**
     * Konwertuje payload z agenta na encję i zapisuje ją w bazie.
     * Gdy {@code timestamp} nie zostanie dostarczony, przypisywany jest
     * {@link Instant#now()}. Po zapisie metryka jest ewaluowana przez
     * {@link AlertEvaluatorService} i rozsyłana klientom SSE.
     *
     * @param payload zweryfikowany payload metryk (walidacja po stronie kontrolera)
     * @return zapisana encja {@link ServerMetric}
     */
    @Transactional
    public ServerMetric saveMetric(MetricPayloadDto payload) {
        ServerMetric saved = metricRepository.save(toEntity(payload));
        alertEvaluatorService.evaluate(saved);
        metricStreamService.broadcast(payload);
        return saved;
    }

    /**
     * Zwraca 50 najnowszych metryk (malejąco po {@code recordedAt})
     * na potrzeby inicjalizacji wykresów na froncie.
     *
     * @return lista do 50 najnowszych metryk
     */
    @Transactional(readOnly = true)
    public List<ServerMetric> getRecentMetrics() {
        return metricRepository.findTop50ByOrderByRecordedAtDesc();
    }

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");
    private static final long BYTES_PER_MIB = 1024L * 1024L;
    private static final int SCALE = 2;

    private ServerMetric toEntity(MetricPayloadDto payload) {
        ServerMetric metric = new ServerMetric();
        metric.setRecordedAt(payload.timestamp() != null ? payload.timestamp() : Instant.now());

        // Sekcja global — konwersje: CPU ułamek -> procent, RAM bajty -> MiB.
        GlobalMetricsDto global = payload.global();
        metric.setTps(toDbScale(global.tps()));
        metric.setMspt(toDbScale(global.mspt()));
        metric.setCpuUsage(toDbScale(global.cpu().multiply(ONE_HUNDRED)));
        metric.setRamUsedMb(bytesToMiB(global.ramUsed()));
        metric.setRamMaxMb(bytesToMiB(global.ramMax()));

        metric.setGcYoungCount(global.gcYoungCount());
        metric.setGcYoungTime(global.gcYoungTime());
        metric.setGcOldCount(global.gcOldCount());
        metric.setGcOldTime(global.gcOldTime());
        metric.setThreadCount(global.threadCount());
        metric.setLoadedClasses(global.loadedClasses());
        metric.setNetworkIn(global.networkIn());
        metric.setNetworkOut(global.networkOut());

        // Sekcja worlds — agregacja do pojedynczego wiersza pomiaru.
        List<WorldMetricsDto> worlds = payload.worlds();
        metric.setPlayersOnline(sumInt(worlds, WorldMetricsDto::playersOnline));
        metric.setLoadedChunks(sumInt(worlds, WorldMetricsDto::loadedChunks));
        metric.setEntitiesCount(sumInt(worlds, WorldMetricsDto::entityCount));
        metric.setTileEntityCount(sumInt(worlds, WorldMetricsDto::tileEntityCount));
        metric.setPingAvg(averagePing(worlds));
        return metric;
    }

    private static BigDecimal toDbScale(BigDecimal value) {
        return value.setScale(SCALE, RoundingMode.HALF_UP);
    }

    private static long bytesToMiB(long bytes) {
        return bytes / BYTES_PER_MIB;
    }

    private static int sumInt(List<WorldMetricsDto> worlds, Function<WorldMetricsDto, Integer> mapper) {
        int total = 0;
        for (WorldMetricsDto world : worlds) {
            Integer value = mapper.apply(world);
            total += value != null ? value : 0;
        }
        return total;
    }

    private static Integer averagePing(List<WorldMetricsDto> worlds) {
        List<BigDecimal> pings = new ArrayList<>();
        for (WorldMetricsDto world : worlds) {
            if (world.pingAvg() != null) {
                pings.add(world.pingAvg());
            }
        }
        if (pings.isEmpty()) {
            return 0;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal ping : pings) {
            total = total.add(ping);
        }
        return total.divide(BigDecimal.valueOf(pings.size()), 0, RoundingMode.HALF_UP).intValue();
    }
}