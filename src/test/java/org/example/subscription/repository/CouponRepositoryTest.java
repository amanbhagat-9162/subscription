package org.example.subscription.repository;

import org.example.subscription.entity.Coupon;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CouponRepositoryTest {

    @Autowired
    private CouponRepository couponRepository;

//    @Test
//    void saveCoupon_success() {
//
//        Coupon coupon = new Coupon();
//        coupon.setCode("NEW10");
//        coupon.setActive(true);
//        coupon.setUsageLimit(5);
//        coupon.setUsedCount(0);
//        coupon.setDiscountPercentage(10.0);
//        coupon.setExpiryDate(new Date());
//
//        Coupon saved = couponRepository.save(coupon);
//
//        assertNotNull(saved.getId());
//    }

    @Test
    void findByCode_success() {

        Coupon coupon = new Coupon();
        coupon.setCode("SAVE20");
        coupon.setActive(true);
        coupon.setUsageLimit(5);
        coupon.setUsedCount(0);
        coupon.setDiscountPercentage(20.0);
        coupon.setExpiryDate(new Date());

        couponRepository.save(coupon);

        Optional<Coupon> found =
                couponRepository.findByCode("SAVE20");

        assertTrue(found.isPresent());
        assertEquals("SAVE20", found.get().getCode());

    }
}
