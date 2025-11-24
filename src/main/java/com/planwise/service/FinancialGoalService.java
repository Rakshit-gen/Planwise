package com.planwise.service;

import com.planwise.dto.FinancialGoalRequest;
import com.planwise.dto.FinancialGoalResponse;
import com.planwise.dto.GoalInsights;
import com.planwise.dto.ProjectionData;
import com.planwise.entity.FinancialGoal;
import com.planwise.entity.User;
import com.planwise.mapper.FinancialGoalMapper;
import com.planwise.repository.FinancialGoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinancialGoalService {
    
    private final FinancialGoalRepository goalRepository;
    private final FinancialGoalMapper goalMapper;
    private final FinancialProjectionService projectionService;
    
    @Transactional(readOnly = true)
    public List<FinancialGoalResponse> getAllGoalsByUser(User user) {
        return goalRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(goalMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public FinancialGoalResponse getGoalById(Long id, User user) {
        FinancialGoal goal = findGoalByIdAndUser(id, user);
        return goalMapper.toResponse(goal);
    }
    
    @Transactional
    public FinancialGoalResponse createGoal(FinancialGoalRequest request, User user) {
        FinancialGoal goal = goalMapper.toEntity(request);
        goal.setUser(user);
        goal = goalRepository.save(goal);
        return goalMapper.toResponse(goal);
    }
    
    @Transactional
    public FinancialGoalResponse updateGoal(Long id, FinancialGoalRequest request, User user) {
        FinancialGoal goal = findGoalByIdAndUser(id, user);
        goalMapper.updateEntity(request, goal);
        goal = goalRepository.save(goal);
        return goalMapper.toResponse(goal);
    }
    
    @Transactional
    public void deleteGoal(Long id, User user) {
        FinancialGoal goal = findGoalByIdAndUser(id, user);
        goalRepository.delete(goal);
    }
    
    @Transactional(readOnly = true)
    public List<ProjectionData> getProjections(Long id, User user) {
        FinancialGoal goal = findGoalByIdAndUser(id, user);
        return projectionService.calculateProjection(goal);
    }
    
    @Transactional(readOnly = true)
    public GoalInsights getInsights(Long id, User user) {
        FinancialGoal goal = findGoalByIdAndUser(id, user);
        return projectionService.calculateInsights(goal);
    }
    
    private FinancialGoal findGoalByIdAndUser(Long id, User user) {
        return goalRepository.findById(id)
                .filter(goal -> goal.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Goal not found or access denied"));
    }
}

