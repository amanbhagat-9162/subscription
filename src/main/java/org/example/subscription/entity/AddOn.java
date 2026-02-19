package org.example.subscription.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "addons")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddOn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;            // Example: Extra Storage

    private String description;     // Optional description

    private Double price;           // Cost of addon

    private Boolean recurring;      // true = charge every renewal
}
