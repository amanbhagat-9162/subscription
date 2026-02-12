package org.example.subscription.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import org.example.subscription.enums.SubscriptionStatus;

@Entity
@Table(name = "subscriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private Plan plan;

    private LocalDate startDate;

    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;
//    private Boolean addRenew = true;
    private Boolean autoRenew = true;

}
