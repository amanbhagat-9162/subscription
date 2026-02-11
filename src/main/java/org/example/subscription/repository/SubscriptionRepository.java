package org.example.subscription.repository;

import org.example.subscription.entity.Subscription;
import org.example.subscription.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findByStatus(SubscriptionStatus status);

    Optional<Subscription> findByUserIdAndStatus(Long userId, SubscriptionStatus status);

}
