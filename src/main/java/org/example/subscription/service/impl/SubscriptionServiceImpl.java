package org.example.subscription.service.impl;

import org.example.subscription.dto.SubscriptionResponseDTO;
import org.example.subscription.entity.*;
import org.example.subscription.enums.CouponType;
import org.example.subscription.enums.PaymentStatus;
import org.example.subscription.enums.SubscriptionStatus;
import org.example.subscription.exception.ResourceNotFoundException;
import org.example.subscription.repository.*;
import org.example.subscription.service.SubscriptionService;
//import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.example.subscription.repository.AddOnRepository;
import org.example.subscription.repository.SubscriptionAddOnRepository;
import org.example.subscription.entity.AddOn;
import org.example.subscription.entity.SubscriptionAddOn;


import java.util.*;
import java.util.stream.Collectors;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final PaymentRepository paymentRepository;
    private final CouponRepository couponRepository;
    private final CouponUsageRepository couponUsageRepository;
    private final AddOnRepository addOnRepository;
    private final SubscriptionAddOnRepository subscriptionAddOnRepository;



    public SubscriptionServiceImpl(
            SubscriptionRepository subscriptionRepository,
            UserRepository userRepository,
            PlanRepository planRepository,
            PaymentRepository paymentRepository,
            CouponRepository couponRepository, CouponUsageRepository couponUsageRepository, AddOnRepository addOnRepository, SubscriptionAddOnRepository subscriptionAddOnRepository) {

        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.planRepository = planRepository;
        this.paymentRepository = paymentRepository;
        this.couponRepository = couponRepository;
        this.couponUsageRepository = couponUsageRepository;
        this.addOnRepository = addOnRepository;
        this.subscriptionAddOnRepository = subscriptionAddOnRepository;
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

        // ===== CHECK ACTIVE SUBSCRIPTION =====
        boolean alreadyActive = subscriptionRepository
                .existsByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE);

        if (alreadyActive) {
            throw new RuntimeException("User already has an active subscription");
        }

        Subscription subscription = new Subscription();
        subscription.setUserId(user.getId());
        subscription.setPlanId(plan.getId());

        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_MONTH, plan.getDurationDays());

        subscription.setStartDate(now);
        subscription.setEndDate(calendar.getTime());
        subscription.setStatus(SubscriptionStatus.PENDING);
        subscription.setAutoRenew(true);

        double finalPrice = plan.getPrice();

        // ===== COUPON LOGIC =====
        if (couponCode != null && !couponCode.isBlank()) {

            Coupon coupon = couponRepository.findByCode(couponCode)
                    .orElseThrow(() -> new RuntimeException("Invalid coupon"));

            if (!coupon.getActive())
                throw new RuntimeException("Coupon inactive");

            if (coupon.getExpiryDate().before(new Date()))
                throw new RuntimeException("Coupon expired");

            if (coupon.getUsedCount() >= coupon.getUsageLimit())
                throw new RuntimeException("Coupon usage exceeded");

            // ===== PER USER CHECK =====
            Optional<CouponUsage> usageOpt =
                    couponUsageRepository.findByUserIdAndCouponId(user.getId(), coupon.getId());

            CouponUsage usage;

            if (usageOpt.isPresent()) {

                usage = usageOpt.get();

                if (usage.getUsageCount() >= 1) {
                    throw new RuntimeException("User already used this coupon");
                }

                usage.setUsageCount(usage.getUsageCount() + 1);

            } else {

                usage = new CouponUsage();
                usage.setUserId(user.getId());
                usage.setCouponId(coupon.getId());
                usage.setUsageCount(1);
            }

            couponUsageRepository.save(usage);

            // ===== GLOBAL COUNT UPDATE =====
            coupon.setUsedCount(coupon.getUsedCount() + 1);
            couponRepository.save(coupon);

            // ===== APPLY DISCOUNT BASED ON TYPE =====
            CouponType type = coupon.getType() == null ? CouponType.PERCENTAGE : coupon.getType();

            switch (type) {
                case PERCENTAGE:
                    if (coupon.getDiscountPercentage() != null) {
                        finalPrice -= (finalPrice * coupon.getDiscountPercentage() / 100);
                    }
                    break;
                case AMOUNT:
                    if (coupon.getDiscountAmount() != null) {
                        finalPrice -= coupon.getDiscountAmount();
                    }
                    break;
                case BOTH:
                    if (coupon.getDiscountPercentage() != null) {
                        finalPrice -= (finalPrice * coupon.getDiscountPercentage() / 100);
                    }
                    if (coupon.getDiscountAmount() != null) {
                        finalPrice -= coupon.getDiscountAmount();
                    }
                    break;
                case FREE_TRIAL:
                    finalPrice = 0.0;
                    break;
                default:
                    break;
            }

            if (finalPrice < 0) finalPrice = 0;

            subscription.setCouponId(coupon.getId());
        }
        subscription.setFinalPrice(finalPrice);

        Subscription saved = subscriptionRepository.save(subscription);
        saved.setStatus(SubscriptionStatus.ACTIVE);
        subscriptionRepository.save(saved);


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
        return convertToDTO(subscriptionRepository.save(subscription));
    }

    @Scheduled(fixedRate = 10000)
    public void handleSubscriptions() {

        List<Subscription> allSubs = subscriptionRepository.findAll();
        Date now = new Date();

        for (Subscription sub : allSubs) {

            // ================= ACTIVE → GRACE =================
//            if (sub.getStatus() == SubscriptionStatus.ACTIVE
//                    && sub.getEndDate().before(now)) {
//
//                sub.setStatus(SubscriptionStatus.GRACE);
//                subscriptionRepository.save(sub);
//            }
            if (sub.getStatus() == SubscriptionStatus.ACTIVE
                    && sub.getEndDate().before(now)) {

                if (Boolean.TRUE.equals(sub.getAutoRenew())) {

                    Plan plan = planRepository.findById(sub.getPlanId()).orElse(null);

                    if (plan != null) {
                        try {

                            Payment payment = new Payment();
                            payment.setSubscriptionId(sub.getId());
//                            payment.setAmount(plan.getPrice());

                            double totalAmount = plan.getPrice();

// Fetch active addons
                            List<SubscriptionAddOn> addOns =
                                    subscriptionAddOnRepository
                                            .findBySubscriptionIdAndActiveTrue(sub.getId());

                            for (SubscriptionAddOn sa : addOns) {
                                AddOn addOn = addOnRepository.findById(sa.getAddOnId()).orElse(null);
                                if (addOn != null && Boolean.TRUE.equals(addOn.getRecurring())) {
                                    totalAmount += addOn.getPrice();
                                }
                            }

                            payment.setAmount(totalAmount);

                            payment.setPaymentMethod("AUTO_RENEW");
                            payment.setPaymentStatus(PaymentStatus.SUCCESS);
                            payment.setTransactionId("REN" + System.currentTimeMillis());


                            payment.setPaymentDate(new Date());

                            paymentRepository.save(payment);

                            Calendar cal = Calendar.getInstance();
                            cal.setTime(new Date());
                            cal.add(Calendar.DAY_OF_MONTH, plan.getDurationDays());

                            sub.setStartDate(new Date());
                            sub.setEndDate(cal.getTime());
                            sub.setRenewalAttempts(0);

                            subscriptionRepository.save(sub);

                            continue; // renewal done, skip GRACE
                        } catch (Exception e) {

                            sub.setRenewalAttempts(sub.getRenewalAttempts() + 1);

                            if (sub.getRenewalAttempts() >= 3) {
                                sub.setStatus(SubscriptionStatus.EXPIRED);
                            } else {
                                sub.setStatus(SubscriptionStatus.GRACE);
                            }

                            subscriptionRepository.save(sub);
                        }
                    }
                } else {
                    sub.setStatus(SubscriptionStatus.GRACE);
                    subscriptionRepository.save(sub);
                }
            }


            // ================= GRACE LOGIC =================
            if (sub.getStatus() == SubscriptionStatus.GRACE) {


//                if (sub.getAutoRenew()) {
//
//                    Plan plan = planRepository.findById(sub.getPlanId()).orElse(null);
//
//                    if (plan != null) {
//
//                        // Create Payment
//                        Payment payment = new Payment();
//                        payment.setSubscriptionId(sub.getId());
//                        payment.setAmount(plan.getPrice());
//                        payment.setPaymentMethod("AUTO_RENEW");
//                        payment.setPaymentStatus(PaymentStatus.SUCCESS);
//                        payment.setTransactionId("REN" + System.currentTimeMillis());
//                        payment.setPaymentDate(new Date());
//
//                        paymentRepository.save(payment);
//
//                        // Extend subscription
//                        Calendar cal = Calendar.getInstance();
//                        cal.setTime(new Date());
//                        cal.add(Calendar.DAY_OF_MONTH, plan.getDurationDays());
//
//                        sub.setStartDate(new Date());
//                        sub.setEndDate(cal.getTime());
//                        sub.setStatus(SubscriptionStatus.ACTIVE);
//                        sub.setRenewalAttempts(0);
//
//                        subscriptionRepository.save(sub);
//
//                        continue; // skip expire logic
//                    }
//                }


                Calendar cal = Calendar.getInstance();
                cal.setTime(sub.getEndDate());
//                cal.add(Calendar.DAY_OF_MONTH, 3);
                cal.add(Calendar.DAY_OF_MONTH, sub.getGraceDays());

                if (cal.getTime().before(now)) {
                    sub.setStatus(SubscriptionStatus.EXPIRED);
                    subscriptionRepository.save(sub);
                }
            }
        }
    }

    // ================= CHANGE PLAN =================

    @Override
    public SubscriptionResponseDTO changePlan(Long subscriptionId, Long newPlanId) {

        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        if (subscription.getStatus() != SubscriptionStatus.ACTIVE) {
            throw new RuntimeException("Only active subscriptions can change plan");
        }

        Plan oldPlan = planRepository.findById(subscription.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Old plan not found"));
        Plan newPlan = planRepository.findById(newPlanId)
                .orElseThrow(() -> new ResourceNotFoundException("New plan not found"));

        long diff = subscription.getEndDate().getTime() - new Date().getTime();
        long remainingDays = diff / (1000 * 60 * 60 * 24);
        if (remainingDays < 0) remainingDays = 0;

        double oldDaily = oldPlan.getPrice() / oldPlan.getDurationDays();
        double remainingCredit = remainingDays * oldDaily;

        double finalAmount = newPlan.getPrice() - remainingCredit;

        Payment payment = new Payment();
        payment.setSubscriptionId(subscription.getId());
        payment.setAmount(Math.max(finalAmount, 0));
        payment.setPaymentMethod("UPGRADE");
//        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        if (finalAmount > 5000) {
            payment.setPaymentStatus(PaymentStatus.FAILED);
        } else {
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
        }

        payment.setTransactionId("UPG" + System.currentTimeMillis());
        payment.setPaymentDate(new Date());

        paymentRepository.save(payment);
        if(payment.getPaymentStatus() == PaymentStatus.FAILED){
            throw new RuntimeException("Payment failed");
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, newPlan.getDurationDays());

        subscription.setPlanId(newPlan.getId());
        subscription.setStartDate(new Date());
        subscription.setEndDate(calendar.getTime());

        subscriptionRepository.save(subscription);

        return convertToDTO(subscription);
    }

    // ================= DTO CONVERTER =================

    private SubscriptionResponseDTO convertToDTO(Subscription sub) {
//        User user = userRepository.findById(sub.getUserId()).orElse(null);
        User user = userRepository.findById(sub.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User missing"));

        Plan plan = planRepository.findById(sub.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan missing"));

        return new SubscriptionResponseDTO(
                sub.getId(),
//                sub.getUser().getName(),
//                sub.getPlan().getName(),
                user.getName(),
                plan.getName(),
                sub.getStartDate(),
                sub.getEndDate(),
                sub.getStatus().name()
        );
    }
}
