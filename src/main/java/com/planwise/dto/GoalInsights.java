package com.planwise.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalInsights {
    private BigDecimal requiredMonthlyContribution;
    private BigDecimal projectedFinalValue;
    private BigDecimal inflationAdjustedFinalValue;
    private BigDecimal projectedGrowth;
    private BigDecimal totalContributions;
    private BigDecimal totalInterestEarned;
    private Double completionProbability;
}

