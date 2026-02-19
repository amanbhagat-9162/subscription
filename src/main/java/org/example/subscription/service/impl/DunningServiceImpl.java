package org.example.subscription.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.subscription.entity.DunningLog;
import org.example.subscription.entity.Subscription;
import org.example.subscription.enums.SubscriptionStatus;
import org.example.subscription.repository.DunningLogRepository;
import org.example.subscription.repository.SubscriptionRepository;
import org.example.subscription.service.DunningService;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class DunningServiceImpl implements DunningService {

    private final DunningLogRepository dunningLogRepository;
    private final SubscriptionRepository subscriptionRepository;

    private static final int MAX_ATTEMPTS = 3;

    @Override
    public void handleFailedPayment(Subscription subscription, String reason) {

        int newAttempt = subscription.getRenewalAttempts() + 1;
        subscription.setRenewalAttempts(newAttempt);

        // Save Dunning Log
        DunningLog log = new DunningLog();
        log.setSubscriptionId(subscription.getId());
        log.setAttemptNumber(newAttempt);
        log.setStatus("FAILED");
        log.setMessage(reason);
        log.setAttemptDate(new Date());

        dunningLogRepository.save(log);

        // Update Subscription Status
        if (newAttempt >= MAX_ATTEMPTS) {
            subscription.setStatus(SubscriptionStatus.EXPIRED);
        } else {
            subscription.setStatus(SubscriptionStatus.GRACE);
        }

        subscriptionRepository.save(subscription);

        System.out.println("Dunning Attempt " + newAttempt + " for Subscription "
                + subscription.getId());
    }
}
