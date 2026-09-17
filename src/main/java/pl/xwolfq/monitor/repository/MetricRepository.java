package pl.xwolfq.monitor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.xwolfq.monitor.entity.ServerMetric;

import java.util.List;

/**
 * Repozytorium dostępu do danych metryk {@link ServerMetric}.
 */
public interface MetricRepository extends JpaRepository<ServerMetric, Long> {

    /**
     * Zwraca 50 najnowszych metryk posortowanych malejąco po dacie pomiaru.
     */
    List<ServerMetric> findTop50ByOrderByRecordedAtDesc();
}