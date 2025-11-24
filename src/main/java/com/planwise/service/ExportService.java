package com.planwise.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.planwise.dto.FinancialGoalResponse;
import com.planwise.dto.GoalInsights;
import com.planwise.dto.ProjectionData;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Service
public class ExportService {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    public Resource exportToCsv(java.util.List<ProjectionData> projections, FinancialGoalResponse goal) {
        StringWriter writer = new StringWriter();
        
        // Header
        writer.append("Date,Projected Value,Inflation Adjusted Value,Cumulative Contribution,Interest Earned\n");
        
        // Data rows
        for (ProjectionData projection : projections) {
            writer.append(projection.getDate().format(DATE_FORMATTER))
                    .append(",")
                    .append(projection.getProjectedValue().toString())
                    .append(",")
                    .append(projection.getInflationAdjustedValue().toString())
                    .append(",")
                    .append(projection.getCumulativeContribution().toString())
                    .append(",")
                    .append(projection.getInterestEarned().toString())
                    .append("\n");
        }
        
        byte[] bytes = writer.toString().getBytes();
        return new ByteArrayResource(bytes);
    }
    
    public Resource exportToPdf(java.util.List<ProjectionData> projections, 
                                 FinancialGoalResponse goal, 
                                 GoalInsights insights) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            
            // Title
            Paragraph title = new Paragraph("Financial Goal Report: " + goal.getName())
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(title);
            
            // Goal Details
            document.add(new Paragraph("Goal Details").setBold().setFontSize(14));
            document.add(new Paragraph("Target Amount: $" + goal.getTargetAmount()));
            document.add(new Paragraph("Current Amount: $" + goal.getCurrentAmount()));
            document.add(new Paragraph("Monthly Contribution: $" + goal.getMonthlyContribution()));
            document.add(new Paragraph("Time Horizon: " + goal.getTimeHorizonMonths() + " months"));
            document.add(new Paragraph("Expected Return Rate: " + goal.getExpectedReturnRate() + "%"));
            document.add(new Paragraph("Inflation Rate: " + goal.getInflationRate() + "%"));
            document.add(new Paragraph("\n"));
            
            // Insights
            document.add(new Paragraph("Projected Insights").setBold().setFontSize(14));
            document.add(new Paragraph("Projected Final Value: $" + insights.getProjectedFinalValue()));
            document.add(new Paragraph("Inflation Adjusted Value: $" + insights.getInflationAdjustedFinalValue()));
            document.add(new Paragraph("Required Monthly Contribution: $" + insights.getRequiredMonthlyContribution()));
            document.add(new Paragraph("Total Contributions: $" + insights.getTotalContributions()));
            document.add(new Paragraph("Total Interest Earned: $" + insights.getTotalInterestEarned()));
            if (insights.getCompletionProbability() != null) {
                document.add(new Paragraph("Completion Probability: " + 
                        String.format("%.2f", insights.getCompletionProbability()) + "%"));
            }
            document.add(new Paragraph("\n"));
            
            // Projections Table
            document.add(new Paragraph("Monthly Projections").setBold().setFontSize(14));
            
            Table table = new Table(UnitValue.createPercentArray(new float[]{2, 2, 2, 2, 2}))
                    .useAllAvailableWidth();
            
            // Header row
            table.addHeaderCell("Date");
            table.addHeaderCell("Projected Value");
            table.addHeaderCell("Inflation Adjusted");
            table.addHeaderCell("Contributions");
            table.addHeaderCell("Interest");
            
            // Data rows (limit to first 50 for PDF readability)
            int limit = Math.min(projections.size(), 50);
            for (int i = 0; i < limit; i++) {
                ProjectionData p = projections.get(i);
                table.addCell(p.getDate().format(DATE_FORMATTER));
                table.addCell("$" + p.getProjectedValue());
                table.addCell("$" + p.getInflationAdjustedValue());
                table.addCell("$" + p.getCumulativeContribution());
                table.addCell("$" + p.getInterestEarned());
            }
            
            document.add(table);
            
            if (projections.size() > 50) {
                document.add(new Paragraph("... and " + (projections.size() - 50) + " more rows"));
            }
            
            document.close();
            
            return new ByteArrayResource(baos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }
}

