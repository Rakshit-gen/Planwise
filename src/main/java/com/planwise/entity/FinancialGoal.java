package com.planwise.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "financial_goals")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialGoal {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal targetAmount;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal currentAmount;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal monthlyContribution;
    
    @Column(nullable = false)
    private Integer timeHorizonMonths;
    
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal expectedReturnRate;
    
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal inflationRate;
    
    @Column(nullable = false)
    private LocalDate targetDate;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

