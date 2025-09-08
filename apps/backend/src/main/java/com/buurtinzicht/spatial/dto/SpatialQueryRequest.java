package com.buurtinzicht.spatial.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(description = "Spatial query request for finding neighborhoods and points of interest")
public class SpatialQueryRequest {

    @Schema(description = "Latitude coordinate", example = "50.8505")
    @DecimalMin(value = "49.0", message = "Latitude must be within Belgian boundaries")
    @DecimalMax(value = "52.0", message = "Latitude must be within Belgian boundaries")
    private BigDecimal latitude;

    @Schema(description = "Longitude coordinate", example = "4.3488")
    @DecimalMin(value = "2.0", message = "Longitude must be within Belgian boundaries")
    @DecimalMax(value = "7.0", message = "Longitude must be within Belgian boundaries")
    private BigDecimal longitude;

    @Schema(description = "Search radius in kilometers", example = "5.0")
    @Positive(message = "Radius must be positive")
    private BigDecimal radiusKm;

    @Schema(description = "Maximum number of results to return", example = "10")
    @Positive(message = "Limit must be positive")
    private Integer limit = 10;

    @Schema(description = "Include neighborhood boundaries in response", example = "false")
    private Boolean includeBoundaries = false;

    @Schema(description = "Include population statistics", example = "true")
    private Boolean includeStatistics = true;

    @Schema(description = "Filter by urbanization level", allowableValues = {"RURAL", "SUBURBAN", "URBAN", "METROPOLITAN"})
    private String urbanizationLevel;

    @Schema(description = "Filter by minimum population", example = "1000")
    @Positive(message = "Population must be positive")
    private Long minPopulation;

    @Schema(description = "Filter by maximum population", example = "100000")
    @Positive(message = "Population must be positive")
    private Long maxPopulation;

    @Schema(description = "Filter by primary language", example = "nl", allowableValues = {"nl", "fr", "de"})
    private String primaryLanguage;

    public SpatialQueryRequest() {}

    public SpatialQueryRequest(BigDecimal latitude, BigDecimal longitude, BigDecimal radiusKm) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.radiusKm = radiusKm;
    }

    public boolean hasCoordinates() {
        return latitude != null && longitude != null;
    }

    public boolean hasPopulationFilter() {
        return minPopulation != null || maxPopulation != null;
    }

    // Getters and setters
    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }

    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }

    public BigDecimal getRadiusKm() { return radiusKm; }
    public void setRadiusKm(BigDecimal radiusKm) { this.radiusKm = radiusKm; }

    public Integer getLimit() { return limit; }
    public void setLimit(Integer limit) { this.limit = limit; }

    public Boolean getIncludeBoundaries() { return includeBoundaries; }
    public void setIncludeBoundaries(Boolean includeBoundaries) { this.includeBoundaries = includeBoundaries; }

    public Boolean getIncludeStatistics() { return includeStatistics; }
    public void setIncludeStatistics(Boolean includeStatistics) { this.includeStatistics = includeStatistics; }

    public String getUrbanizationLevel() { return urbanizationLevel; }
    public void setUrbanizationLevel(String urbanizationLevel) { this.urbanizationLevel = urbanizationLevel; }

    public Long getMinPopulation() { return minPopulation; }
    public void setMinPopulation(Long minPopulation) { this.minPopulation = minPopulation; }

    public Long getMaxPopulation() { return maxPopulation; }
    public void setMaxPopulation(Long maxPopulation) { this.maxPopulation = maxPopulation; }

    public String getPrimaryLanguage() { return primaryLanguage; }
    public void setPrimaryLanguage(String primaryLanguage) { this.primaryLanguage = primaryLanguage; }

    @Override
    public String toString() {
        return "SpatialQueryRequest{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", radiusKm=" + radiusKm +
                ", limit=" + limit +
                ", includeBoundaries=" + includeBoundaries +
                ", urbanizationLevel='" + urbanizationLevel + '\'' +
                ", primaryLanguage='" + primaryLanguage + '\'' +
                '}';
    }
}