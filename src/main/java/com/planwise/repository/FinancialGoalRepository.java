package com.planwise.repository;

import com.planwise.entity.FinancialGoal;
import com.planwise.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinancialGoalRepository extends JpaRepository<FinancialGoal, Long> {
    List<FinancialGoal> findByUser(User user);
    List<FinancialGoal> findByUserOrderByCreatedAtDesc(User user);
}

