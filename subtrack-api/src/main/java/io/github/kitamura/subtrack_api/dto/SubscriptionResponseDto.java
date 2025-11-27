package io.github.kitamura.subtrack_api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponseDto {

    private Long id;
    private Long userId;
    private String name;
    private BigDecimal price;
    private String billingCycle;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate nextPaymentDate;
    private String category;
    private LocalDateTime createdAt;
    private boolean deleted;
}