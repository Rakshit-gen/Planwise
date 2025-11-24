package com.planwise.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectionData {
    private LocalDate date;
    private BigDecimal projectedValue;
    private BigDecimal inflationAdjustedValue;
    private BigDecimal cumulativeContribution;
    private BigDecimal interestEarned;
}

