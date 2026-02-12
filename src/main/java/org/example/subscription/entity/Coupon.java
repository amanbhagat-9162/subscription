package org.example.subscription.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String code;

    private Double discountPercentage;

    private Double discountAmount;

    private Integer usageLimit;

    private Integer usedCount = 0;

    private Boolean active = true;

    private LocalDate expiryDate;
}
