package org.example.subscription.service;

import org.example.subscription.entity.AddOn;

public interface AddOnService {

    AddOn createAddOn(AddOn addOn);

    void attachAddOn(Long subscriptionId, Long addOnId);

    void removeAddOn(Long subscriptionId, Long addOnId);
}
