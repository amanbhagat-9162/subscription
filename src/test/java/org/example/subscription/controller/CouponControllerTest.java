package org.example.subscription.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.subscription.entity.Coupon;
import org.example.subscription.repository.CouponRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CouponController.class)
class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CouponRepository couponRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // ================= CREATE SUCCESS =================

    @Test
    void createCoupon_success() throws Exception {

        Coupon coupon = new Coupon();
        coupon.setId(1L);
        coupon.setCode("NEW10");
        coupon.setActive(true);

        // Coupon does NOT exist
        when(couponRepository.findByCode("NEW10"))
                .thenReturn(Optional.empty());

        when(couponRepository.save(any(Coupon.class)))
                .thenReturn(coupon);

        mockMvc.perform(post("/api/coupons")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(coupon)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("NEW10"))
                .andExpect(jsonPath("$.active").value(true));

        verify(couponRepository).save(any(Coupon.class));
    }

    // ================= CREATE DUPLICATE =================

    @Test
    void createCoupon_duplicate_shouldThrowException() throws Exception {

        Coupon coupon = new Coupon();
        coupon.setId(1L);
        coupon.setCode("NEW10");

        // Coupon already exists
        when(couponRepository.findByCode("NEW10"))
                .thenReturn(Optional.of(coupon));

        mockMvc.perform(post("/api/coupons")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(coupon)))
                .andExpect(status().isInternalServerError());
    }

    // ================= GET ALL =================

    @Test
    void getAllCoupons_success() throws Exception {

        Coupon coupon = new Coupon();
        coupon.setId(1L);
        coupon.setCode("NEW10");
        coupon.setActive(true);

        when(couponRepository.findAll())
                .thenReturn(List.of(coupon));

        mockMvc.perform(get("/api/coupons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("NEW10"))
                .andExpect(jsonPath("$[0].active").value(true));

        verify(couponRepository).findAll();
    }

    @Test
    void getAllCoupons_emptyList() throws Exception {

        when(couponRepository.findAll())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/coupons"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}
