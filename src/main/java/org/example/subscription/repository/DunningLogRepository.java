package org.example.subscription.repository;

import org.example.subscription.entity.DunningLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DunningLogRepository extends JpaRepository<DunningLog, Long> {

    List<DunningLog> findBySubscriptionId(Long subscriptionId);
}
