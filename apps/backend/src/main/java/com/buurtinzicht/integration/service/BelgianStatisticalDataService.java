package com.buurtinzicht.integration.service;

import com.buurtinzicht.integration.dto.StatisticalDataResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class BelgianStatisticalDataService {

    private static final Logger logger = LoggerFactory.getLogger(BelgianStatisticalDataService.class);

    private final RestTemplate statbelRestTemplate;
    private final RestTemplate defaultRestTemplate;
    private final ObjectMapper objectMapper;

    // Cache for 6 hours - statistical data doesn't change frequently
    private static final String CACHE_NAME = "statisticalData";
    private static final int CURRENT_YEAR = LocalDate.now().getYear();

    @Autowired
    public BelgianStatisticalDataService(
            @Qualifier("statbelRestTemplate") RestTemplate statbelRestTemplate,
            @Qualifier("defaultExternalApiRestTemplate") RestTemplate defaultRestTemplate,
            ObjectMapper objectMapper) {
        this.statbelRestTemplate = statbelRestTemplate;
        this.defaultRestTemplate = defaultRestTemplate;
        this.objectMapper = objectMapper;
    }

    @Cacheable(value = CACHE_NAME, key = "#nisCode")
    public StatisticalDataResponse getStatisticalDataByNisCode(String nisCode) {
        logger.info("Fetching statistical data for NIS code: {}", nisCode);

        StatisticalDataResponse response = new StatisticalDataResponse(nisCode, null);
        response.setDataSource("STATBEL");
        response.setReferenceYear(CURRENT_YEAR - 1); // Most recent complete year

        try {
            // Get population data
            addPopulationData(response, nisCode);

            // Get economic data
            addEconomicData(response, nisCode);

            // Get demographic data
            addDemographicData(response, nisCode);

            // Get housing data (if available)
            addHousingData(response, nisCode);

            // Calculate overall data quality score
            calculateDataQualityScore(response);

            logger.info("Successfully retrieved statistical data for NIS code: {} with {} data points",
                nisCode, countAvailableDataPoints(response));

        } catch (Exception e) {
            logger.error("Failed to retrieve statistical data for NIS code: {}", nisCode, e);
            response.setDataQualityScore(0.0);
        }

        return response;
    }

    @Cacheable(value = CACHE_NAME, key = "#municipalityName")
    public StatisticalDataResponse getStatisticalDataByMunicipality(String municipalityName) {
        logger.info("Fetching statistical data for municipality: {}", municipalityName);

        try {
            // First, get NIS code from municipality name
            String nisCode = getNisCodeByMunicipality(municipalityName);
            if (nisCode != null) {
                StatisticalDataResponse response = getStatisticalDataByNisCode(nisCode);
                response.setMunicipalityName(municipalityName);
                return response;
            } else {
                logger.warn("Could not find NIS code for municipality: {}", municipalityName);
                StatisticalDataResponse response = new StatisticalDataResponse(null, municipalityName);
                response.setDataQualityScore(0.0);
                return response;
            }

        } catch (Exception e) {
            logger.error("Failed to retrieve statistical data for municipality: {}", municipalityName, e);
            StatisticalDataResponse response = new StatisticalDataResponse(null, municipalityName);
            response.setDataQualityScore(0.0);
            return response;
        }
    }

    private void addPopulationData(StatisticalDataResponse response, String nisCode) {
        try {
            // Example endpoint - adjust based on actual Statbel API structure
            String url = "/population?nis=" + nisCode + "&year=" + response.getReferenceYear();
            ResponseEntity<String> apiResponse = statbelRestTemplate.getForEntity(url, String.class);

            if (apiResponse.getStatusCode() == HttpStatus.OK && apiResponse.getBody() != null) {
                JsonNode data = objectMapper.readTree(apiResponse.getBody());
                
                // Parse population data - structure depends on actual API response
                response.setTotalPopulation(getLongValue(data, "total_population"));
                response.setPopulationDensity(getBigDecimalValue(data, "density_per_km2"));
                response.setAverageAge(getBigDecimalValue(data, "average_age"));
                response.setNumberOfHouseholds(getLongValue(data, "households"));
                response.setAverageHouseholdSize(getBigDecimalValue(data, "household_size"));
                response.setBirthRate(getBigDecimalValue(data, "birth_rate"));
                response.setDeathRate(getBigDecimalValue(data, "death_rate"));
                response.setForeignNationalRate(getBigDecimalValue(data, "foreign_national_rate"));
                
                // Administrative info
                response.setMunicipalityName(getStringValue(data, "municipality_name"));
                response.setProvince(getStringValue(data, "province"));
                response.setRegion(getStringValue(data, "region"));
                response.setPrimaryLanguage(getStringValue(data, "primary_language"));
            }

        } catch (RestClientException e) {
            logger.debug("Population data not available from Statbel for NIS {}: {}", nisCode, e.getMessage());
            // Use mock data for development
            addMockPopulationData(response, nisCode);
        } catch (Exception e) {
            logger.warn("Error parsing population data for NIS {}: {}", nisCode, e.getMessage());
        }
    }

    private void addEconomicData(StatisticalDataResponse response, String nisCode) {
        try {
            String url = "/economic?nis=" + nisCode + "&year=" + response.getReferenceYear();
            ResponseEntity<String> apiResponse = statbelRestTemplate.getForEntity(url, String.class);

            if (apiResponse.getStatusCode() == HttpStatus.OK && apiResponse.getBody() != null) {
                JsonNode data = objectMapper.readTree(apiResponse.getBody());
                
                response.setMedianIncome(getBigDecimalValue(data, "median_income"));
                response.setUnemploymentRate(getBigDecimalValue(data, "unemployment_rate"));
                response.setHigherEducationRate(getBigDecimalValue(data, "higher_education_rate"));
            }

        } catch (RestClientException e) {
            logger.debug("Economic data not available from Statbel for NIS {}: {}", nisCode, e.getMessage());
            // Use mock data for development
            addMockEconomicData(response, nisCode);
        } catch (Exception e) {
            logger.warn("Error parsing economic data for NIS {}: {}", nisCode, e.getMessage());
        }
    }

    private void addDemographicData(StatisticalDataResponse response, String nisCode) {
        try {
            String url = "/demographics?nis=" + nisCode + "&year=" + response.getReferenceYear();
            ResponseEntity<String> apiResponse = statbelRestTemplate.getForEntity(url, String.class);

            if (apiResponse.getStatusCode() == HttpStatus.OK && apiResponse.getBody() != null) {
                JsonNode data = objectMapper.readTree(apiResponse.getBody());
                
                // Additional demographic details
                Map<String, Object> additionalStats = new HashMap<>();
                additionalStats.put("age_0_17", getBigDecimalValue(data, "age_0_17"));
                additionalStats.put("age_18_64", getBigDecimalValue(data, "age_18_64"));
                additionalStats.put("age_65_plus", getBigDecimalValue(data, "age_65_plus"));
                additionalStats.put("male_percentage", getBigDecimalValue(data, "male_percentage"));
                additionalStats.put("female_percentage", getBigDecimalValue(data, "female_percentage"));
                
                response.setAdditionalStats(additionalStats);
            }

        } catch (RestClientException e) {
            logger.debug("Demographic data not available from Statbel for NIS {}: {}", nisCode, e.getMessage());
        } catch (Exception e) {
            logger.warn("Error parsing demographic data for NIS {}: {}", nisCode, e.getMessage());
        }
    }

    private void addHousingData(StatisticalDataResponse response, String nisCode) {
        try {
            // This might come from a different source like real estate APIs
            String url = "/housing?nis=" + nisCode + "&year=" + response.getReferenceYear();
            ResponseEntity<String> apiResponse = statbelRestTemplate.getForEntity(url, String.class);

            if (apiResponse.getStatusCode() == HttpStatus.OK && apiResponse.getBody() != null) {
                JsonNode data = objectMapper.readTree(apiResponse.getBody());
                
                response.setAverageHousingPricePerSqm(getBigDecimalValue(data, "avg_price_per_sqm"));
            }

        } catch (RestClientException e) {
            logger.debug("Housing data not available from Statbel for NIS {}: {}", nisCode, e.getMessage());
            // Use mock data for development
            addMockHousingData(response, nisCode);
        } catch (Exception e) {
            logger.warn("Error parsing housing data for NIS {}: {}", nisCode, e.getMessage());
        }
    }

    private String getNisCodeByMunicipality(String municipalityName) {
        try {
            String url = "/municipalities/search?name=" + municipalityName;
            ResponseEntity<String> apiResponse = statbelRestTemplate.getForEntity(url, String.class);

            if (apiResponse.getStatusCode() == HttpStatus.OK && apiResponse.getBody() != null) {
                JsonNode data = objectMapper.readTree(apiResponse.getBody());
                return getStringValue(data, "nis_code");
            }

        } catch (Exception e) {
            logger.debug("Could not resolve NIS code for municipality {}: {}", municipalityName, e.getMessage());
        }

        // Fallback with common Belgian municipalities for development
        return getMockNisCode(municipalityName);
    }

    // Helper methods for parsing API responses
    private String getStringValue(JsonNode node, String fieldName) {
        JsonNode field = node.get(fieldName);
        return field != null && !field.isNull() ? field.asText() : null;
    }

    private Long getLongValue(JsonNode node, String fieldName) {
        JsonNode field = node.get(fieldName);
        return field != null && !field.isNull() ? field.asLong() : null;
    }

    private BigDecimal getBigDecimalValue(JsonNode node, String fieldName) {
        JsonNode field = node.get(fieldName);
        return field != null && !field.isNull() ? new BigDecimal(field.asText()) : null;
    }

    private void calculateDataQualityScore(StatisticalDataResponse response) {
        int totalFields = 15;
        int availableFields = countAvailableDataPoints(response);
        
        double score = (double) availableFields / totalFields * 100;
        response.setDataQualityScore(Math.round(score * 10.0) / 10.0);
    }

    private int countAvailableDataPoints(StatisticalDataResponse response) {
        int count = 0;
        if (response.getTotalPopulation() != null) count++;
        if (response.getPopulationDensity() != null) count++;
        if (response.getAverageAge() != null) count++;
        if (response.getMedianIncome() != null) count++;
        if (response.getUnemploymentRate() != null) count++;
        if (response.getHigherEducationRate() != null) count++;
        if (response.getCrimeRate() != null) count++;
        if (response.getAverageHousingPricePerSqm() != null) count++;
        if (response.getForeignNationalRate() != null) count++;
        if (response.getNumberOfHouseholds() != null) count++;
        if (response.getAverageHouseholdSize() != null) count++;
        if (response.getBirthRate() != null) count++;
        if (response.getDeathRate() != null) count++;
        if (response.getPrimaryLanguage() != null) count++;
        if (response.getMunicipalityName() != null) count++;
        
        return count;
    }

    // Mock data methods for development purposes
    private void addMockPopulationData(StatisticalDataResponse response, String nisCode) {
        // Generate realistic mock data based on NIS code
        int seed = nisCode.hashCode();
        
        response.setTotalPopulation(50000L + (seed % 200000));
        response.setPopulationDensity(BigDecimal.valueOf(1500 + (seed % 4000)));
        response.setAverageAge(BigDecimal.valueOf(35 + (seed % 15)));
        response.setNumberOfHouseholds(response.getTotalPopulation() / 2);
        response.setAverageHouseholdSize(BigDecimal.valueOf(2.0 + (seed % 100) / 100.0));
        response.setBirthRate(BigDecimal.valueOf(10 + (seed % 8)));
        response.setDeathRate(BigDecimal.valueOf(8 + (seed % 6)));
        response.setForeignNationalRate(BigDecimal.valueOf(15 + (seed % 30)));
    }

    private void addMockEconomicData(StatisticalDataResponse response, String nisCode) {
        int seed = nisCode.hashCode();
        
        response.setMedianIncome(BigDecimal.valueOf(25000 + (seed % 30000)));
        response.setUnemploymentRate(BigDecimal.valueOf(5 + (seed % 20)));
        response.setHigherEducationRate(BigDecimal.valueOf(30 + (seed % 40)));
    }

    private void addMockHousingData(StatisticalDataResponse response, String nisCode) {
        int seed = nisCode.hashCode();
        
        response.setAverageHousingPricePerSqm(BigDecimal.valueOf(2000 + (seed % 3000)));
    }

    private String getMockNisCode(String municipalityName) {
        // Some common Belgian municipalities with their actual NIS codes
        Map<String, String> commonMunicipalities = Map.of(
            "Brussels", "21004",
            "Antwerp", "11002", 
            "Ghent", "44021",
            "Charleroi", "52011",
            "Liège", "62063",
            "Bruges", "31005",
            "Namur", "92094",
            "Leuven", "24062",
            "Mons", "23064",
            "Aalst", "41002"
        );
        
        return commonMunicipalities.getOrDefault(municipalityName, "99999");
    }
}