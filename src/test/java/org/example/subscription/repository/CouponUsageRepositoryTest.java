package org.example.subscription.repository;

import org.example.subscription.entity.CouponUsage;
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
class CouponUsageRepositoryTest {

    @Autowired
    private CouponUsageRepository couponUsageRepository;

    @Test
    void saveCouponUsage_success() {

        CouponUsage usage = new CouponUsage();
        usage.setUserId(1L);
        usage.setCouponId(100L);
        usage.setUsageCount(1);

        CouponUsage saved = couponUsageRepository.save(usage);

        assertNotNull(saved.getId());
    }

    @Test
    void findByUserIdAndCouponId_success() {

        CouponUsage usage = new CouponUsage();
        usage.setUserId(2L);
        usage.setCouponId(200L);
        usage.setUsageCount(1);

        couponUsageRepository.save(usage);

        Optional<CouponUsage> found =
                couponUsageRepository
                        .findByUserIdAndCouponId(2L, 200L);

        assertTrue(found.isPresent());
        assertEquals(2L, found.get().getUserId());
    }
}
