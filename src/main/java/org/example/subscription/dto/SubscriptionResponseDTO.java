package org.example.subscription.dto;

import lombok.*;
//import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionResponseDTO {

    private Long id;
    private String userName;
    private String planName;
//    private LocalDate startDate;
//    private LocalDate endDate;
    private Date startDate;
    private Date endDate;

    private String status;
}
