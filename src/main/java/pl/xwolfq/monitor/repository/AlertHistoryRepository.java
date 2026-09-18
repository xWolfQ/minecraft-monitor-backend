package pl.xwolfq.monitor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.xwolfq.monitor.entity.AlertHistory;

import java.util.List;

/**
 * Repozytorium historii wyzwolonych alertów.
 */
public interface AlertHistoryRepository extends JpaRepository<AlertHistory, Long> {

    /**
     * Zwraca 20 ostatnich alertów posortowanych malejąco po dacie wyzwolenia.
     */
    List<AlertHistory> findTop20ByOrderByTriggeredAtDesc();
}