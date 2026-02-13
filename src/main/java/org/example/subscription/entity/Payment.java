package org.example.subscription.entity;

import jakarta.persistence.*;
import lombok.*;
//import java.time.LocalDateTime;
import java.util.Date;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import org.example.subscription.enums.PaymentStatus;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @ManyToOne
//    private Subscription subscription;
    private Long subscriptionId;

    private Double amount;

    private String paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private String transactionId;

//    private LocalDateTime paymentDate;
@Temporal(TemporalType.TIMESTAMP)
private Date paymentDate;

    private Double refundAmount;

    private String refundReason;
    @Temporal(TemporalType.TIMESTAMP)
    private Date refundDate;

}

// in refund payent kitna aya or kitna gaya wo bhi hona chaiye