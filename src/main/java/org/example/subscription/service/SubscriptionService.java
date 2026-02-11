package org.example.subscription.service;

import org.example.subscription.dto.SubscriptionResponseDTO;
import java.util.List;

public interface SubscriptionService {

    SubscriptionResponseDTO createSubscription(Long userId, Long planId);

    List<SubscriptionResponseDTO> getAllSubscriptions();

    SubscriptionResponseDTO cancelSubscription(Long id);
}
