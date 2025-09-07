package com.buurtinzicht.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a neighborhood scorecard with various quality of life metrics.
 * Scorecards are generated based on multiple data sources and provide comprehensive
 * neighborhood insights for users.
 */
@Entity
@Table(name = "neighborhood_scorecards")
public class NeighborhoodScorecard {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "neighborhood_id", nullable = false)
    private Neighborhood neighborhood;

    @Column(name = "overall_score", nullable = false, precision = 4, scale = 2)
    @DecimalMin(value = "0.0", message = "Overall score must be non-negative")
    @DecimalMax(value = "100.0", message = "Overall score cannot exceed 100")
    private BigDecimal overallScore;

    // Infrastructure Metrics
    @Column(name = "transportation_score", precision = 4, scale = 2)
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "100.0")
    private BigDecimal transportationScore;

    @Column(name = "public_services_score", precision = 4, scale = 2)
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "100.0")
    private BigDecimal publicServicesScore;

    @Column(name = "connectivity_score", precision = 4, scale = 2)
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "100.0")
    private BigDecimal connectivityScore;

    // Economic Metrics
    @Column(name = "cost_of_living_score", precision = 4, scale = 2)
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "100.0")
    private BigDecimal costOfLivingScore;

    @Column(name = "employment_score", precision = 4, scale = 2)
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "100.0")
    private BigDecimal employmentScore;

    @Column(name = "housing_market_score", precision = 4, scale = 2)
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "100.0")
    private BigDecimal housingMarketScore;

    // Social & Cultural Metrics
    @Column(name = "education_score", precision = 4, scale = 2)
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "100.0")
    private BigDecimal educationScore;

    @Column(name = "healthcare_score", precision = 4, scale = 2)
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "100.0")
    private BigDecimal healthcareScore;

    @Column(name = "cultural_amenities_score", precision = 4, scale = 2)
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "100.0")
    private BigDecimal culturalAmenitiesScore;

    @Column(name = "community_engagement_score", precision = 4, scale = 2)
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "100.0")
    private BigDecimal communityEngagementScore;

    // Environmental Metrics
    @Column(name = "air_quality_score", precision = 4, scale = 2)
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "100.0")
    private BigDecimal airQualityScore;

    @Column(name = "green_spaces_score", precision = 4, scale = 2)
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "100.0")
    private BigDecimal greenSpacesScore;

    @Column(name = "noise_pollution_score", precision = 4, scale = 2)
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "100.0")
    private BigDecimal noisePollutionScore;

    // Safety & Security Metrics
    @Column(name = "crime_safety_score", precision = 4, scale = 2)
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "100.0")
    private BigDecimal crimeSafetyScore;

    @Column(name = "emergency_services_score", precision = 4, scale = 2)
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "100.0")
    private BigDecimal emergencyServicesScore;

    // Data Quality & Confidence
    @Column(name = "data_completeness_percentage", precision = 5, scale = 2)
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "100.0")
    private BigDecimal dataCompletenessPercentage;

    @Column(name = "confidence_level", precision = 4, scale = 2)
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "100.0")
    private BigDecimal confidenceLevel;

    @Column(name = "data_sources_count")
    @Min(value = 0, message = "Data sources count must be non-negative")
    private Integer dataSourcesCount;

    // Versioning and Metadata
    @Column(name = "scorecard_version", nullable = false)
    @Pattern(regexp = "^\\d+\\.\\d+\\.\\d+$", message = "Version must follow semantic versioning (x.y.z)")
    private String scorecardVersion;

    @Column(name = "calculation_algorithm", nullable = false)
    @Size(max = 100, message = "Algorithm name cannot exceed 100 characters")
    private String calculationAlgorithm;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "is_published", nullable = false)
    private Boolean isPublished = false;

    @Column(name = "language_code", length = 5)
    @Pattern(regexp = "^(nl|fr|de|en)$", message = "Language must be one of: nl, fr, de, en")
    private String languageCode = "nl";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "version", nullable = false)
    @Version
    private Long version = 0L;

    // Constructors
    public NeighborhoodScorecard() {}

    public NeighborhoodScorecard(Neighborhood neighborhood, BigDecimal overallScore) {
        this.neighborhood = neighborhood;
        this.overallScore = overallScore;
        this.scorecardVersion = "1.0.0";
        this.calculationAlgorithm = "WEIGHTED_AVERAGE_V1";
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Neighborhood getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(Neighborhood neighborhood) {
        this.neighborhood = neighborhood;
    }

    public BigDecimal getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(BigDecimal overallScore) {
        this.overallScore = overallScore;
    }

    public BigDecimal getTransportationScore() {
        return transportationScore;
    }

    public void setTransportationScore(BigDecimal transportationScore) {
        this.transportationScore = transportationScore;
    }

    public BigDecimal getPublicServicesScore() {
        return publicServicesScore;
    }

    public void setPublicServicesScore(BigDecimal publicServicesScore) {
        this.publicServicesScore = publicServicesScore;
    }

    public BigDecimal getConnectivityScore() {
        return connectivityScore;
    }

    public void setConnectivityScore(BigDecimal connectivityScore) {
        this.connectivityScore = connectivityScore;
    }

    public BigDecimal getCostOfLivingScore() {
        return costOfLivingScore;
    }

    public void setCostOfLivingScore(BigDecimal costOfLivingScore) {
        this.costOfLivingScore = costOfLivingScore;
    }

    public BigDecimal getEmploymentScore() {
        return employmentScore;
    }

    public void setEmploymentScore(BigDecimal employmentScore) {
        this.employmentScore = employmentScore;
    }

    public BigDecimal getHousingMarketScore() {
        return housingMarketScore;
    }

    public void setHousingMarketScore(BigDecimal housingMarketScore) {
        this.housingMarketScore = housingMarketScore;
    }

    public BigDecimal getEducationScore() {
        return educationScore;
    }

    public void setEducationScore(BigDecimal educationScore) {
        this.educationScore = educationScore;
    }

    public BigDecimal getHealthcareScore() {
        return healthcareScore;
    }

    public void setHealthcareScore(BigDecimal healthcareScore) {
        this.healthcareScore = healthcareScore;
    }

    public BigDecimal getCulturalAmenitiesScore() {
        return culturalAmenitiesScore;
    }

    public void setCulturalAmenitiesScore(BigDecimal culturalAmenitiesScore) {
        this.culturalAmenitiesScore = culturalAmenitiesScore;
    }

    public BigDecimal getCommunityEngagementScore() {
        return communityEngagementScore;
    }

    public void setCommunityEngagementScore(BigDecimal communityEngagementScore) {
        this.communityEngagementScore = communityEngagementScore;
    }

    public BigDecimal getAirQualityScore() {
        return airQualityScore;
    }

    public void setAirQualityScore(BigDecimal airQualityScore) {
        this.airQualityScore = airQualityScore;
    }

    public BigDecimal getGreenSpacesScore() {
        return greenSpacesScore;
    }

    public void setGreenSpacesScore(BigDecimal greenSpacesScore) {
        this.greenSpacesScore = greenSpacesScore;
    }

    public BigDecimal getNoisePollutionScore() {
        return noisePollutionScore;
    }

    public void setNoisePollutionScore(BigDecimal noisePollutionScore) {
        this.noisePollutionScore = noisePollutionScore;
    }

    public BigDecimal getCrimeSafetyScore() {
        return crimeSafetyScore;
    }

    public void setCrimeSafetyScore(BigDecimal crimeSafetyScore) {
        this.crimeSafetyScore = crimeSafetyScore;
    }

    public BigDecimal getEmergencyServicesScore() {
        return emergencyServicesScore;
    }

    public void setEmergencyServicesScore(BigDecimal emergencyServicesScore) {
        this.emergencyServicesScore = emergencyServicesScore;
    }

    public BigDecimal getDataCompletenessPercentage() {
        return dataCompletenessPercentage;
    }

    public void setDataCompletenessPercentage(BigDecimal dataCompletenessPercentage) {
        this.dataCompletenessPercentage = dataCompletenessPercentage;
    }

    public BigDecimal getConfidenceLevel() {
        return confidenceLevel;
    }

    public void setConfidenceLevel(BigDecimal confidenceLevel) {
        this.confidenceLevel = confidenceLevel;
    }

    public Integer getDataSourcesCount() {
        return dataSourcesCount;
    }

    public void setDataSourcesCount(Integer dataSourcesCount) {
        this.dataSourcesCount = dataSourcesCount;
    }

    public String getScorecardVersion() {
        return scorecardVersion;
    }

    public void setScorecardVersion(String scorecardVersion) {
        this.scorecardVersion = scorecardVersion;
    }

    public String getCalculationAlgorithm() {
        return calculationAlgorithm;
    }

    public void setCalculationAlgorithm(String calculationAlgorithm) {
        this.calculationAlgorithm = calculationAlgorithm;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsPublished() {
        return isPublished;
    }

    public void setIsPublished(Boolean isPublished) {
        this.isPublished = isPublished;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    // Helper methods
    public boolean hasMinimumDataQuality() {
        return dataCompletenessPercentage != null && 
               dataCompletenessPercentage.compareTo(BigDecimal.valueOf(70.0)) >= 0;
    }

    public boolean isHighConfidence() {
        return confidenceLevel != null && 
               confidenceLevel.compareTo(BigDecimal.valueOf(80.0)) >= 0;
    }

    public boolean isReadyForPublication() {
        return hasMinimumDataQuality() && isHighConfidence() && overallScore != null;
    }
}