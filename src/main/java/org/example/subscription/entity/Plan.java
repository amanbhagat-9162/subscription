package org.example.subscription.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;          // BASIC / PREMIUM / PRO

    private Double price;

    private Integer durationDays; // 30 / 90 / 365

    private String description;
}
