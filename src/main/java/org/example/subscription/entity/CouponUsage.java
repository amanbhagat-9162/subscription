package org.example.subscription.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "coupon_usage", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "coupon_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @ManyToOne
//    private User user;
//
//    @ManyToOne
//    private Coupon coupon;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "coupon_id")
    private Long couponId;


    private Integer usageCount = 0;
}
