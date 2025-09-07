package com.buurtinzicht.web.controller;

import com.buurtinzicht.scorecard.dto.ScorecardRequest;
import com.buurtinzicht.scorecard.dto.ScorecardResponse;
import com.buurtinzicht.scorecard.service.ScorecardCalculationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * REST controller for neighborhood scorecard generation and retrieval.
 * Provides comprehensive quality of life metrics for Belgian neighborhoods.
 */
@RestController
@RequestMapping("/api/scorecards")
@Tag(name = "Scorecards", description = "Neighborhood quality of life scorecard operations")
@SecurityRequirement(name = "Bearer Authentication")
public class ScorecardController {

    private static final Logger logger = LoggerFactory.getLogger(ScorecardController.class);

    private final ScorecardCalculationService scorecardCalculationService;

    @Autowired
    public ScorecardController(ScorecardCalculationService scorecardCalculationService) {
        this.scorecardCalculationService = scorecardCalculationService;
    }

    @Operation(
        summary = "Generate neighborhood scorecard",
        description = "Generate a comprehensive quality of life scorecard for a Belgian neighborhood. " +
                     "Includes metrics across infrastructure, economic, social/cultural, environmental, and safety categories."
    )
    @ApiResponse(responseCode = "200", description = "Scorecard generated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    @ApiResponse(responseCode = "404", description = "Neighborhood not found")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('B2C_USER', 'B2B_USER', 'ADMIN')")
    public ResponseEntity<ScorecardResponse> generateScorecard(
            @Valid @RequestBody ScorecardRequest request) {
        
        logger.info("Generating scorecard for NIS code: {} in language: {}", 
            request.getNisCode(), request.getLanguage());

        try {
            ScorecardResponse response = scorecardCalculationService.generateScorecard(request);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request for scorecard generation: {}", e.getMessage());
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            logger.error("Error generating scorecard: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
        summary = "Get basic neighborhood scorecard",
        description = "Get a basic scorecard for a neighborhood using default settings. " +
                     "For custom weightings and advanced features, use the POST endpoint."
    )
    @ApiResponse(responseCode = "200", description = "Scorecard retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Neighborhood not found")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @GetMapping("/{nisCode}")
    @PreAuthorize("hasAnyRole('B2C_USER', 'B2B_USER', 'ADMIN')")
    public ResponseEntity<ScorecardResponse> getBasicScorecard(
            @Parameter(description = "Belgian NIS code", example = "21004")
            @PathVariable String nisCode,
            @Parameter(description = "Language for localized content", example = "nl")
            @RequestParam(defaultValue = "nl") String language,
            @Parameter(description = "Include detailed breakdown", example = "true")
            @RequestParam(defaultValue = "true") Boolean includeBreakdown) {
        
        logger.info("Getting basic scorecard for NIS code: {} in language: {}", nisCode, language);

        ScorecardRequest request = new ScorecardRequest(nisCode, language);
        request.setIncludeBreakdown(includeBreakdown);
        request.setIncludeMetadata(false);
        request.setIncludeComparison(false);
        request.setIncludeHistoricTrends(false);

        try {
            ScorecardResponse response = scorecardCalculationService.generateScorecard(request);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.warn("Neighborhood not found: {}", nisCode);
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            logger.error("Error generating basic scorecard: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
        summary = "Get premium scorecard with comparisons",
        description = "Get an enhanced scorecard with regional comparisons and historic trends. " +
                     "Requires B2B subscription for full features."
    )
    @ApiResponse(responseCode = "200", description = "Premium scorecard retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Premium features require B2B subscription")
    @ApiResponse(responseCode = "404", description = "Neighborhood not found")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @GetMapping("/{nisCode}/premium")
    @PreAuthorize("hasAnyRole('B2B_USER', 'ADMIN')")
    public ResponseEntity<ScorecardResponse> getPremiumScorecard(
            @Parameter(description = "Belgian NIS code", example = "21004")
            @PathVariable String nisCode,
            @Parameter(description = "Language for localized content", example = "nl")
            @RequestParam(defaultValue = "nl") String language,
            @Parameter(description = "Include historic trend data", example = "true")
            @RequestParam(defaultValue = "true") Boolean includeHistoricTrends) {
        
        logger.info("Getting premium scorecard for NIS code: {} in language: {}", nisCode, language);

        ScorecardRequest request = new ScorecardRequest(nisCode, language);
        request.setIncludeBreakdown(true);
        request.setIncludeMetadata(true);
        request.setIncludeComparison(true);
        request.setIncludeHistoricTrends(includeHistoricTrends);

        try {
            ScorecardResponse response = scorecardCalculationService.generateScorecard(request);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.warn("Neighborhood not found: {}", nisCode);
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            logger.error("Error generating premium scorecard: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
        summary = "Compare neighborhood scorecards",
        description = "Compare scorecards between two neighborhoods side by side. " +
                     "Returns both scorecards with additional comparison metrics."
    )
    @ApiResponse(responseCode = "200", description = "Comparison completed successfully")
    @ApiResponse(responseCode = "400", description = "Invalid NIS codes provided")
    @ApiResponse(responseCode = "404", description = "One or both neighborhoods not found")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @GetMapping("/compare")
    @PreAuthorize("hasAnyRole('B2C_USER', 'B2B_USER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> compareNeighborhoods(
            @Parameter(description = "First neighborhood NIS code", example = "21004")
            @RequestParam String nis1,
            @Parameter(description = "Second neighborhood NIS code", example = "11002")
            @RequestParam String nis2,
            @Parameter(description = "Language for localized content", example = "nl")
            @RequestParam(defaultValue = "nl") String language) {
        
        logger.info("Comparing neighborhoods: {} vs {}", nis1, nis2);

        if (nis1.equals(nis2)) {
            logger.warn("Cannot compare neighborhood with itself: {}", nis1);
            return ResponseEntity.badRequest().build();
        }

        try {
            // Generate scorecards for both neighborhoods
            ScorecardRequest request1 = new ScorecardRequest(nis1, language);
            request1.setIncludeBreakdown(true);
            ScorecardResponse scorecard1 = scorecardCalculationService.generateScorecard(request1);

            ScorecardRequest request2 = new ScorecardRequest(nis2, language);
            request2.setIncludeBreakdown(true);
            ScorecardResponse scorecard2 = scorecardCalculationService.generateScorecard(request2);

            // Build comparison response
            Map<String, Object> comparison = Map.of(
                "neighborhood1", scorecard1,
                "neighborhood2", scorecard2,
                "comparison", Map.of(
                    "overallScoreDifference", 
                    scorecard1.getOverallScore().subtract(scorecard2.getOverallScore()),
                    "strongerIn", analyzeStrengths(scorecard1, scorecard2),
                    "summary", generateComparisonSummary(scorecard1, scorecard2, language)
                )
            );

            return ResponseEntity.ok(comparison);

        } catch (IllegalArgumentException e) {
            logger.warn("One or both neighborhoods not found: {} / {}", nis1, nis2);
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            logger.error("Error comparing neighborhoods: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
        summary = "Get available scorecard metrics",
        description = "Get a list of all available metrics and categories that can be included in scorecards."
    )
    @ApiResponse(responseCode = "200", description = "Metrics list retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @GetMapping("/metrics")
    @PreAuthorize("hasAnyRole('B2C_USER', 'B2B_USER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getAvailableMetrics(
            @Parameter(description = "Language for localized descriptions", example = "nl")
            @RequestParam(defaultValue = "nl") String language) {
        
        logger.debug("Getting available metrics in language: {}", language);

        Map<String, Object> metrics = Map.of(
            "categories", List.of(
                Map.of("id", "infrastructure", "name", getLocalizedCategoryName("infrastructure", language), 
                       "weight", 0.25, "description", getLocalizedCategoryDescription("infrastructure", language)),
                Map.of("id", "economic", "name", getLocalizedCategoryName("economic", language), 
                       "weight", 0.25, "description", getLocalizedCategoryDescription("economic", language)),
                Map.of("id", "socialCultural", "name", getLocalizedCategoryName("socialCultural", language), 
                       "weight", 0.25, "description", getLocalizedCategoryDescription("socialCultural", language)),
                Map.of("id", "environmental", "name", getLocalizedCategoryName("environmental", language), 
                       "weight", 0.15, "description", getLocalizedCategoryDescription("environmental", language)),
                Map.of("id", "safety", "name", getLocalizedCategoryName("safety", language), 
                       "weight", 0.10, "description", getLocalizedCategoryDescription("safety", language))
            ),
            "metrics", List.of(
                Map.of("id", "transportation", "category", "infrastructure", 
                       "name", getLocalizedMetricName("transportation", language)),
                Map.of("id", "publicServices", "category", "infrastructure", 
                       "name", getLocalizedMetricName("publicServices", language)),
                Map.of("id", "connectivity", "category", "infrastructure", 
                       "name", getLocalizedMetricName("connectivity", language)),
                Map.of("id", "costOfLiving", "category", "economic", 
                       "name", getLocalizedMetricName("costOfLiving", language)),
                Map.of("id", "employment", "category", "economic", 
                       "name", getLocalizedMetricName("employment", language)),
                Map.of("id", "housingMarket", "category", "economic", 
                       "name", getLocalizedMetricName("housingMarket", language)),
                Map.of("id", "education", "category", "socialCultural", 
                       "name", getLocalizedMetricName("education", language)),
                Map.of("id", "healthcare", "category", "socialCultural", 
                       "name", getLocalizedMetricName("healthcare", language)),
                Map.of("id", "culturalAmenities", "category", "socialCultural", 
                       "name", getLocalizedMetricName("culturalAmenities", language)),
                Map.of("id", "communityEngagement", "category", "socialCultural", 
                       "name", getLocalizedMetricName("communityEngagement", language)),
                Map.of("id", "airQuality", "category", "environmental", 
                       "name", getLocalizedMetricName("airQuality", language)),
                Map.of("id", "greenSpaces", "category", "environmental", 
                       "name", getLocalizedMetricName("greenSpaces", language)),
                Map.of("id", "noisePollution", "category", "environmental", 
                       "name", getLocalizedMetricName("noisePollution", language)),
                Map.of("id", "crimeSafety", "category", "safety", 
                       "name", getLocalizedMetricName("crimeSafety", language)),
                Map.of("id", "emergencyServices", "category", "safety", 
                       "name", getLocalizedMetricName("emergencyServices", language))
            ),
            "algorithms", List.of(
                Map.of("version", "1.0.0", "name", "Weighted Average V1", "default", true)
            ),
            "languages", List.of("nl", "fr", "en", "de")
        );

        return ResponseEntity.ok(metrics);
    }

    // Helper methods

    private Map<String, String> analyzeStrengths(ScorecardResponse scorecard1, ScorecardResponse scorecard2) {
        Map<String, String> strengths = Map.of(
            "infrastructure", determineStronger(scorecard1.getCategoryScores().getInfrastructure(), 
                                               scorecard2.getCategoryScores().getInfrastructure()),
            "economic", determineStronger(scorecard1.getCategoryScores().getEconomic(), 
                                         scorecard2.getCategoryScores().getEconomic()),
            "socialCultural", determineStronger(scorecard1.getCategoryScores().getSocialCultural(), 
                                               scorecard2.getCategoryScores().getSocialCultural()),
            "environmental", determineStronger(scorecard1.getCategoryScores().getEnvironmental(), 
                                              scorecard2.getCategoryScores().getEnvironmental()),
            "safety", determineStronger(scorecard1.getCategoryScores().getSafety(), 
                                       scorecard2.getCategoryScores().getSafety())
        );
        return strengths;
    }

    private String determineStronger(BigDecimal score1, BigDecimal score2) {
        if (score1 == null && score2 == null) return "tie";
        if (score1 == null) return "neighborhood2";
        if (score2 == null) return "neighborhood1";
        
        int comparison = score1.compareTo(score2);
        if (comparison > 0) return "neighborhood1";
        if (comparison < 0) return "neighborhood2";
        return "tie";
    }

    private String generateComparisonSummary(ScorecardResponse scorecard1, ScorecardResponse scorecard2, String language) {
        BigDecimal diff = scorecard1.getOverallScore().subtract(scorecard2.getOverallScore());
        String stronger = scorecard1.getNeighborhood().getName();
        String weaker = scorecard2.getNeighborhood().getName();
        
        if (diff.compareTo(BigDecimal.ZERO) < 0) {
            stronger = scorecard2.getNeighborhood().getName();
            weaker = scorecard1.getNeighborhood().getName();
            diff = diff.abs();
        }
        
        return switch (language) {
            case "fr" -> String.format("%s score %.1f points de plus que %s", stronger, diff, weaker);
            case "en" -> String.format("%s scores %.1f points higher than %s", stronger, diff, weaker);
            case "de" -> String.format("%s punktet %.1f Punkte höher als %s", stronger, diff, weaker);
            default -> String.format("%s scoort %.1f punten hoger dan %s", stronger, diff, weaker);
        };
    }

    // Localization helper methods

    private String getLocalizedCategoryName(String category, String language) {
        Map<String, Map<String, String>> names = Map.of(
            "infrastructure", Map.of("nl", "Infrastructuur", "fr", "Infrastructure", "en", "Infrastructure", "de", "Infrastruktur"),
            "economic", Map.of("nl", "Economie", "fr", "Économie", "en", "Economy", "de", "Wirtschaft"),
            "socialCultural", Map.of("nl", "Sociaal & Cultureel", "fr", "Social & Culturel", "en", "Social & Cultural", "de", "Sozial & Kulturell"),
            "environmental", Map.of("nl", "Milieu", "fr", "Environnement", "en", "Environment", "de", "Umwelt"),
            "safety", Map.of("nl", "Veiligheid", "fr", "Sécurité", "en", "Safety", "de", "Sicherheit")
        );
        return names.getOrDefault(category, Map.of()).getOrDefault(language, category);
    }

    private String getLocalizedCategoryDescription(String category, String language) {
        // Simplified for brevity - in practice would have comprehensive descriptions
        return getLocalizedCategoryName(category, language) + " " + 
               (language.equals("nl") ? "metriek" : "metrics");
    }

    private String getLocalizedMetricName(String metric, String language) {
        Map<String, Map<String, String>> names = Map.of(
            "transportation", Map.of("nl", "Vervoer", "fr", "Transport", "en", "Transportation", "de", "Verkehr"),
            "publicServices", Map.of("nl", "Openbare Diensten", "fr", "Services Publics", "en", "Public Services", "de", "Öffentliche Dienste"),
            "connectivity", Map.of("nl", "Connectiviteit", "fr", "Connectivité", "en", "Connectivity", "de", "Konnektivität"),
            "education", Map.of("nl", "Onderwijs", "fr", "Éducation", "en", "Education", "de", "Bildung"),
            "healthcare", Map.of("nl", "Gezondheidszorg", "fr", "Soins de Santé", "en", "Healthcare", "de", "Gesundheitswesen")
        );
        return names.getOrDefault(metric, Map.of()).getOrDefault(language, metric);
    }
}