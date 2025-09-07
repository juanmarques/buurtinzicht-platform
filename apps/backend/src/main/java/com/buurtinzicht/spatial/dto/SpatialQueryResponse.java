package com.buurtinzicht.spatial.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Schema(description = "Spatial query response with neighborhood results")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpatialQueryResponse {

    @Schema(description = "List of neighborhoods found")
    private List<NeighborhoodResult> neighborhoods;

    @Schema(description = "Total number of results found", example = "15")
    private Integer totalResults;

    @Schema(description = "Number of results returned", example = "10")
    private Integer returnedResults;

    @Schema(description = "Search center point")
    private SearchPoint searchCenter;

    @Schema(description = "Search radius in kilometers", example = "5.0")
    private BigDecimal searchRadiusKm;

    @Schema(description = "Query execution time in milliseconds", example = "125")
    private Long executionTimeMs;

    @Schema(description = "Timestamp of query execution", example = "2025-01-15T10:30:00Z")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant queriedAt;

    public SpatialQueryResponse() {
        this.queriedAt = Instant.now();
    }

    public SpatialQueryResponse(List<NeighborhoodResult> neighborhoods) {
        this();
        this.neighborhoods = neighborhoods;
        this.returnedResults = neighborhoods != null ? neighborhoods.size() : 0;
    }

    @Schema(description = "Individual neighborhood result")
    public static class NeighborhoodResult {
        @Schema(description = "Neighborhood ID", example = "550e8400-e29b-41d4-a716-446655440000")
        private String id;

        @Schema(description = "NIS code", example = "21004")
        private String nisCode;

        @Schema(description = "Neighborhood name", example = "Brussels")
        private String name;

        @Schema(description = "Localized name based on request language")
        private String localizedName;

        @Schema(description = "Municipality name", example = "City of Brussels")
        private String municipality;

        @Schema(description = "Province name", example = "Brussels-Capital Region")
        private String province;

        @Schema(description = "Region name", example = "Brussels-Capital Region")
        private String region;

        @Schema(description = "Neighborhood centroid coordinates")
        private Point centroid;

        @Schema(description = "Distance from search point in kilometers", example = "2.5")
        private BigDecimal distanceKm;

        @Schema(description = "Area in square kilometers", example = "32.61")
        private BigDecimal areaKm2;

        @Schema(description = "Population count", example = "179277")
        private Long population;

        @Schema(description = "Population density per km²", example = "5747.3")
        private BigDecimal populationDensity;

        @Schema(description = "Primary language", example = "fr", allowableValues = {"nl", "fr", "de"})
        private String primaryLanguage;

        @Schema(description = "Urbanization level", example = "URBAN")
        private String urbanizationLevel;

        @Schema(description = "Postal codes in this neighborhood")
        private List<String> postalCodes;

        @Schema(description = "Elevation data")
        private ElevationData elevation;

        @Schema(description = "Neighborhood boundary (if requested)")
        private String boundaryGeoJson;

        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

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

        public Point getCentroid() { return centroid; }
        public void setCentroid(Point centroid) { this.centroid = centroid; }

        public BigDecimal getDistanceKm() { return distanceKm; }
        public void setDistanceKm(BigDecimal distanceKm) { this.distanceKm = distanceKm; }

        public BigDecimal getAreaKm2() { return areaKm2; }
        public void setAreaKm2(BigDecimal areaKm2) { this.areaKm2 = areaKm2; }

        public Long getPopulation() { return population; }
        public void setPopulation(Long population) { this.population = population; }

        public BigDecimal getPopulationDensity() { return populationDensity; }
        public void setPopulationDensity(BigDecimal populationDensity) { this.populationDensity = populationDensity; }

        public String getPrimaryLanguage() { return primaryLanguage; }
        public void setPrimaryLanguage(String primaryLanguage) { this.primaryLanguage = primaryLanguage; }

        public String getUrbanizationLevel() { return urbanizationLevel; }
        public void setUrbanizationLevel(String urbanizationLevel) { this.urbanizationLevel = urbanizationLevel; }

        public List<String> getPostalCodes() { return postalCodes; }
        public void setPostalCodes(List<String> postalCodes) { this.postalCodes = postalCodes; }

        public ElevationData getElevation() { return elevation; }
        public void setElevation(ElevationData elevation) { this.elevation = elevation; }

        public String getBoundaryGeoJson() { return boundaryGeoJson; }
        public void setBoundaryGeoJson(String boundaryGeoJson) { this.boundaryGeoJson = boundaryGeoJson; }
    }

    @Schema(description = "Geographic point")
    public static class Point {
        @Schema(description = "Latitude", example = "50.8505")
        private BigDecimal latitude;

        @Schema(description = "Longitude", example = "4.3488")
        private BigDecimal longitude;

        public Point() {}

        public Point(BigDecimal latitude, BigDecimal longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public BigDecimal getLatitude() { return latitude; }
        public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }

        public BigDecimal getLongitude() { return longitude; }
        public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
    }

    @Schema(description = "Search center point")
    public static class SearchPoint extends Point {
        @Schema(description = "Address if geocoded from address", example = "Koning Albert II-laan 35, 1000 Brussels")
        private String address;

        public SearchPoint() {}

        public SearchPoint(BigDecimal latitude, BigDecimal longitude) {
            super(latitude, longitude);
        }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
    }

    @Schema(description = "Elevation data")
    public static class ElevationData {
        @Schema(description = "Minimum elevation in meters", example = "15.2")
        private BigDecimal min;

        @Schema(description = "Maximum elevation in meters", example = "180.5")
        private BigDecimal max;

        @Schema(description = "Average elevation in meters", example = "85.3")
        private BigDecimal avg;

        public ElevationData() {}

        public ElevationData(BigDecimal min, BigDecimal max, BigDecimal avg) {
            this.min = min;
            this.max = max;
            this.avg = avg;
        }

        public BigDecimal getMin() { return min; }
        public void setMin(BigDecimal min) { this.min = min; }

        public BigDecimal getMax() { return max; }
        public void setMax(BigDecimal max) { this.max = max; }

        public BigDecimal getAvg() { return avg; }
        public void setAvg(BigDecimal avg) { this.avg = avg; }
    }

    // Main class getters and setters
    public List<NeighborhoodResult> getNeighborhoods() { return neighborhoods; }
    public void setNeighborhoods(List<NeighborhoodResult> neighborhoods) { 
        this.neighborhoods = neighborhoods;
        this.returnedResults = neighborhoods != null ? neighborhoods.size() : 0;
    }

    public Integer getTotalResults() { return totalResults; }
    public void setTotalResults(Integer totalResults) { this.totalResults = totalResults; }

    public Integer getReturnedResults() { return returnedResults; }
    public void setReturnedResults(Integer returnedResults) { this.returnedResults = returnedResults; }

    public SearchPoint getSearchCenter() { return searchCenter; }
    public void setSearchCenter(SearchPoint searchCenter) { this.searchCenter = searchCenter; }

    public BigDecimal getSearchRadiusKm() { return searchRadiusKm; }
    public void setSearchRadiusKm(BigDecimal searchRadiusKm) { this.searchRadiusKm = searchRadiusKm; }

    public Long getExecutionTimeMs() { return executionTimeMs; }
    public void setExecutionTimeMs(Long executionTimeMs) { this.executionTimeMs = executionTimeMs; }

    public Instant getQueriedAt() { return queriedAt; }
    public void setQueriedAt(Instant queriedAt) { this.queriedAt = queriedAt; }

    @Override
    public String toString() {
        return "SpatialQueryResponse{" +
                "totalResults=" + totalResults +
                ", returnedResults=" + returnedResults +
                ", searchRadiusKm=" + searchRadiusKm +
                ", executionTimeMs=" + executionTimeMs +
                '}';
    }
}