package com.planwise.service;

import com.planwise.dto.GoalInsights;
import com.planwise.dto.ProjectionData;
import com.planwise.entity.FinancialGoal;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class FinancialProjectionService {
    
    private static final MathContext MC = new MathContext(10, RoundingMode.HALF_UP);
    private static final int MONTE_CARLO_ITERATIONS = 1000;
    
    /**
     * Calculate compound growth projection for a financial goal
     * Formula: FV = PV * (1 + r)^n + PMT * [((1 + r)^n - 1) / r]
     * Where:
     * FV = Future Value
     * PV = Present Value (current amount)
     * r = monthly return rate
     * n = number of months
     * PMT = monthly contribution
     */
    public List<ProjectionData> calculateProjection(FinancialGoal goal) {
        List<ProjectionData> projections = new ArrayList<>();
        
        BigDecimal currentValue = goal.getCurrentAmount();
        BigDecimal monthlyContribution = goal.getMonthlyContribution();
        BigDecimal monthlyReturnRate = goal.getExpectedReturnRate()
                .divide(BigDecimal.valueOf(100), MC)
                .divide(BigDecimal.valueOf(12), MC);
        BigDecimal inflationRate = goal.getInflationRate()
                .divide(BigDecimal.valueOf(100), MC);
        int months = goal.getTimeHorizonMonths();
        
        LocalDate startDate = LocalDate.now();
        BigDecimal cumulativeContribution = BigDecimal.ZERO;
        BigDecimal totalInterest = BigDecimal.ZERO;
        
        for (int month = 0; month <= months; month++) {
            LocalDate projectionDate = startDate.plusMonths(month);
            
            // Calculate future value with compound interest
            BigDecimal futureValue = calculateFutureValue(
                    currentValue,
                    monthlyContribution,
                    monthlyReturnRate,
                    month
            );
            
            // Calculate inflation-adjusted value
            BigDecimal years = BigDecimal.valueOf(month).divide(BigDecimal.valueOf(12), MC);
            BigDecimal inflationFactor = BigDecimal.ONE.add(inflationRate).pow(years.intValue(), MC);
            BigDecimal inflationAdjustedValue = futureValue.divide(inflationFactor, MC);
            
            // Track contributions and interest
            if (month > 0) {
                cumulativeContribution = cumulativeContribution.add(monthlyContribution);
            }
            
            BigDecimal principal = currentValue.add(cumulativeContribution);
            BigDecimal interestEarned = futureValue.subtract(principal);
            totalInterest = interestEarned;
            
            projections.add(ProjectionData.builder()
                    .date(projectionDate)
                    .projectedValue(futureValue.setScale(2, RoundingMode.HALF_UP))
                    .inflationAdjustedValue(inflationAdjustedValue.setScale(2, RoundingMode.HALF_UP))
                    .cumulativeContribution(cumulativeContribution.setScale(2, RoundingMode.HALF_UP))
                    .interestEarned(interestEarned.setScale(2, RoundingMode.HALF_UP))
                    .build());
        }
        
        return projections;
    }
    
    /**
     * Calculate future value using compound interest formula
     */
    private BigDecimal calculateFutureValue(
            BigDecimal presentValue,
            BigDecimal monthlyPayment,
            BigDecimal monthlyRate,
            int months
    ) {
        if (months == 0) {
            return presentValue;
        }
        
        // FV = PV * (1 + r)^n
        BigDecimal compoundFactor = BigDecimal.ONE.add(monthlyRate).pow(months, MC);
        BigDecimal futureValueFromPV = presentValue.multiply(compoundFactor, MC);
        
        // FV of annuity: PMT * [((1 + r)^n - 1) / r]
        BigDecimal futureValueOfAnnuity = BigDecimal.ZERO;
        if (monthlyRate.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal numerator = compoundFactor.subtract(BigDecimal.ONE);
            BigDecimal annuityFactor = numerator.divide(monthlyRate, MC);
            futureValueOfAnnuity = monthlyPayment.multiply(annuityFactor, MC);
        } else {
            futureValueOfAnnuity = monthlyPayment.multiply(BigDecimal.valueOf(months), MC);
        }
        
        return futureValueFromPV.add(futureValueOfAnnuity, MC);
    }
    
    /**
     * Calculate required monthly contribution to reach target
     * Formula: PMT = (FV - PV * (1 + r)^n) / [((1 + r)^n - 1) / r]
     */
    public BigDecimal calculateRequiredMonthlyContribution(FinancialGoal goal) {
        BigDecimal targetAmount = goal.getTargetAmount();
        BigDecimal currentValue = goal.getCurrentAmount();
        BigDecimal monthlyReturnRate = goal.getExpectedReturnRate()
                .divide(BigDecimal.valueOf(100), MC)
                .divide(BigDecimal.valueOf(12), MC);
        int months = goal.getTimeHorizonMonths();
        
        if (months == 0) {
            return targetAmount.subtract(currentValue);
        }
        
        BigDecimal compoundFactor = BigDecimal.ONE.add(monthlyReturnRate).pow(months, MC);
        BigDecimal futureValueFromPV = currentValue.multiply(compoundFactor, MC);
        BigDecimal remainingAmount = targetAmount.subtract(futureValueFromPV, MC);
        
        if (remainingAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        
        if (monthlyReturnRate.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal numerator = compoundFactor.subtract(BigDecimal.ONE);
            BigDecimal annuityFactor = numerator.divide(monthlyReturnRate, MC);
            return remainingAmount.divide(annuityFactor, MC);
        } else {
            return remainingAmount.divide(BigDecimal.valueOf(months), MC);
        }
    }
    
    /**
     * Calculate goal insights including projections and probabilities
     */
    public GoalInsights calculateInsights(FinancialGoal goal) {
        List<ProjectionData> projections = calculateProjection(goal);
        ProjectionData finalProjection = projections.get(projections.size() - 1);
        
        BigDecimal requiredMonthlyContribution = calculateRequiredMonthlyContribution(goal);
        BigDecimal projectedFinalValue = finalProjection.getProjectedValue();
        BigDecimal inflationAdjustedFinalValue = finalProjection.getInflationAdjustedValue();
        BigDecimal projectedGrowth = projectedFinalValue.subtract(goal.getCurrentAmount(), MC);
        BigDecimal totalContributions = finalProjection.getCumulativeContribution();
        BigDecimal totalInterestEarned = finalProjection.getInterestEarned();
        
        // Monte Carlo simulation for completion probability
        Double completionProbability = calculateCompletionProbability(goal);
        
        return GoalInsights.builder()
                .requiredMonthlyContribution(requiredMonthlyContribution.setScale(2, RoundingMode.HALF_UP))
                .projectedFinalValue(projectedFinalValue)
                .inflationAdjustedFinalValue(inflationAdjustedFinalValue)
                .projectedGrowth(projectedGrowth.setScale(2, RoundingMode.HALF_UP))
                .totalContributions(totalContributions)
                .totalInterestEarned(totalInterestEarned)
                .completionProbability(completionProbability)
                .build();
    }
    
    /**
     * Monte Carlo simulation to calculate goal completion probability
     */
    private Double calculateCompletionProbability(FinancialGoal goal) {
        Random random = new Random();
        int successCount = 0;
        
        BigDecimal targetAmount = goal.getTargetAmount();
        BigDecimal meanReturn = goal.getExpectedReturnRate();
        BigDecimal stdDev = meanReturn.multiply(BigDecimal.valueOf(0.3), MC); // 30% volatility
        
        for (int i = 0; i < MONTE_CARLO_ITERATIONS; i++) {
            BigDecimal currentValue = goal.getCurrentAmount();
            BigDecimal monthlyContribution = goal.getMonthlyContribution();
            int months = goal.getTimeHorizonMonths();
            
            for (int month = 0; month < months; month++) {
                // Generate random return rate with normal distribution
                double randomValue = random.nextGaussian();
                BigDecimal randomReturn = meanReturn.add(
                        stdDev.multiply(BigDecimal.valueOf(randomValue), MC)
                );
                BigDecimal monthlyRate = randomReturn.max(BigDecimal.valueOf(-0.99))
                        .divide(BigDecimal.valueOf(100), MC)
                        .divide(BigDecimal.valueOf(12), MC);
                
                // Apply compound growth
                currentValue = currentValue.multiply(
                        BigDecimal.ONE.add(monthlyRate), MC
                ).add(monthlyContribution, MC);
            }
            
            if (currentValue.compareTo(targetAmount) >= 0) {
                successCount++;
            }
        }
        
        return (double) successCount / MONTE_CARLO_ITERATIONS * 100;
    }
}

