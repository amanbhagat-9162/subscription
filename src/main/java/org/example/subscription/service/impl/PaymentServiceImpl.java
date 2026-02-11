package org.example.subscription.service.impl;

import org.example.subscription.entity.*;
import org.example.subscription.enums.*;
import org.example.subscription.repository.*;
import org.example.subscription.service.PaymentService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final SubscriptionRepository subscriptionRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              SubscriptionRepository subscriptionRepository) {
        this.paymentRepository = paymentRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public Payment processPayment(Long subscriptionId, Double amount, String method) {

        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        Payment payment = new Payment();
        payment.setSubscription(subscription);
        payment.setAmount(amount);
        payment.setPaymentMethod(method);
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setTransactionId("TXN" + System.currentTimeMillis());
        payment.setPaymentDate(LocalDateTime.now());

        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscriptionRepository.save(subscription);

        return paymentRepository.save(payment);
    }
    @Override
    public Payment refundPayment(Long paymentId, String reason) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getPaymentStatus() != PaymentStatus.SUCCESS) {
            throw new RuntimeException("Only successful payments can be refunded");
        }

        payment.setPaymentStatus(PaymentStatus.REFUNDED);
        payment.setRefundAmount(payment.getAmount());
        payment.setRefundReason(reason);
        payment.setRefundDate(LocalDateTime.now());

        Subscription subscription = payment.getSubscription();
        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscriptionRepository.save(subscription);

        return paymentRepository.save(payment);
    }

}
