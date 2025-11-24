package com.planwise.controller;

import com.planwise.dto.ProjectionData;
import com.planwise.entity.User;
import com.planwise.service.FinancialGoalService;
import com.planwise.service.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/export")
@RequiredArgsConstructor
@Tag(name = "Export", description = "Export financial projections")
@SecurityRequirement(name = "bearerAuth")
public class ExportController {
    
    private final FinancialGoalService goalService;
    private final ExportService exportService;
    
    @GetMapping("/{id}/csv")
    @Operation(summary = "Export goal projections as CSV")
    public ResponseEntity<Resource> exportToCsv(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        var projections = goalService.getProjections(id, user);
        var goal = goalService.getGoalById(id, user);
        
        Resource resource = exportService.exportToCsv(projections, goal);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"goal-" + id + "-projections.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }
    
    @GetMapping("/{id}/pdf")
    @Operation(summary = "Export goal projections as PDF")
    public ResponseEntity<Resource> exportToPdf(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        var projections = goalService.getProjections(id, user);
        var goal = goalService.getGoalById(id, user);
        var insights = goalService.getInsights(id, user);
        
        Resource resource = exportService.exportToPdf(projections, goal, insights);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"goal-" + id + "-report.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
}

