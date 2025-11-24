package com.planwise.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class FinancialGoalRequest {
    
    @NotBlank(message = "Goal name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Target amount is required")
    @DecimalMin(value = "0.01", message = "Target amount must be greater than 0")
    private BigDecimal targetAmount;
    
    @NotNull(message = "Current amount is required")
    @DecimalMin(value = "0.0", message = "Current amount must be non-negative")
    private BigDecimal currentAmount;
    
    @NotNull(message = "Monthly contribution is required")
    @DecimalMin(value = "0.0", message = "Monthly contribution must be non-negative")
    private BigDecimal monthlyContribution;
    
    @NotNull(message = "Time horizon is required")
    @Min(value = 1, message = "Time horizon must be at least 1 month")
    private Integer timeHorizonMonths;
    
    @NotNull(message = "Expected return rate is required")
    @DecimalMin(value = "0.0", message = "Expected return rate must be non-negative")
    private BigDecimal expectedReturnRate;
    
    @NotNull(message = "Inflation rate is required")
    @DecimalMin(value = "0.0", message = "Inflation rate must be non-negative")
    private BigDecimal inflationRate;
    
    @NotNull(message = "Target date is required")
    private LocalDate targetDate;
}

