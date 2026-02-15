package org.example.subscription.service;

import org.example.subscription.dto.SubscriptionResponseDTO;
import java.util.List;

public interface SubscriptionService {

    //    SubscriptionResponseDTO createSubscription(Long userId, Long planId);
    SubscriptionResponseDTO createSubscription(Long userId, Long planId, String couponCode);


    List<SubscriptionResponseDTO> getAllSubscriptions();

    SubscriptionResponseDTO cancelSubscription(Long id);

    SubscriptionResponseDTO changePlan(Long subscriptionId, Long newPlanId);

}

