package org.example.subscription.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionResponseDTO {

    private Long id;
    private String userName;
    private String planName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
}
