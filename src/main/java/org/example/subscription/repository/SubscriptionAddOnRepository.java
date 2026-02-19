package org.example.subscription.repository;

import org.example.subscription.entity.SubscriptionAddOn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionAddOnRepository
        extends JpaRepository<SubscriptionAddOn, Long> {

    List<SubscriptionAddOn> findBySubscriptionIdAndActiveTrue(Long subscriptionId);
}
