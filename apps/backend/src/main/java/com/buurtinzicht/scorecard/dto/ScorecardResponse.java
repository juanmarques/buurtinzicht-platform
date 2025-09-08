package com.buurtinzicht.scorecard.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Response DTO for neighborhood scorecard data.
 * Contains comprehensive scoring information with optional metadata.
 */
@Schema(description = "Neighborhood scorecard with comprehensive quality of life metrics")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScorecardResponse {

    @Schema(description = "Unique scorecard identifier")
    private String scorecardId;

    @Schema(description = "Neighborhood information")
    private NeighborhoodSummary neighborhood;

    @Schema(description = "Overall quality of life score (0-100)", example = "78.5")
    private BigDecimal overallScore;

    @Schema(description = "Overall score grade (A, B, C, D, F)", example = "B")
    private String overallGrade;

    @Schema(description = "Category scores breakdown")
    private CategoryScores categoryScores;

    @Schema(description = "Individual metric scores")
    private Map<String, MetricScore> metricScores;

    @Schema(description = "Scorecard metadata and data quality information")
    private ScorecardMetadata metadata;

    @Schema(description = "Regional and national comparison data")
    private ComparisonData comparison;

    @Schema(description = "Historic trend data (premium feature)")
    private List<HistoricScorePoint> historicTrends;

    @Schema(description = "Key insights and recommendations")
    private List<ScorecardInsight> insights;

    @Schema(description = "Generation timestamp")
    private LocalDateTime generatedAt;

    @Schema(description = "Time taken to generate scorecard (milliseconds)")
    private Long executionTimeMs;

    // Constructors
    public ScorecardResponse() {
        this.generatedAt = LocalDateTime.now();
    }

    // Nested Classes
    @Schema(description = "Neighborhood basic information")
    public static class NeighborhoodSummary {
        @Schema(description = "NIS code", example = "21004")
        private String nisCode;

        @Schema(description = "Neighborhood name", example = "Brussels")
        private String name;

        @Schema(description = "Localized name", example = "Bruxelles")
        private String localizedName;

        @Schema(description = "Municipality", example = "City of Brussels")
        private String municipality;

        @Schema(description = "Province", example = "Brussels-Capital Region")
        private String province;

        @Schema(description = "Region", example = "Brussels-Capital Region")
        private String region;

        @Schema(description = "Population", example = "179277")
        private Long population;

        @Schema(description = "Area in square kilometers", example = "32.61")
        private BigDecimal areaKm2;

        // Constructors, getters and setters
        public NeighborhoodSummary() {}

        public String getNisCode() { return nisCode; }
        public void setNisCode(String nisCode) { this.nisCode = nisCode; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getLocalizedName() { return localizedName; }
        public void setLocalizedName(String localizedName) { this.localizedName = localizedName; }
        
        public String getMunicipality() { return municipality; }
        public void setMunicipality(String municipality) { this.municipality = municipality; }
        
        public String getProvince() { return province; }
        public void setProvince(String province) { this.province = province; }
        
        public String getRegion() { return region; }
        public void setRegion(String region) { this.region = region; }
        
        public Long getPopulation() { return population; }
        public void setPopulation(Long population) { this.population = population; }
        
        public BigDecimal getAreaKm2() { return areaKm2; }
        public void setAreaKm2(BigDecimal areaKm2) { this.areaKm2 = areaKm2; }
    }

    @Schema(description = "Category-level scores")
    public static class CategoryScores {
        @Schema(description = "Infrastructure score (0-100)", example = "82.3")
        private BigDecimal infrastructure;

        @Schema(description = "Economic opportunity score (0-100)", example = "75.1")
        private BigDecimal economic;

        @Schema(description = "Social and cultural score (0-100)", example = "88.7")
        private BigDecimal socialCultural;

        @Schema(description = "Environmental quality score (0-100)", example = "71.9")
        private BigDecimal environmental;

        @Schema(description = "Safety and security score (0-100)", example = "79.4")
        private BigDecimal safety;

        // Constructors, getters and setters
        public CategoryScores() {}

        public BigDecimal getInfrastructure() { return infrastructure; }
        public void setInfrastructure(BigDecimal infrastructure) { this.infrastructure = infrastructure; }
        
        public BigDecimal getEconomic() { return economic; }
        public void setEconomic(BigDecimal economic) { this.economic = economic; }
        
        public BigDecimal getSocialCultural() { return socialCultural; }
        public void setSocialCultural(BigDecimal socialCultural) { this.socialCultural = socialCultural; }
        
        public BigDecimal getEnvironmental() { return environmental; }
        public void setEnvironmental(BigDecimal environmental) { this.environmental = environmental; }
        
        public BigDecimal getSafety() { return safety; }
        public void setSafety(BigDecimal safety) { this.safety = safety; }
    }

    @Schema(description = "Individual metric score with metadata")
    public static class MetricScore {
        @Schema(description = "Score value (0-100)", example = "85.2")
        private BigDecimal score;

        @Schema(description = "Weight used in calculation (0-1)", example = "0.25")
        private BigDecimal weight;

        @Schema(description = "Data completeness percentage", example = "92.5")
        private BigDecimal completeness;

        @Schema(description = "Confidence level", example = "88.1")
        private BigDecimal confidence;

        @Schema(description = "Number of data sources", example = "3")
        private Integer sourceCount;

        @Schema(description = "Last updated timestamp")
        private LocalDateTime lastUpdated;

        // Constructors, getters and setters
        public MetricScore() {}

        public MetricScore(BigDecimal score, BigDecimal weight) {
            this.score = score;
            this.weight = weight;
        }

        public BigDecimal getScore() { return score; }
        public void setScore(BigDecimal score) { this.score = score; }
        
        public BigDecimal getWeight() { return weight; }
        public void setWeight(BigDecimal weight) { this.weight = weight; }
        
        public BigDecimal getCompleteness() { return completeness; }
        public void setCompleteness(BigDecimal completeness) { this.completeness = completeness; }
        
        public BigDecimal getConfidence() { return confidence; }
        public void setConfidence(BigDecimal confidence) { this.confidence = confidence; }
        
        public Integer getSourceCount() { return sourceCount; }
        public void setSourceCount(Integer sourceCount) { this.sourceCount = sourceCount; }
        
        public LocalDateTime getLastUpdated() { return lastUpdated; }
        public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
    }

    @Schema(description = "Scorecard metadata and quality information")
    public static class ScorecardMetadata {
        @Schema(description = "Scorecard version", example = "1.0.0")
        private String version;

        @Schema(description = "Calculation algorithm used", example = "WEIGHTED_AVERAGE_V1")
        private String algorithm;

        @Schema(description = "Overall data completeness percentage", example = "87.3")
        private BigDecimal dataCompleteness;

        @Schema(description = "Overall confidence level", example = "91.2")
        private BigDecimal confidenceLevel;

        @Schema(description = "Total number of data sources", example = "12")
        private Integer totalDataSources;

        @Schema(description = "Languages available for this scorecard")
        private List<String> availableLanguages;

        // Constructors, getters and setters
        public ScorecardMetadata() {}

        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        
        public String getAlgorithm() { return algorithm; }
        public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }
        
        public BigDecimal getDataCompleteness() { return dataCompleteness; }
        public void setDataCompleteness(BigDecimal dataCompleteness) { this.dataCompleteness = dataCompleteness; }
        
        public BigDecimal getConfidenceLevel() { return confidenceLevel; }
        public void setConfidenceLevel(BigDecimal confidenceLevel) { this.confidenceLevel = confidenceLevel; }
        
        public Integer getTotalDataSources() { return totalDataSources; }
        public void setTotalDataSources(Integer totalDataSources) { this.totalDataSources = totalDataSources; }
        
        public List<String> getAvailableLanguages() { return availableLanguages; }
        public void setAvailableLanguages(List<String> availableLanguages) { this.availableLanguages = availableLanguages; }
    }

    @Schema(description = "Regional and national comparison data")
    public static class ComparisonData {
        @Schema(description = "Provincial average score", example = "74.2")
        private BigDecimal provincialAverage;

        @Schema(description = "Regional average score", example = "76.8")
        private BigDecimal regionalAverage;

        @Schema(description = "National average score", example = "72.1")
        private BigDecimal nationalAverage;

        @Schema(description = "Percentile rank nationally (0-100)", example = "78")
        private Integer nationalPercentile;

        @Schema(description = "Similar neighborhoods for comparison")
        private List<String> similarNeighborhoods;

        // Constructors, getters and setters
        public ComparisonData() {}

        public BigDecimal getProvincialAverage() { return provincialAverage; }
        public void setProvincialAverage(BigDecimal provincialAverage) { this.provincialAverage = provincialAverage; }
        
        public BigDecimal getRegionalAverage() { return regionalAverage; }
        public void setRegionalAverage(BigDecimal regionalAverage) { this.regionalAverage = regionalAverage; }
        
        public BigDecimal getNationalAverage() { return nationalAverage; }
        public void setNationalAverage(BigDecimal nationalAverage) { this.nationalAverage = nationalAverage; }
        
        public Integer getNationalPercentile() { return nationalPercentile; }
        public void setNationalPercentile(Integer nationalPercentile) { this.nationalPercentile = nationalPercentile; }
        
        public List<String> getSimilarNeighborhoods() { return similarNeighborhoods; }
        public void setSimilarNeighborhoods(List<String> similarNeighborhoods) { this.similarNeighborhoods = similarNeighborhoods; }
    }

    @Schema(description = "Historic score data point")
    public static class HistoricScorePoint {
        @Schema(description = "Score date")
        private LocalDateTime date;

        @Schema(description = "Overall score at that time", example = "76.3")
        private BigDecimal score;

        @Schema(description = "Category scores at that time")
        private CategoryScores categoryScores;

        // Constructors, getters and setters
        public HistoricScorePoint() {}

        public LocalDateTime getDate() { return date; }
        public void setDate(LocalDateTime date) { this.date = date; }
        
        public BigDecimal getScore() { return score; }
        public void setScore(BigDecimal score) { this.score = score; }
        
        public CategoryScores getCategoryScores() { return categoryScores; }
        public void setCategoryScores(CategoryScores categoryScores) { this.categoryScores = categoryScores; }
    }

    @Schema(description = "Scorecard insight and recommendation")
    public static class ScorecardInsight {
        @Schema(description = "Insight type", example = "STRENGTH")
        private String type;

        @Schema(description = "Category this insight relates to", example = "SOCIAL_CULTURAL")
        private String category;

        @Schema(description = "Localized insight text", example = "This neighborhood excels in cultural amenities")
        private String message;

        @Schema(description = "Impact level (LOW, MEDIUM, HIGH)", example = "HIGH")
        private String impact;

        @Schema(description = "Confidence in this insight (0-100)", example = "92.5")
        private BigDecimal confidence;

        // Constructors, getters and setters
        public ScorecardInsight() {}

        public ScorecardInsight(String type, String category, String message) {
            this.type = type;
            this.category = category;
            this.message = message;
        }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public String getImpact() { return impact; }
        public void setImpact(String impact) { this.impact = impact; }
        
        public BigDecimal getConfidence() { return confidence; }
        public void setConfidence(BigDecimal confidence) { this.confidence = confidence; }
    }

    // Main class getters and setters
    public String getScorecardId() { return scorecardId; }
    public void setScorecardId(String scorecardId) { this.scorecardId = scorecardId; }

    public NeighborhoodSummary getNeighborhood() { return neighborhood; }
    public void setNeighborhood(NeighborhoodSummary neighborhood) { this.neighborhood = neighborhood; }

    public BigDecimal getOverallScore() { return overallScore; }
    public void setOverallScore(BigDecimal overallScore) { this.overallScore = overallScore; }

    public String getOverallGrade() { return overallGrade; }
    public void setOverallGrade(String overallGrade) { this.overallGrade = overallGrade; }

    public CategoryScores getCategoryScores() { return categoryScores; }
    public void setCategoryScores(CategoryScores categoryScores) { this.categoryScores = categoryScores; }

    public Map<String, MetricScore> getMetricScores() { return metricScores; }
    public void setMetricScores(Map<String, MetricScore> metricScores) { this.metricScores = metricScores; }

    public ScorecardMetadata getMetadata() { return metadata; }
    public void setMetadata(ScorecardMetadata metadata) { this.metadata = metadata; }

    public ComparisonData getComparison() { return comparison; }
    public void setComparison(ComparisonData comparison) { this.comparison = comparison; }

    public List<HistoricScorePoint> getHistoricTrends() { return historicTrends; }
    public void setHistoricTrends(List<HistoricScorePoint> historicTrends) { this.historicTrends = historicTrends; }

    public List<ScorecardInsight> getInsights() { return insights; }
    public void setInsights(List<ScorecardInsight> insights) { this.insights = insights; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }

    public Long getExecutionTimeMs() { return executionTimeMs; }
    public void setExecutionTimeMs(Long executionTimeMs) { this.executionTimeMs = executionTimeMs; }

    // Helper methods
    public boolean isHighQualityScorecard() {
        return metadata != null && 
               metadata.getDataCompleteness() != null && 
               metadata.getDataCompleteness().compareTo(BigDecimal.valueOf(80.0)) >= 0 &&
               metadata.getConfidenceLevel() != null &&
               metadata.getConfidenceLevel().compareTo(BigDecimal.valueOf(85.0)) >= 0;
    }

    public String getGradeFromScore(BigDecimal score) {
        if (score == null) return "N/A";
        
        if (score.compareTo(BigDecimal.valueOf(90)) >= 0) return "A";
        if (score.compareTo(BigDecimal.valueOf(80)) >= 0) return "B";
        if (score.compareTo(BigDecimal.valueOf(70)) >= 0) return "C";
        if (score.compareTo(BigDecimal.valueOf(60)) >= 0) return "D";
        return "F";
    }
}