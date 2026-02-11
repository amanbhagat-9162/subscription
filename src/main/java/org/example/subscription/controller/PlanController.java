package org.example.subscription.controller;

import org.example.subscription.entity.Plan;
import org.example.subscription.repository.PlanRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
public class PlanController {

    private final PlanRepository planRepository;

    public PlanController(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    @PostMapping
    public Plan createPlan(@RequestBody Plan plan) {
        return planRepository.save(plan);
    }

    @GetMapping
    public List<Plan> getAllPlans() {
        return planRepository.findAll();
    }
}
