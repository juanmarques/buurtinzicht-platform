package com.buurtinzicht.scorecard.dto;

import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Request DTO for generating neighborhood scorecards.
 * Allows clients to specify which metrics to include and their weightings.
 */
@Schema(description = "Request for generating neighborhood scorecard")
public class ScorecardRequest {

    @Schema(description = "Belgian NIS code of the neighborhood", example = "21004")
    @NotBlank(message = "NIS code is required")
    @Pattern(regexp = "^\\d{5}$", message = "NIS code must be exactly 5 digits")
    private String nisCode;

    @Schema(description = "Language for localized content", example = "nl", allowableValues = {"nl", "fr", "de", "en"})
    @Pattern(regexp = "^(nl|fr|de|en)$", message = "Language must be one of: nl, fr, de, en")
    private String language = "nl";

    @Schema(description = "Include detailed breakdown of sub-scores", example = "true")
    private Boolean includeBreakdown = true;

    @Schema(description = "Include confidence intervals and data quality metrics", example = "false")
    private Boolean includeMetadata = false;

    @Schema(description = "Custom weights for scorecard categories (0.0 to 1.0)")
    private Map<String, BigDecimal> customWeights;

    @Schema(description = "Specific metrics to include in calculation")
    private List<String> includedMetrics;

    @Schema(description = "Specific metrics to exclude from calculation")
    private List<String> excludedMetrics;

    @Schema(description = "Scorecard algorithm version to use", example = "1.0.0")
    @Pattern(regexp = "^\\d+\\.\\d+\\.\\d+$", message = "Version must follow semantic versioning (x.y.z)")
    private String algorithmVersion = "1.0.0";

    @Schema(description = "Minimum data completeness threshold (0-100)", example = "70.0")
    @DecimalMin(value = "0.0", message = "Minimum completeness must be non-negative")
    @DecimalMax(value = "100.0", message = "Minimum completeness cannot exceed 100")
    private BigDecimal minDataCompleteness = BigDecimal.valueOf(70.0);

    @Schema(description = "Include comparison with regional averages", example = "false")
    private Boolean includeComparison = false;

    @Schema(description = "Generate historic trend data (requires B2B subscription)", example = "false")
    private Boolean includeHistoricTrends = false;

    // Constructors
    public ScorecardRequest() {}

    public ScorecardRequest(String nisCode) {
        this.nisCode = nisCode;
    }

    public ScorecardRequest(String nisCode, String language) {
        this.nisCode = nisCode;
        this.language = language;
    }

    // Getters and Setters
    public String getNisCode() {
        return nisCode;
    }

    public void setNisCode(String nisCode) {
        this.nisCode = nisCode;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Boolean getIncludeBreakdown() {
        return includeBreakdown;
    }

    public void setIncludeBreakdown(Boolean includeBreakdown) {
        this.includeBreakdown = includeBreakdown;
    }

    public Boolean getIncludeMetadata() {
        return includeMetadata;
    }

    public void setIncludeMetadata(Boolean includeMetadata) {
        this.includeMetadata = includeMetadata;
    }

    public Map<String, BigDecimal> getCustomWeights() {
        return customWeights;
    }

    public void setCustomWeights(Map<String, BigDecimal> customWeights) {
        this.customWeights = customWeights;
    }

    public List<String> getIncludedMetrics() {
        return includedMetrics;
    }

    public void setIncludedMetrics(List<String> includedMetrics) {
        this.includedMetrics = includedMetrics;
    }

    public List<String> getExcludedMetrics() {
        return excludedMetrics;
    }

    public void setExcludedMetrics(List<String> excludedMetrics) {
        this.excludedMetrics = excludedMetrics;
    }

    public String getAlgorithmVersion() {
        return algorithmVersion;
    }

    public void setAlgorithmVersion(String algorithmVersion) {
        this.algorithmVersion = algorithmVersion;
    }

    public BigDecimal getMinDataCompleteness() {
        return minDataCompleteness;
    }

    public void setMinDataCompleteness(BigDecimal minDataCompleteness) {
        this.minDataCompleteness = minDataCompleteness;
    }

    public Boolean getIncludeComparison() {
        return includeComparison;
    }

    public void setIncludeComparison(Boolean includeComparison) {
        this.includeComparison = includeComparison;
    }

    public Boolean getIncludeHistoricTrends() {
        return includeHistoricTrends;
    }

    public void setIncludeHistoricTrends(Boolean includeHistoricTrends) {
        this.includeHistoricTrends = includeHistoricTrends;
    }

    // Helper methods
    public boolean hasCustomWeights() {
        return customWeights != null && !customWeights.isEmpty();
    }

    public boolean hasMetricFilters() {
        return (includedMetrics != null && !includedMetrics.isEmpty()) ||
               (excludedMetrics != null && !excludedMetrics.isEmpty());
    }

    public boolean isPremiumRequest() {
        return includeHistoricTrends || (includeComparison != null && includeComparison);
    }
}