package org.example.subscription.repository;

import org.example.subscription.entity.CouponUsage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CouponUsageRepository
        extends JpaRepository<CouponUsage, Long> {

    Optional<CouponUsage> findByUserIdAndCouponId(Long userId, Long couponId);
}
