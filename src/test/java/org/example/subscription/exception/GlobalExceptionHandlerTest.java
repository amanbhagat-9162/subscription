package org.example.subscription.exception;

import org.example.subscription.controller.SubscriptionController;
import org.example.subscription.service.SubscriptionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

//import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SubscriptionController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SubscriptionService subscriptionService;

    @Test
    void shouldReturn404_whenSubscriptionNotFound() throws Exception {

        when(subscriptionService.cancelSubscription(99L))
                .thenThrow(new ResourceNotFoundException("Subscription not found"));

        mockMvc.perform(put("/api/subscriptions/99/cancel"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Subscription not found"));
    }


}

