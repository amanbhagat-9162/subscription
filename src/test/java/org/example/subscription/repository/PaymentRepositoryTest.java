package org.example.subscription.repository;

import org.example.subscription.entity.Payment;
import org.example.subscription.enums.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    void savePayment_success() {

        Payment payment = new Payment();
        payment.setSubscriptionId(1L);
        payment.setAmount(1000.0);
        payment.setPaymentMethod("CARD");
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setTransactionId("TXN123");
        payment.setPaymentDate(new Date());

        Payment saved = paymentRepository.save(payment);

        assertNotNull(saved.getId());
    }

    @Test
    void findById_success() {

        Payment payment = new Payment();
        payment.setSubscriptionId(2L);
        payment.setAmount(2000.0);
        payment.setPaymentMethod("UPI");
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setTransactionId("TXN456");
        payment.setPaymentDate(new Date());

        Payment saved = paymentRepository.save(payment);

        Optional<Payment> found =
                paymentRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("TXN456", found.get().getTransactionId());
    }
}
