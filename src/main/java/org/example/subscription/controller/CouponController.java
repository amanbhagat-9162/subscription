package org.example.subscription.controller;

import org.example.subscription.entity.Coupon;
import org.example.subscription.repository.CouponRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponRepository couponRepository;

    public CouponController(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @PostMapping
    public Coupon createCoupon(@RequestBody Coupon coupon) {
        if (couponRepository.findByCode(coupon.getCode()).isPresent()) {
            throw new RuntimeException("Coupon code already exists");
        }
        return couponRepository.save(coupon);
    }

    @GetMapping
    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }
}
