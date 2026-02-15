package org.example.subscription.controller;

import org.example.subscription.entity.Payment;
import org.example.subscription.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;


    @Test
    void pay_success() throws Exception {

        Payment payment = new Payment();
        payment.setId(1L);
        payment.setSubscriptionId(10L);
        payment.setAmount(1000.0);
        payment.setPaymentMethod("CARD");
        payment.setPaymentStatus(null);
        payment.setPaymentDate(new Date());

        when(paymentService.processPayment(10L, 1000.0, "CARD"))
                .thenReturn(payment);

        mockMvc.perform(post("/api/payments")
                        .param("subscriptionId", "10")
                        .param("amount", "1000.0")
                        .param("method", "CARD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subscriptionId").value(10))
                .andExpect(jsonPath("$.amount").value(1000.0))
                .andExpect(jsonPath("$.paymentMethod").value("CARD"));

        verify(paymentService)
                .processPayment(10L, 1000.0, "CARD");
    }


    @Test
    void refund_success() throws Exception {

        Payment refundPayment = new Payment();
        refundPayment.setId(1L);
        refundPayment.setAmount(1000.0);
        refundPayment.setPaymentMethod("CARD");

        when(paymentService.refundPayment(1L, "Duplicate Payment"))
                .thenReturn(refundPayment);

        mockMvc.perform(put("/api/payments/refund")
                        .param("paymentId", "1")
                        .param("reason", "Duplicate Payment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(1000.0));

        verify(paymentService)
                .refundPayment(1L, "Duplicate Payment");
    }
}
