package org.example.subscription.repository;

import org.example.subscription.entity.Plan;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@ActiveProfiles("test")
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PlanRepositoryTest {

    @Autowired
    private PlanRepository planRepository;

    @Test
    void savePlan_success() {

        Plan plan = new Plan();
        plan.setName("Premium");
        plan.setPrice(2000.0);
        plan.setDurationDays(30);

        Plan saved = planRepository.save(plan);

        assertNotNull(saved.getId());
        assertEquals("Premium", saved.getName());
    }
}

