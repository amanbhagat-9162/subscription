package org.example.subscription.service.impl;

import org.example.subscription.dto.SubscriptionResponseDTO;
import org.example.subscription.entity.*;
import org.example.subscription.enums.PaymentStatus;
import org.example.subscription.enums.SubscriptionStatus;
import org.example.subscription.exception.ResourceNotFoundException;
import org.example.subscription.repository.*;
import org.example.subscription.service.SubscriptionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final PaymentRepository paymentRepository;
    private final CouponRepository couponRepository;

    public SubscriptionServiceImpl(
            SubscriptionRepository subscriptionRepository,
            UserRepository userRepository,
            PlanRepository planRepository,
            PaymentRepository paymentRepository,
            CouponRepository couponRepository) {

        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.planRepository = planRepository;
        this.paymentRepository = paymentRepository;
        this.couponRepository = couponRepository;
    }

    // ================= CREATE SUBSCRIPTION =================

    @Override
    public SubscriptionResponseDTO createSubscription(Long userId,
                                                      Long planId,
                                                      String couponCode) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));

        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setPlan(plan);
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusDays(plan.getDurationDays()));
        subscription.setStatus(SubscriptionStatus.PENDING);
        subscription.setAutoRenew(true);

        double finalPrice = plan.getPrice();

        // ===== COUPON LOGIC =====
        if (couponCode != null && !couponCode.isBlank()) {

            Coupon coupon = couponRepository.findByCode(couponCode)
                    .orElseThrow(() -> new RuntimeException("Invalid coupon"));

            if (!coupon.getActive())
                throw new RuntimeException("Coupon inactive");

            if (coupon.getExpiryDate().isBefore(LocalDate.now()))
                throw new RuntimeException("Coupon expired");

            if (coupon.getUsedCount() >= coupon.getUsageLimit())
                throw new RuntimeException("Coupon usage exceeded");

            if (coupon.getDiscountPercentage() != null) {
                finalPrice -= (finalPrice * coupon.getDiscountPercentage() / 100);
            }

            if (coupon.getDiscountAmount() != null) {
                finalPrice -= coupon.getDiscountAmount();
            }

            coupon.setUsedCount(coupon.getUsedCount() + 1);
            couponRepository.save(coupon);

            subscription.setCoupon(coupon);
        }

        subscription.setFinalPrice(finalPrice);

        Subscription saved = subscriptionRepository.save(subscription);
        return convertToDTO(saved);
    }

    // ================= GET ALL =================

    @Override
    public List<SubscriptionResponseDTO> getAllSubscriptions() {
        return subscriptionRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ================= CANCEL =================

    @Override
    public SubscriptionResponseDTO cancelSubscription(Long id) {

        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        subscription.setStatus(SubscriptionStatus.CANCELLED);

        Subscription updated = subscriptionRepository.save(subscription);
        return convertToDTO(updated);
    }

    // ================= SCHEDULER =================

    @Scheduled(fixedRate = 10000)
    public void handleSubscriptions() {

        // ACTIVE → GRACE
        List<Subscription> activeSubs =
                subscriptionRepository.findByStatus(SubscriptionStatus.ACTIVE);

        for (Subscription sub : activeSubs) {

            if (sub.getEndDate().isBefore(LocalDate.now())) {
                sub.setStatus(SubscriptionStatus.GRACE);
                subscriptionRepository.save(sub);
            }
        }

        // GRACE → ACTIVE or EXPIRED
        List<Subscription> graceSubs =
                subscriptionRepository.findByStatus(SubscriptionStatus.GRACE);

        for (Subscription sub : graceSubs) {

            if (sub.getEndDate().plusDays(3).isBefore(LocalDate.now())) {

                if (Boolean.TRUE.equals(sub.getAutoRenew())) {

                    Payment payment = new Payment();
                    payment.setSubscription(sub);
                    payment.setAmount(sub.getPlan().getPrice());
                    payment.setPaymentMethod("AUTO");
                    payment.setPaymentStatus(PaymentStatus.SUCCESS);
                    payment.setTransactionId("AUTO" + System.currentTimeMillis());
                    payment.setPaymentDate(LocalDateTime.now());

                    paymentRepository.save(payment);

                    sub.setStartDate(LocalDate.now());
                    sub.setEndDate(LocalDate.now()
                            .plusDays(sub.getPlan().getDurationDays()));
                    sub.setStatus(SubscriptionStatus.ACTIVE);

                } else {
                    sub.setStatus(SubscriptionStatus.EXPIRED);
                }

                subscriptionRepository.save(sub);
            }
        }
    }

    // ================= DTO CONVERTER =================

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
