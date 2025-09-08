package com.buurtinzicht.scorecard.service;

import com.buurtinzicht.domain.model.Neighborhood;
import com.buurtinzicht.domain.model.NeighborhoodScorecard;
import com.buurtinzicht.domain.repository.NeighborhoodRepository;
import com.buurtinzicht.scorecard.dto.ScorecardRequest;
import com.buurtinzicht.scorecard.dto.ScorecardResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for calculating neighborhood scorecards using various algorithms and weightings.
 * Provides comprehensive quality of life scoring with configurable metrics.
 */
@Service
@Transactional(readOnly = true)
public class ScorecardCalculationService {

    private static final Logger logger = LoggerFactory.getLogger(ScorecardCalculationService.class);

    // Default category weights (must sum to 1.0)
    private static final Map<String, BigDecimal> DEFAULT_WEIGHTS = Map.of(
        "infrastructure", BigDecimal.valueOf(0.25),
        "economic", BigDecimal.valueOf(0.25),
        "socialCultural", BigDecimal.valueOf(0.25),
        "environmental", BigDecimal.valueOf(0.15),
        "safety", BigDecimal.valueOf(0.10)
    );

    // Metric to category mapping
    private static final Map<String, String> METRIC_CATEGORIES = Map.of(
        "transportation", "infrastructure",
        "publicServices", "infrastructure", 
        "connectivity", "infrastructure",
        "costOfLiving", "economic",
        "employment", "economic",
        "housingMarket", "economic",
        "education", "socialCultural",
        "healthcare", "socialCultural",
        "culturalAmenities", "socialCultural",
        "communityEngagement", "socialCultural",
        "airQuality", "environmental",
        "greenSpaces", "environmental",
        "noisePollution", "environmental",
        "crimeSafety", "safety",
        "emergencyServices", "safety"
    );

    private final NeighborhoodRepository neighborhoodRepository;
    private final ScorecardMetricsService metricsService;

    @Autowired
    public ScorecardCalculationService(
            NeighborhoodRepository neighborhoodRepository,
            ScorecardMetricsService metricsService) {
        this.neighborhoodRepository = neighborhoodRepository;
        this.metricsService = metricsService;
    }

    /**
     * Generate a comprehensive scorecard for a neighborhood.
     */
    @Cacheable(value = "scorecards", key = "#request.nisCode + '_' + #request.language + '_' + #request.algorithmVersion")
    public ScorecardResponse generateScorecard(ScorecardRequest request) {
        logger.info("Generating scorecard for NIS code: {} in language: {}", 
            request.getNisCode(), request.getLanguage());

        long startTime = System.currentTimeMillis();

        try {
            // Find the neighborhood
            Optional<Neighborhood> neighborhoodOpt = neighborhoodRepository.findByNisCode(request.getNisCode());
            if (neighborhoodOpt.isEmpty()) {
                throw new IllegalArgumentException("Neighborhood not found: " + request.getNisCode());
            }

            Neighborhood neighborhood = neighborhoodOpt.get();

            // Calculate all individual metrics
            Map<String, BigDecimal> rawMetrics = metricsService.calculateAllMetrics(neighborhood);

            // Apply filters if specified
            Map<String, BigDecimal> filteredMetrics = applyMetricFilters(rawMetrics, request);

            // Calculate category scores
            Map<String, BigDecimal> categoryScores = calculateCategoryScores(filteredMetrics, request);

            // Calculate overall score
            BigDecimal overallScore = calculateOverallScore(categoryScores, request);

            // Build response
            ScorecardResponse response = buildScorecardResponse(
                neighborhood, overallScore, categoryScores, filteredMetrics, request);

            // Add optional features
            if (request.getIncludeComparison() != null && request.getIncludeComparison()) {
                response.setComparison(generateComparisonData(neighborhood, overallScore));
            }

            if (request.getIncludeHistoricTrends() != null && request.getIncludeHistoricTrends()) {
                response.setHistoricTrends(generateHistoricTrends(neighborhood));
            }

            // Add insights
            response.setInsights(generateInsights(neighborhood, categoryScores, filteredMetrics, request.getLanguage()));

            long executionTime = System.currentTimeMillis() - startTime;
            response.setExecutionTimeMs(executionTime);

            logger.info("Generated scorecard for {} (overall score: {}) in {}ms", 
                neighborhood.getName(), overallScore, executionTime);

            return response;

        } catch (Exception e) {
            logger.error("Error generating scorecard for {}: {}", request.getNisCode(), e.getMessage(), e);
            throw new RuntimeException("Failed to generate scorecard: " + e.getMessage(), e);
        }
    }

    /**
     * Calculate category scores by aggregating related metrics.
     */
    private Map<String, BigDecimal> calculateCategoryScores(
            Map<String, BigDecimal> metrics, ScorecardRequest request) {
        
        Map<String, List<BigDecimal>> categoryMetrics = new HashMap<>();
        
        // Group metrics by category
        for (Map.Entry<String, BigDecimal> entry : metrics.entrySet()) {
            String category = METRIC_CATEGORIES.get(entry.getKey());
            if (category != null) {
                categoryMetrics.computeIfAbsent(category, k -> new ArrayList<>()).add(entry.getValue());
            }
        }
        
        // Calculate average for each category
        Map<String, BigDecimal> categoryScores = new HashMap<>();
        for (Map.Entry<String, List<BigDecimal>> entry : categoryMetrics.entrySet()) {
            BigDecimal sum = entry.getValue().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal average = sum.divide(BigDecimal.valueOf(entry.getValue().size()), 2, RoundingMode.HALF_UP);
            categoryScores.put(entry.getKey(), average);
        }
        
        return categoryScores;
    }

    /**
     * Calculate overall score using weighted average of category scores.
     */
    private BigDecimal calculateOverallScore(
            Map<String, BigDecimal> categoryScores, ScorecardRequest request) {
        
        Map<String, BigDecimal> weights = getEffectiveWeights(request);
        BigDecimal totalScore = BigDecimal.ZERO;
        BigDecimal totalWeight = BigDecimal.ZERO;
        
        for (Map.Entry<String, BigDecimal> entry : categoryScores.entrySet()) {
            BigDecimal weight = weights.get(entry.getKey());
            if (weight != null && weight.compareTo(BigDecimal.ZERO) > 0) {
                totalScore = totalScore.add(entry.getValue().multiply(weight));
                totalWeight = totalWeight.add(weight);
            }
        }
        
        if (totalWeight.compareTo(BigDecimal.ZERO) == 0) {
            logger.warn("No valid weights found, using equal weighting");
            return categoryScores.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(categoryScores.size()), 2, RoundingMode.HALF_UP);
        }
        
        return totalScore.divide(totalWeight, 2, RoundingMode.HALF_UP);
    }

    /**
     * Apply metric inclusion/exclusion filters from request.
     */
    private Map<String, BigDecimal> applyMetricFilters(
            Map<String, BigDecimal> metrics, ScorecardRequest request) {
        
        Map<String, BigDecimal> filtered = new HashMap<>(metrics);
        
        // Apply exclusions first
        if (request.getExcludedMetrics() != null) {
            for (String excluded : request.getExcludedMetrics()) {
                filtered.remove(excluded);
            }
        }
        
        // Apply inclusions (if specified, only include these)
        if (request.getIncludedMetrics() != null && !request.getIncludedMetrics().isEmpty()) {
            filtered = filtered.entrySet().stream()
                .filter(entry -> request.getIncludedMetrics().contains(entry.getKey()))
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue
                ));
        }
        
        return filtered;
    }

    /**
     * Get effective weights (custom weights or defaults).
     */
    private Map<String, BigDecimal> getEffectiveWeights(ScorecardRequest request) {
        if (request.getCustomWeights() != null && !request.getCustomWeights().isEmpty()) {
            // Validate and normalize custom weights
            return normalizeWeights(request.getCustomWeights());
        }
        return DEFAULT_WEIGHTS;
    }

    /**
     * Normalize weights to sum to 1.0.
     */
    private Map<String, BigDecimal> normalizeWeights(Map<String, BigDecimal> weights) {
        BigDecimal sum = weights.values().stream()
            .filter(Objects::nonNull)
            .filter(w -> w.compareTo(BigDecimal.ZERO) > 0)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (sum.compareTo(BigDecimal.ZERO) == 0) {
            logger.warn("Invalid custom weights, using defaults");
            return DEFAULT_WEIGHTS;
        }
        
        return weights.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().divide(sum, 4, RoundingMode.HALF_UP)
            ));
    }

    /**
     * Build comprehensive scorecard response.
     */
    private ScorecardResponse buildScorecardResponse(
            Neighborhood neighborhood, BigDecimal overallScore, 
            Map<String, BigDecimal> categoryScores, Map<String, BigDecimal> metrics,
            ScorecardRequest request) {
        
        ScorecardResponse response = new ScorecardResponse();
        response.setScorecardId(UUID.randomUUID().toString());
        
        // Neighborhood summary
        ScorecardResponse.NeighborhoodSummary summary = new ScorecardResponse.NeighborhoodSummary();
        summary.setNisCode(neighborhood.getNisCode());
        summary.setName(neighborhood.getName());
        summary.setLocalizedName(neighborhood.getLocalizedName(request.getLanguage()));
        summary.setMunicipality(neighborhood.getMunicipality());
        summary.setProvince(neighborhood.getProvince());
        summary.setRegion(neighborhood.getRegion());
        summary.setPopulation(neighborhood.getPopulation());
        summary.setAreaKm2(neighborhood.getAreaKm2());
        response.setNeighborhood(summary);
        
        // Scores
        response.setOverallScore(overallScore);
        response.setOverallGrade(getGradeFromScore(overallScore));
        
        // Category scores
        ScorecardResponse.CategoryScores categories = new ScorecardResponse.CategoryScores();
        categories.setInfrastructure(categoryScores.get("infrastructure"));
        categories.setEconomic(categoryScores.get("economic"));
        categories.setSocialCultural(categoryScores.get("socialCultural"));
        categories.setEnvironmental(categoryScores.get("environmental"));
        categories.setSafety(categoryScores.get("safety"));
        response.setCategoryScores(categories);
        
        // Individual metric scores (if breakdown requested)
        if (request.getIncludeBreakdown() != null && request.getIncludeBreakdown()) {
            Map<String, ScorecardResponse.MetricScore> metricScores = new HashMap<>();
            Map<String, BigDecimal> weights = getEffectiveWeights(request);
            
            for (Map.Entry<String, BigDecimal> entry : metrics.entrySet()) {
                ScorecardResponse.MetricScore metricScore = new ScorecardResponse.MetricScore();
                metricScore.setScore(entry.getValue());
                
                String category = METRIC_CATEGORIES.get(entry.getKey());
                if (category != null && weights.containsKey(category)) {
                    metricScore.setWeight(weights.get(category));
                }
                
                metricScore.setCompleteness(BigDecimal.valueOf(90.0)); // Simulated
                metricScore.setConfidence(BigDecimal.valueOf(85.0)); // Simulated
                metricScore.setSourceCount(3); // Simulated
                metricScore.setLastUpdated(LocalDateTime.now());
                
                metricScores.put(entry.getKey(), metricScore);
            }
            response.setMetricScores(metricScores);
        }
        
        // Metadata (if requested)
        if (request.getIncludeMetadata() != null && request.getIncludeMetadata()) {
            ScorecardResponse.ScorecardMetadata metadata = new ScorecardResponse.ScorecardMetadata();
            metadata.setVersion(request.getAlgorithmVersion());
            metadata.setAlgorithm("WEIGHTED_AVERAGE_V1");
            metadata.setDataCompleteness(BigDecimal.valueOf(87.3)); // Simulated
            metadata.setConfidenceLevel(BigDecimal.valueOf(91.2)); // Simulated
            metadata.setTotalDataSources(12); // Simulated
            metadata.setAvailableLanguages(Arrays.asList("nl", "fr", "en", "de"));
            response.setMetadata(metadata);
        }
        
        return response;
    }

    /**
     * Generate comparison data with regional averages.
     */
    private ScorecardResponse.ComparisonData generateComparisonData(
            Neighborhood neighborhood, BigDecimal overallScore) {
        
        ScorecardResponse.ComparisonData comparison = new ScorecardResponse.ComparisonData();
        
        // Calculate simulated regional averages
        comparison.setProvincialAverage(calculateProvincialAverage(neighborhood.getProvince()));
        comparison.setRegionalAverage(calculateRegionalAverage(neighborhood.getRegion()));
        comparison.setNationalAverage(BigDecimal.valueOf(72.1));
        comparison.setNationalPercentile(calculatePercentile(overallScore));
        
        // Find similar neighborhoods (simulated)
        comparison.setSimilarNeighborhoods(Arrays.asList("11002", "44021", "31005"));
        
        return comparison;
    }

    /**
     * Generate historic trend data (premium feature).
     */
    private List<ScorecardResponse.HistoricScorePoint> generateHistoricTrends(Neighborhood neighborhood) {
        List<ScorecardResponse.HistoricScorePoint> trends = new ArrayList<>();
        
        // Generate simulated historic data (6 months back)
        LocalDateTime now = LocalDateTime.now();
        for (int i = 6; i >= 1; i--) {
            ScorecardResponse.HistoricScorePoint point = new ScorecardResponse.HistoricScorePoint();
            point.setDate(now.minusMonths(i));
            
            // Simulate slight variations over time
            BigDecimal variation = BigDecimal.valueOf((Math.random() - 0.5) * 4.0);
            point.setScore(BigDecimal.valueOf(75.0).add(variation));
            
            // Add simulated category trends
            ScorecardResponse.CategoryScores historical = new ScorecardResponse.CategoryScores();
            historical.setInfrastructure(BigDecimal.valueOf(80.0).add(variation));
            historical.setEconomic(BigDecimal.valueOf(72.0).add(variation));
            historical.setSocialCultural(BigDecimal.valueOf(85.0).add(variation));
            historical.setEnvironmental(BigDecimal.valueOf(70.0).add(variation));
            historical.setSafety(BigDecimal.valueOf(82.0).add(variation));
            point.setCategoryScores(historical);
            
            trends.add(point);
        }
        
        return trends;
    }

    /**
     * Generate actionable insights based on scores.
     */
    private List<ScorecardResponse.ScorecardInsight> generateInsights(
            Neighborhood neighborhood, Map<String, BigDecimal> categoryScores, 
            Map<String, BigDecimal> metrics, String language) {
        
        List<ScorecardResponse.ScorecardInsight> insights = new ArrayList<>();
        
        // Find strengths (categories scoring above 80)
        categoryScores.entrySet().stream()
            .filter(entry -> entry.getValue().compareTo(BigDecimal.valueOf(80.0)) > 0)
            .forEach(entry -> {
                ScorecardResponse.ScorecardInsight insight = new ScorecardResponse.ScorecardInsight();
                insight.setType("STRENGTH");
                insight.setCategory(entry.getKey().toUpperCase());
                insight.setMessage(getLocalizedStrengthMessage(entry.getKey(), language));
                insight.setImpact("HIGH");
                insight.setConfidence(BigDecimal.valueOf(92.5));
                insights.add(insight);
            });
        
        // Find improvement areas (categories scoring below 60)
        categoryScores.entrySet().stream()
            .filter(entry -> entry.getValue().compareTo(BigDecimal.valueOf(60.0)) < 0)
            .forEach(entry -> {
                ScorecardResponse.ScorecardInsight insight = new ScorecardResponse.ScorecardInsight();
                insight.setType("IMPROVEMENT_AREA");
                insight.setCategory(entry.getKey().toUpperCase());
                insight.setMessage(getLocalizedImprovementMessage(entry.getKey(), language));
                insight.setImpact("HIGH");
                insight.setConfidence(BigDecimal.valueOf(88.0));
                insights.add(insight);
            });
        
        // Add contextual insights based on neighborhood characteristics
        if (neighborhood.getUrbanizationLevel() == Neighborhood.UrbanizationLevel.METROPOLITAN) {
            ScorecardResponse.ScorecardInsight insight = new ScorecardResponse.ScorecardInsight();
            insight.setType("CONTEXT");
            insight.setCategory("INFRASTRUCTURE");
            insight.setMessage(getLocalizedMessage("metropolitan_context", language));
            insight.setImpact("MEDIUM");
            insight.setConfidence(BigDecimal.valueOf(85.0));
            insights.add(insight);
        }
        
        return insights;
    }

    // Helper methods

    private String getGradeFromScore(BigDecimal score) {
        if (score.compareTo(BigDecimal.valueOf(90)) >= 0) return "A";
        if (score.compareTo(BigDecimal.valueOf(80)) >= 0) return "B";
        if (score.compareTo(BigDecimal.valueOf(70)) >= 0) return "C";
        if (score.compareTo(BigDecimal.valueOf(60)) >= 0) return "D";
        return "F";
    }

    private BigDecimal calculateProvincialAverage(String province) {
        // Simulate provincial averages based on Belgian regional characteristics
        if (province == null) return BigDecimal.valueOf(70.0);
        
        return switch (province.toLowerCase()) {
            case "brussels-capital region" -> BigDecimal.valueOf(78.2);
            case "antwerp" -> BigDecimal.valueOf(74.8);
            case "east flanders" -> BigDecimal.valueOf(73.5);
            case "west flanders" -> BigDecimal.valueOf(72.1);
            case "flemish brabant" -> BigDecimal.valueOf(75.9);
            case "limburg" -> BigDecimal.valueOf(69.3);
            case "hainaut" -> BigDecimal.valueOf(67.8);
            case "liège" -> BigDecimal.valueOf(68.9);
            case "luxembourg" -> BigDecimal.valueOf(71.4);
            case "namur" -> BigDecimal.valueOf(70.2);
            case "walloon brabant" -> BigDecimal.valueOf(73.1);
            default -> BigDecimal.valueOf(70.0);
        };
    }

    private BigDecimal calculateRegionalAverage(String region) {
        if (region == null) return BigDecimal.valueOf(70.0);
        
        return switch (region.toLowerCase()) {
            case "brussels-capital region" -> BigDecimal.valueOf(76.8);
            case "flanders" -> BigDecimal.valueOf(73.4);
            case "wallonia" -> BigDecimal.valueOf(69.1);
            default -> BigDecimal.valueOf(70.0);
        };
    }

    private Integer calculatePercentile(BigDecimal score) {
        // Simple percentile calculation simulation
        if (score.compareTo(BigDecimal.valueOf(85)) >= 0) return 90;
        if (score.compareTo(BigDecimal.valueOf(80)) >= 0) return 80;
        if (score.compareTo(BigDecimal.valueOf(75)) >= 0) return 70;
        if (score.compareTo(BigDecimal.valueOf(70)) >= 0) return 60;
        if (score.compareTo(BigDecimal.valueOf(65)) >= 0) return 50;
        return 40;
    }

    private String getLocalizedStrengthMessage(String category, String language) {
        Map<String, Map<String, String>> messages = Map.of(
            "infrastructure", Map.of(
                "nl", "Dit gebied heeft uitstekende infrastructuur en vervoersmogelijkheden",
                "fr", "Cette zone dispose d'excellentes infrastructures et options de transport",
                "en", "This area has excellent infrastructure and transportation options",
                "de", "Diese Gegend verfügt über ausgezeichnete Infrastruktur und Verkehrsmöglichkeiten"
            ),
            "economic", Map.of(
                "nl", "Sterke economische mogelijkheden en werkgelegenheid in deze buurt",
                "fr", "Excellentes opportunités économiques et d'emploi dans ce quartier",
                "en", "Strong economic opportunities and employment in this neighborhood",
                "de", "Starke wirtschaftliche Möglichkeiten und Beschäftigung in dieser Nachbarschaft"
            )
        );
        
        return messages.getOrDefault(category, Map.of("nl", "Dit is een sterke categorie voor deze buurt"))
            .getOrDefault(language, "This is a strong category for this neighborhood");
    }

    private String getLocalizedImprovementMessage(String category, String language) {
        Map<String, Map<String, String>> messages = Map.of(
            "environmental", Map.of(
                "nl", "Milieuaspecten zoals luchtkwaliteit en groene ruimtes kunnen worden verbeterd",
                "fr", "Les aspects environnementaux comme la qualité de l'air et les espaces verts peuvent être améliorés",
                "en", "Environmental aspects like air quality and green spaces could be improved",
                "de", "Umweltaspekte wie Luftqualität und Grünflächen könnten verbessert werden"
            ),
            "safety", Map.of(
                "nl", "Veiligheid en beveiliging kunnen aandacht gebruiken in dit gebied",
                "fr", "La sécurité et la sûreté pourraient nécessiter une attention dans cette zone",
                "en", "Safety and security could use attention in this area",
                "de", "Sicherheit und Schutz könnten in diesem Bereich Aufmerksamkeit gebrauchen"
            )
        );
        
        return messages.getOrDefault(category, Map.of("nl", "Deze categorie heeft ruimte voor verbetering"))
            .getOrDefault(language, "This category has room for improvement");
    }

    private String getLocalizedMessage(String key, String language) {
        Map<String, Map<String, String>> messages = Map.of(
            "metropolitan_context", Map.of(
                "nl", "Als grootstedelijk gebied profiteert deze locatie van uitgebreide stedelijke voorzieningen",
                "fr", "En tant que zone métropolitaine, cet emplacement bénéficie d'équipements urbains étendus",
                "en", "As a metropolitan area, this location benefits from extensive urban amenities",
                "de", "Als Großstadtgebiet profitiert dieser Standort von umfangreichen städtischen Annehmlichkeiten"
            )
        );
        
        return messages.getOrDefault(key, Map.of("nl", "Algemene opmerking"))
            .getOrDefault(language, "General observation");
    }
}