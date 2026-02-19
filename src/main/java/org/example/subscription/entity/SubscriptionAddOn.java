package org.example.subscription.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subscription_addons")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionAddOn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subscription_id")
    private Long subscriptionId;

    @Column(name = "addon_id")
    private Long addOnId;

    private Boolean active = true;  // If removed, mark false
}
