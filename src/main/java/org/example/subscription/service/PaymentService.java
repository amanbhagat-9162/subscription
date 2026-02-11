package org.example.subscription.service;

import org.example.subscription.entity.Payment;

public interface PaymentService {
    Payment processPayment(Long subscriptionId, Double amount, String method);
    Payment refundPayment(Long paymentId, String reason);

}
