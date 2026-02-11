package org.example.subscription.controller;

import org.example.subscription.entity.Payment;
import org.example.subscription.service.PaymentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public Payment pay(@RequestParam Long subscriptionId,
                       @RequestParam Double amount,
                       @RequestParam String method) {

        return paymentService.processPayment(subscriptionId, amount, method);
    }
    @PutMapping("/refund")
    public Payment refund(@RequestParam Long paymentId,
                          @RequestParam String reason) {

        return paymentService.refundPayment(paymentId, reason);
    }

}
