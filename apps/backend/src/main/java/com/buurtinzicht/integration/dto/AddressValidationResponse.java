package com.buurtinzicht.integration.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;

@Schema(description = "Address validation response with validation status and geocoding")
public class AddressValidationResponse {

    @Schema(description = "Whether the address was successfully validated", example = "true")
    private boolean valid;

    @Schema(description = "Validation confidence score (0-100)", example = "95.5")
    private Double confidenceScore;

    @Schema(description = "Standardized street name", example = "Koning Albert II-laan")
    private String standardizedStreet;

    @Schema(description = "Standardized house number", example = "35")
    private String standardizedHouseNumber;

    @Schema(description = "Standardized box number", example = "1")
    private String standardizedBoxNumber;

    @Schema(description = "Standardized postal code", example = "1000")
    private String standardizedPostalCode;

    @Schema(description = "Standardized city name", example = "Brussels")
    private String standardizedCity;

    @Schema(description = "Municipality name", example = "City of Brussels")
    private String municipality;

    @Schema(description = "Province name", example = "Brussels-Capital Region")
    private String province;

    @Schema(description = "Region name", example = "Brussels-Capital Region")
    private String region;

    @Schema(description = "Latitude coordinate", example = "50.8505")
    private BigDecimal latitude;

    @Schema(description = "Longitude coordinate", example = "4.3488")
    private BigDecimal longitude;

    @Schema(description = "National Institute for Statistics code (NIS)", example = "21004")
    private String nisCode;

    @Schema(description = "Language of the municipality", example = "nl", allowableValues = {"nl", "fr", "de"})
    private String language;

    @Schema(description = "Validation error message (if validation failed)")
    private String errorMessage;

    @Schema(description = "API source used for validation", example = "GEO6")
    private String source;

    @Schema(description = "Timestamp of validation", example = "2025-01-15T10:30:00Z")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant validatedAt;

    public AddressValidationResponse() {
        this.validatedAt = Instant.now();
    }

    public AddressValidationResponse(boolean valid) {
        this();
        this.valid = valid;
    }

    public String getFullStandardizedAddress() {
        StringBuilder sb = new StringBuilder();
        if (standardizedStreet != null) {
            sb.append(standardizedStreet);
        }
        if (standardizedHouseNumber != null) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(standardizedHouseNumber);
        }
        if (standardizedBoxNumber != null) {
            sb.append("/").append(standardizedBoxNumber);
        }
        if (standardizedPostalCode != null || standardizedCity != null) {
            if (sb.length() > 0) sb.append(", ");
            if (standardizedPostalCode != null) {
                sb.append(standardizedPostalCode);
            }
            if (standardizedCity != null) {
                if (standardizedPostalCode != null) sb.append(" ");
                sb.append(standardizedCity);
            }
        }
        return sb.toString();
    }

    public boolean hasGeoLocation() {
        return latitude != null && longitude != null;
    }

    // Getters and setters
    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }

    public Double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; }

    public String getStandardizedStreet() { return standardizedStreet; }
    public void setStandardizedStreet(String standardizedStreet) { this.standardizedStreet = standardizedStreet; }

    public String getStandardizedHouseNumber() { return standardizedHouseNumber; }
    public void setStandardizedHouseNumber(String standardizedHouseNumber) { this.standardizedHouseNumber = standardizedHouseNumber; }

    public String getStandardizedBoxNumber() { return standardizedBoxNumber; }
    public void setStandardizedBoxNumber(String standardizedBoxNumber) { this.standardizedBoxNumber = standardizedBoxNumber; }

    public String getStandardizedPostalCode() { return standardizedPostalCode; }
    public void setStandardizedPostalCode(String standardizedPostalCode) { this.standardizedPostalCode = standardizedPostalCode; }

    public String getStandardizedCity() { return standardizedCity; }
    public void setStandardizedCity(String standardizedCity) { this.standardizedCity = standardizedCity; }

    public String getMunicipality() { return municipality; }
    public void setMunicipality(String municipality) { this.municipality = municipality; }

    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }

    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }

    public String getNisCode() { return nisCode; }
    public void setNisCode(String nisCode) { this.nisCode = nisCode; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public Instant getValidatedAt() { return validatedAt; }
    public void setValidatedAt(Instant validatedAt) { this.validatedAt = validatedAt; }

    @Override
    public String toString() {
        return "AddressValidationResponse{" +
                "valid=" + valid +
                ", confidenceScore=" + confidenceScore +
                ", standardizedStreet='" + standardizedStreet + '\'' +
                ", standardizedHouseNumber='" + standardizedHouseNumber + '\'' +
                ", standardizedPostalCode='" + standardizedPostalCode + '\'' +
                ", standardizedCity='" + standardizedCity + '\'' +
                ", municipality='" + municipality + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", nisCode='" + nisCode + '\'' +
                ", source='" + source + '\'' +
                '}';
    }
}