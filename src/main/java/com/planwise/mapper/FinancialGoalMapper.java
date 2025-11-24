package com.planwise.mapper;

import com.planwise.dto.FinancialGoalRequest;
import com.planwise.dto.FinancialGoalResponse;
import com.planwise.entity.FinancialGoal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FinancialGoalMapper {
    
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    FinancialGoal toEntity(FinancialGoalRequest request);
    
    FinancialGoalResponse toResponse(FinancialGoal goal);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(FinancialGoalRequest request, @MappingTarget FinancialGoal goal);
}

