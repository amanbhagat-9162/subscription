package org.example.subscription.controller;

import org.example.subscription.dto.SubscriptionResponseDTO;
import org.example.subscription.service.SubscriptionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping
    public SubscriptionResponseDTO createSubscription(
            @RequestParam Long userId,
            @RequestParam Long planId) {

        return subscriptionService.createSubscription(userId, planId);
    }

    @GetMapping
    public List<SubscriptionResponseDTO> getAllSubscriptions() {
        return subscriptionService.getAllSubscriptions();
    }

    @PutMapping("/{id}/cancel")
    public SubscriptionResponseDTO cancel(@PathVariable Long id) {
        return subscriptionService.cancelSubscription(id);
    }
}
