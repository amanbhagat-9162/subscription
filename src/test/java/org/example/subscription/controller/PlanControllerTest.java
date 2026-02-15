package org.example.subscription.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.subscription.entity.Plan;
import org.example.subscription.repository.PlanRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlanController.class)
class PlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlanRepository planRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createPlan_success() throws Exception {

        Plan plan = new Plan();
        plan.setId(1L);
        plan.setName("Basic");
        plan.setPrice(1000.0);
        plan.setDurationDays(30);

        when(planRepository.save(any(Plan.class)))
                .thenReturn(plan);

        mockMvc.perform(post("/api/plans")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(plan)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Basic"))
                .andExpect(jsonPath("$.price").value(1000.0));

        verify(planRepository).save(any(Plan.class));
    }

    @Test
    void getAllPlans_success() throws Exception {

        Plan plan = new Plan();
        plan.setId(1L);
        plan.setName("Pro");

        when(planRepository.findAll())
                .thenReturn(List.of(plan));

        mockMvc.perform(get("/api/plans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Pro"));
    }
}
