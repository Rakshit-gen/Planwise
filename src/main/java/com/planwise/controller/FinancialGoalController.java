package com.planwise.controller;

import com.planwise.dto.FinancialGoalRequest;
import com.planwise.dto.FinancialGoalResponse;
import com.planwise.dto.GoalInsights;
import com.planwise.dto.ProjectionData;
import com.planwise.entity.User;
import com.planwise.service.FinancialGoalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/goals")
@RequiredArgsConstructor
@Tag(name = "Financial Goals", description = "Financial goal management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class FinancialGoalController {
    
    private final FinancialGoalService goalService;
    
    @GetMapping
    @Operation(summary = "Get all goals for the authenticated user")
    public ResponseEntity<List<FinancialGoalResponse>> getAllGoals(@AuthenticationPrincipal User user) {
        List<FinancialGoalResponse> goals = goalService.getAllGoalsByUser(user);
        return ResponseEntity.ok(goals);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get a specific goal by ID")
    public ResponseEntity<FinancialGoalResponse> getGoal(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        FinancialGoalResponse goal = goalService.getGoalById(id, user);
        return ResponseEntity.ok(goal);
    }
    
    @PostMapping
    @Operation(summary = "Create a new financial goal")
    public ResponseEntity<FinancialGoalResponse> createGoal(
            @Valid @RequestBody FinancialGoalRequest request,
            @AuthenticationPrincipal User user) {
        FinancialGoalResponse goal = goalService.createGoal(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(goal);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing financial goal")
    public ResponseEntity<FinancialGoalResponse> updateGoal(
            @PathVariable Long id,
            @Valid @RequestBody FinancialGoalRequest request,
            @AuthenticationPrincipal User user) {
        FinancialGoalResponse goal = goalService.updateGoal(id, request, user);
        return ResponseEntity.ok(goal);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a financial goal")
    public ResponseEntity<Void> deleteGoal(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        goalService.deleteGoal(id, user);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}/projections")
    @Operation(summary = "Get financial projections for a goal")
    public ResponseEntity<List<ProjectionData>> getProjections(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        List<ProjectionData> projections = goalService.getProjections(id, user);
        return ResponseEntity.ok(projections);
    }
    
    @GetMapping("/{id}/insights")
    @Operation(summary = "Get insights and analysis for a goal")
    public ResponseEntity<GoalInsights> getInsights(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        GoalInsights insights = goalService.getInsights(id, user);
        return ResponseEntity.ok(insights);
    }
}

