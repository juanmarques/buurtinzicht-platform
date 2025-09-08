package com.buurtinzicht.integration.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Schema(description = "Statistical data response for Belgian neighborhoods and municipalities")
public class StatisticalDataResponse {

    @Schema(description = "NIS code of the municipality", example = "21004")
    private String nisCode;

    @Schema(description = "Municipality name", example = "City of Brussels")
    private String municipalityName;

    @Schema(description = "Province name", example = "Brussels-Capital Region")
    private String province;

    @Schema(description = "Region name", example = "Brussels-Capital Region")
    private String region;

    @Schema(description = "Total population", example = "179277")
    private Long totalPopulation;

    @Schema(description = "Population density per km²", example = "5747.3")
    private BigDecimal populationDensity;

    @Schema(description = "Average age", example = "37.2")
    private BigDecimal averageAge;

    @Schema(description = "Median household income in EUR", example = "25500")
    private BigDecimal medianIncome;

    @Schema(description = "Unemployment rate percentage", example = "15.2")
    private BigDecimal unemploymentRate;

    @Schema(description = "Educational attainment - percentage with higher education", example = "45.8")
    private BigDecimal higherEducationRate;

    @Schema(description = "Crime rate per 1000 inhabitants", example = "85.4")
    private BigDecimal crimeRate;

    @Schema(description = "Housing prices - average per m²", example = "3250")
    private BigDecimal averageHousingPricePerSqm;

    @Schema(description = "Percentage of foreign nationals", example = "35.2")
    private BigDecimal foreignNationalRate;

    @Schema(description = "Number of households", example = "89156")
    private Long numberOfHouseholds;

    @Schema(description = "Average household size", example = "1.98")
    private BigDecimal averageHouseholdSize;

    @Schema(description = "Birth rate per 1000 inhabitants", example = "12.5")
    private BigDecimal birthRate;

    @Schema(description = "Death rate per 1000 inhabitants", example = "8.9")
    private BigDecimal deathRate;

    @Schema(description = "Primary language", example = "fr", allowableValues = {"nl", "fr", "de"})
    private String primaryLanguage;

    @Schema(description = "Data source identifier", example = "STATBEL")
    private String dataSource;

    @Schema(description = "Reference year for the statistics", example = "2023")
    private Integer referenceYear;

    @Schema(description = "Additional statistics as key-value pairs")
    private Map<String, Object> additionalStats;

    @Schema(description = "Timestamp when data was retrieved", example = "2025-01-15T10:30:00Z")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant retrievedAt;

    @Schema(description = "Data quality score (0-100)", example = "92.5")
    private Double dataQualityScore;

    public StatisticalDataResponse() {
        this.retrievedAt = Instant.now();
    }

    public StatisticalDataResponse(String nisCode, String municipalityName) {
        this();
        this.nisCode = nisCode;
        this.municipalityName = municipalityName;
    }

    public boolean hasPopulationData() {
        return totalPopulation != null && totalPopulation > 0;
    }

    public boolean hasEconomicData() {
        return medianIncome != null || unemploymentRate != null;
    }

    public boolean hasHousingData() {
        return averageHousingPricePerSqm != null;
    }

    public boolean hasDemographicData() {
        return averageAge != null || populationDensity != null || foreignNationalRate != null;
    }

    // Getters and setters
    public String getNisCode() { return nisCode; }
    public void setNisCode(String nisCode) { this.nisCode = nisCode; }

    public String getMunicipalityName() { return municipalityName; }
    public void setMunicipalityName(String municipalityName) { this.municipalityName = municipalityName; }

    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public Long getTotalPopulation() { return totalPopulation; }
    public void setTotalPopulation(Long totalPopulation) { this.totalPopulation = totalPopulation; }

    public BigDecimal getPopulationDensity() { return populationDensity; }
    public void setPopulationDensity(BigDecimal populationDensity) { this.populationDensity = populationDensity; }

    public BigDecimal getAverageAge() { return averageAge; }
    public void setAverageAge(BigDecimal averageAge) { this.averageAge = averageAge; }

    public BigDecimal getMedianIncome() { return medianIncome; }
    public void setMedianIncome(BigDecimal medianIncome) { this.medianIncome = medianIncome; }

    public BigDecimal getUnemploymentRate() { return unemploymentRate; }
    public void setUnemploymentRate(BigDecimal unemploymentRate) { this.unemploymentRate = unemploymentRate; }

    public BigDecimal getHigherEducationRate() { return higherEducationRate; }
    public void setHigherEducationRate(BigDecimal higherEducationRate) { this.higherEducationRate = higherEducationRate; }

    public BigDecimal getCrimeRate() { return crimeRate; }
    public void setCrimeRate(BigDecimal crimeRate) { this.crimeRate = crimeRate; }

    public BigDecimal getAverageHousingPricePerSqm() { return averageHousingPricePerSqm; }
    public void setAverageHousingPricePerSqm(BigDecimal averageHousingPricePerSqm) { this.averageHousingPricePerSqm = averageHousingPricePerSqm; }

    public BigDecimal getForeignNationalRate() { return foreignNationalRate; }
    public void setForeignNationalRate(BigDecimal foreignNationalRate) { this.foreignNationalRate = foreignNationalRate; }

    public Long getNumberOfHouseholds() { return numberOfHouseholds; }
    public void setNumberOfHouseholds(Long numberOfHouseholds) { this.numberOfHouseholds = numberOfHouseholds; }

    public BigDecimal getAverageHouseholdSize() { return averageHouseholdSize; }
    public void setAverageHouseholdSize(BigDecimal averageHouseholdSize) { this.averageHouseholdSize = averageHouseholdSize; }

    public BigDecimal getBirthRate() { return birthRate; }
    public void setBirthRate(BigDecimal birthRate) { this.birthRate = birthRate; }

    public BigDecimal getDeathRate() { return deathRate; }
    public void setDeathRate(BigDecimal deathRate) { this.deathRate = deathRate; }

    public String getPrimaryLanguage() { return primaryLanguage; }
    public void setPrimaryLanguage(String primaryLanguage) { this.primaryLanguage = primaryLanguage; }

    public String getDataSource() { return dataSource; }
    public void setDataSource(String dataSource) { this.dataSource = dataSource; }

    public Integer getReferenceYear() { return referenceYear; }
    public void setReferenceYear(Integer referenceYear) { this.referenceYear = referenceYear; }

    public Map<String, Object> getAdditionalStats() { return additionalStats; }
    public void setAdditionalStats(Map<String, Object> additionalStats) { this.additionalStats = additionalStats; }

    public Instant getRetrievedAt() { return retrievedAt; }
    public void setRetrievedAt(Instant retrievedAt) { this.retrievedAt = retrievedAt; }

    public Double getDataQualityScore() { return dataQualityScore; }
    public void setDataQualityScore(Double dataQualityScore) { this.dataQualityScore = dataQualityScore; }

    @Override
    public String toString() {
        return "StatisticalDataResponse{" +
                "nisCode='" + nisCode + '\'' +
                ", municipalityName='" + municipalityName + '\'' +
                ", province='" + province + '\'' +
                ", totalPopulation=" + totalPopulation +
                ", populationDensity=" + populationDensity +
                ", averageAge=" + averageAge +
                ", medianIncome=" + medianIncome +
                ", unemploymentRate=" + unemploymentRate +
                ", primaryLanguage='" + primaryLanguage + '\'' +
                ", dataSource='" + dataSource + '\'' +
                ", referenceYear=" + referenceYear +
                '}';
    }
}