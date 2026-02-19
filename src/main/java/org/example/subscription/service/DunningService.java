package org.example.subscription.service;

import org.example.subscription.entity.Subscription;

public interface DunningService {

    void handleFailedPayment(Subscription subscription, String reason);

}
