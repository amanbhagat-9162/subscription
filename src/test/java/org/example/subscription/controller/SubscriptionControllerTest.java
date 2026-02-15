package org.example.subscription.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.subscription.dto.SubscriptionResponseDTO;
import org.example.subscription.exception.ResourceNotFoundException;
import org.example.subscription.service.SubscriptionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SubscriptionController.class)
class SubscriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SubscriptionService subscriptionService;


    @Autowired
    private ObjectMapper objectMapper;

    // ✅ CREATE SUBSCRIPTION TEST
    @Test
    void createSubscription_success() throws Exception {

        SubscriptionResponseDTO dto =
                new SubscriptionResponseDTO(
                        1L,
                        "Aman",
                        "Basic",
                        null,
                        null,
                        "PENDING"
                );

        when(subscriptionService.createSubscription(1L, 10L, null))
                .thenReturn(dto);

        mockMvc.perform(post("/api/subscriptions")
                        .param("userId", "1")
                        .param("planId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("Aman"))
                .andExpect(jsonPath("$.planName").value("Basic"))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(subscriptionService).createSubscription(1L, 10L, null);
    }

    // ✅ GET ALL TEST
    @Test
    void getAllSubscriptions_success() throws Exception {

        SubscriptionResponseDTO dto =
                new SubscriptionResponseDTO(
                        1L,
                        "Aman",
                        "Basic",
                        null,
                        null,
                        "ACTIVE"
                );

        when(subscriptionService.getAllSubscriptions())
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/subscriptions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userName").value("Aman"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));

        verify(subscriptionService).getAllSubscriptions();
    }
    @Test
    void createSubscription_userNotFound_shouldReturn404() throws Exception {

        when(subscriptionService.createSubscription(99L, 10L, null))
                .thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(post("/api/subscriptions")
                        .param("userId", "99")
                        .param("planId", "10"))
                .andExpect(status().isNotFound());
    }

        // ✅ CANCEL TEST
    @Test
    void cancelSubscription_success() throws Exception {

        SubscriptionResponseDTO dto =
                new SubscriptionResponseDTO(
                        1L,
                        "Aman",
                        "Basic",
                        null,
                        null,
                        "CANCELLED"
                );

        when(subscriptionService.cancelSubscription(1L))
                .thenReturn(dto);

        mockMvc.perform(put("/api/subscriptions/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        verify(subscriptionService).cancelSubscription(1L);
    }
    @Test
    void createSubscription_withCoupon_success() throws Exception {

        SubscriptionResponseDTO dto =
                new SubscriptionResponseDTO(
                        1L,
                        "Aman",
                        "Pro",
                        null,
                        null,
                        "PENDING"
                );

        when(subscriptionService.createSubscription(1L, 10L, "NEW10"))
                .thenReturn(dto);

        mockMvc.perform(post("/api/subscriptions")
                        .param("userId", "1")
                        .param("planId", "10")
                        .param("couponCode", "NEW10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.planName").value("Pro"));

        verify(subscriptionService)
                .createSubscription(1L, 10L, "NEW10");
    }

    @Test
    void changePlan_success() throws Exception {

        SubscriptionResponseDTO dto =
                new SubscriptionResponseDTO(
                        1L,
                        "Aman",
                        "Premium",
                        null,
                        null,
                        "ACTIVE"
                );

        when(subscriptionService.changePlan(1L, 20L))
                .thenReturn(dto);

        mockMvc.perform(put("/api/subscriptions/1/change-plan")
                        .param("newPlanId", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.planName").value("Premium"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(subscriptionService)
                .changePlan(1L, 20L);
    }

    @Test
    void changePlan_notFound_shouldReturn404() throws Exception {

        when(subscriptionService.changePlan(1L, 20L))
                .thenThrow(new ResourceNotFoundException("Subscription not found"));

        mockMvc.perform(put("/api/subscriptions/1/change-plan")
                        .param("newPlanId", "20"))
                .andExpect(status().isNotFound());
    }

}
