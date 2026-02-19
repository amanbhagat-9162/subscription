package org.example.subscription.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.subscription.entity.*;
import org.example.subscription.repository.*;
import org.example.subscription.service.AddOnService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddOnServiceImpl implements AddOnService {

    private final AddOnRepository addOnRepository;
    private final SubscriptionAddOnRepository subscriptionAddOnRepository;

    @Override
    public AddOn createAddOn(AddOn addOn) {
        return addOnRepository.save(addOn);
    }

    @Override
    public void attachAddOn(Long subscriptionId, Long addOnId) {

        SubscriptionAddOn link = new SubscriptionAddOn();
        link.setSubscriptionId(subscriptionId);
        link.setAddOnId(addOnId);
        link.setActive(true);

        subscriptionAddOnRepository.save(link);
    }

    @Override
    public void removeAddOn(Long subscriptionId, Long addOnId) {

        subscriptionAddOnRepository
                .findBySubscriptionIdAndActiveTrue(subscriptionId)
                .stream()
                .filter(sa -> sa.getAddOnId().equals(addOnId))
                .forEach(sa -> {
                    sa.setActive(false);
                    subscriptionAddOnRepository.save(sa);
                });
    }
}
