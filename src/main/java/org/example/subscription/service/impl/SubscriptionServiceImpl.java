package org.example.subscription.service.impl;

import org.example.subscription.dto.SubscriptionResponseDTO;
import org.example.subscription.entity.*;
import org.example.subscription.enums.SubscriptionStatus;
import org.example.subscription.exception.ResourceNotFoundException;
import org.example.subscription.repository.*;
import org.example.subscription.service.SubscriptionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;
import org.example.subscription.exception.ResourceNotFoundException;


@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final PlanRepository planRepository;

    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository,
                                   UserRepository userRepository,
                                   PlanRepository planRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.planRepository = planRepository;
    }

    @Override
    public SubscriptionResponseDTO createSubscription(Long userId, Long planId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found"));
        Optional<Subscription> existing =
                subscriptionRepository.findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE);

        if (existing.isPresent()) {
            throw new ResourceNotFoundException("Active subscription already exists for this user");
        }

        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setPlan(plan);
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusDays(plan.getDurationDays()));
        subscription.setStatus(SubscriptionStatus.PENDING);

        Subscription saved = subscriptionRepository.save(subscription);
        return convertToDTO(saved);
    }

    @Override
    public List<SubscriptionResponseDTO> getAllSubscriptions() {
        return subscriptionRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SubscriptionResponseDTO cancelSubscription(Long id) {

        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        subscription.setStatus(SubscriptionStatus.CANCELLED);

        Subscription updated = subscriptionRepository.save(subscription);
        return convertToDTO(updated);
    }

    @Scheduled(fixedRate = 10000)
    public void expireSubscriptions() {

        List<Subscription> activeSubscriptions =
                subscriptionRepository.findByStatus(SubscriptionStatus.ACTIVE);

        activeSubscriptions.forEach(sub -> {

            if (sub.getEndDate().isBefore(LocalDate.now())) {

                sub.setStatus(SubscriptionStatus.EXPIRED);
                subscriptionRepository.save(sub);
            }
        });
    }

    private SubscriptionResponseDTO convertToDTO(Subscription sub) {
        return new SubscriptionResponseDTO(
                sub.getId(),
                sub.getUser().getName(),
                sub.getPlan().getName(),
                sub.getStartDate(),
                sub.getEndDate(),
                sub.getStatus().name()
        );
    }
}
