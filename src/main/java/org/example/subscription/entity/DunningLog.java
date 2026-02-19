package org.example.subscription.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "dunning_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DunningLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long subscriptionId;

    private Integer attemptNumber;

    private String status; // FAILED / SUCCESS

    private String message;

    @Temporal(TemporalType.TIMESTAMP)
    private Date attemptDate;
}
