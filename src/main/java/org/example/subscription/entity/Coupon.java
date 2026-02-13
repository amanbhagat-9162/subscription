package org.example.subscription.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;


import java.time.LocalDate;
import org.example.subscription.enums.CouponType;

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

    @Temporal(TemporalType.DATE)
    private Date expiryDate;

    @Enumerated(EnumType.STRING)
    private CouponType type = CouponType.PERCENTAGE;


}

// yre add krna hai coupon bahut type missing hai kon sa type kon se coupon se kya hoga like kitna discount hoga
//coupn multiple type ho sakte hai ek coupon ek hi liye valid nhi ho skate hai nn to ye dekhna hai
//sql se connect krna hai
// test likhna hai
//make a builder class ki user ko jitna  response chaiye utna hi le ske wo