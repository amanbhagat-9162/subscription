package org.example.subscription.entity;

import jakarta.persistence.*;
import lombok.*;
//import java.time.LocalDate;
import java.util.Date;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

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

//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private User user;
    private Long userId;
    private Long planId;

//    @ManyToOne
//    @JoinColumn(name = "plan_id")
//    private Plan plan;

    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    private Date endDate;


    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;
//    private Boolean addRenew = true;
    private Boolean autoRenew = true;
    private Double finalPrice;
//    @ManyToOne
//    @JoinColumn(name = "coupon_id")
//    private Coupon coupon;
    private Long couponId;

// foreingn id plan id coupon id hau many to one use nhinkrna hai
    //interface padhna hai deep me
    //coupon ka entity banana hai

}
