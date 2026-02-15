package org.example.subscription.repository;

import org.example.subscription.entity.Subscription;
import org.example.subscription.enums.SubscriptionStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SubscriptionRepositoryTest {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Test
    void saveSubscription_success() {

        Subscription sub = new Subscription();
        sub.setUserId(1L);
        sub.setPlanId(10L);
        sub.setStatus(SubscriptionStatus.ACTIVE);

        Subscription saved = subscriptionRepository.save(sub);

        assertNotNull(saved.getId());
        assertEquals(SubscriptionStatus.ACTIVE, saved.getStatus());
    }

    @Test
    void findById_success() {

        Subscription sub = new Subscription();
        sub.setUserId(1L);
        sub.setPlanId(10L);
        sub.setStatus(SubscriptionStatus.PENDING);

        Subscription saved = subscriptionRepository.save(sub);

        Optional<Subscription> found =
                subscriptionRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(SubscriptionStatus.PENDING, found.get().getStatus());
    }
}
