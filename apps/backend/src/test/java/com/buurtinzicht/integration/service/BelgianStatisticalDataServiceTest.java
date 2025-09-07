package com.buurtinzicht.integration.service;

import com.buurtinzicht.integration.dto.StatisticalDataResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class BelgianStatisticalDataServiceTest {

    @Mock
    private RestTemplate statbelRestTemplate;

    @Mock
    private RestTemplate defaultRestTemplate;

    private ObjectMapper objectMapper;
    private BelgianStatisticalDataService statisticalDataService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        statisticalDataService = new BelgianStatisticalDataService(
            statbelRestTemplate, defaultRestTemplate, objectMapper);
    }

    @Test
    void getStatisticalDataByNisCode_ShouldReturnData_WhenStatbelResponds() {
        // Given
        String nisCode = "21004"; // Brussels
        
        String mockPopulationResponse = """
            {
                "total_population": 179277,
                "density_per_km2": 5747.3,
                "average_age": 37.2,
                "households": 89156,
                "household_size": 1.98,
                "birth_rate": 12.5,
                "death_rate": 8.9,
                "foreign_national_rate": 35.2,
                "municipality_name": "City of Brussels",
                "province": "Brussels-Capital Region",
                "region": "Brussels-Capital Region",
                "primary_language": "fr"
            }
        """;

        String mockEconomicResponse = """
            {
                "median_income": 25500,
                "unemployment_rate": 15.2,
                "higher_education_rate": 45.8
            }
        """;

        when(statbelRestTemplate.getForEntity(
            eq("/population?nis=" + nisCode + "&year=2024"), eq(String.class)))
            .thenReturn(new ResponseEntity<>(mockPopulationResponse, HttpStatus.OK));

        when(statbelRestTemplate.getForEntity(
            eq("/economic?nis=" + nisCode + "&year=2024"), eq(String.class)))
            .thenReturn(new ResponseEntity<>(mockEconomicResponse, HttpStatus.OK));

        // When
        StatisticalDataResponse response = statisticalDataService.getStatisticalDataByNisCode(nisCode);

        // Then
        assertNotNull(response);
        assertEquals(nisCode, response.getNisCode());
        assertEquals("STATBEL", response.getDataSource());
        assertEquals(2024, response.getReferenceYear());

        // Population data
        assertEquals(179277L, response.getTotalPopulation());
        assertEquals(BigDecimal.valueOf(5747.3), response.getPopulationDensity());
        assertEquals(BigDecimal.valueOf(37.2), response.getAverageAge());
        assertEquals(89156L, response.getNumberOfHouseholds());
        assertEquals(BigDecimal.valueOf(1.98), response.getAverageHouseholdSize());
        assertEquals(BigDecimal.valueOf(12.5), response.getBirthRate());
        assertEquals(BigDecimal.valueOf(8.9), response.getDeathRate());
        assertEquals(BigDecimal.valueOf(35.2), response.getForeignNationalRate());

        // Administrative data
        assertEquals("City of Brussels", response.getMunicipalityName());
        assertEquals("Brussels-Capital Region", response.getProvince());
        assertEquals("Brussels-Capital Region", response.getRegion());
        assertEquals("fr", response.getPrimaryLanguage());

        // Economic data
        assertEquals(BigDecimal.valueOf(25500), response.getMedianIncome());
        assertEquals(BigDecimal.valueOf(15.2), response.getUnemploymentRate());
        assertEquals(BigDecimal.valueOf(45.8), response.getHigherEducationRate());

        // Data quality
        assertTrue(response.getDataQualityScore() > 70);
        assertTrue(response.hasPopulationData());
        assertTrue(response.hasEconomicData());
    }

    @Test
    void getStatisticalDataByNisCode_ShouldUseMockData_WhenStatbelFails() {
        // Given
        String nisCode = "21004";

        when(statbelRestTemplate.getForEntity(anyString(), eq(String.class)))
            .thenThrow(new RestClientException("Statbel API unavailable"));

        // When
        StatisticalDataResponse response = statisticalDataService.getStatisticalDataByNisCode(nisCode);

        // Then
        assertNotNull(response);
        assertEquals(nisCode, response.getNisCode());
        assertEquals("STATBEL", response.getDataSource());

        // Mock data should be present
        assertNotNull(response.getTotalPopulation());
        assertNotNull(response.getPopulationDensity());
        assertNotNull(response.getAverageAge());
        assertNotNull(response.getMedianIncome());
        assertNotNull(response.getUnemploymentRate());
        assertNotNull(response.getAverageHousingPricePerSqm());

        // Data quality should be calculated
        assertTrue(response.getDataQualityScore() > 0);
        assertTrue(response.hasPopulationData());
        assertTrue(response.hasEconomicData());
        assertTrue(response.hasHousingData());
    }

    @Test
    void getStatisticalDataByMunicipality_ShouldResolveNisCode_WhenMunicipalityProvided() {
        // Given
        String municipalityName = "Brussels";
        String expectedNisCode = "21004";

        String mockMunicipalityResponse = """
            {
                "nis_code": "21004"
            }
        """;

        when(statbelRestTemplate.getForEntity(
            eq("/municipalities/search?name=" + municipalityName), eq(String.class)))
            .thenReturn(new ResponseEntity<>(mockMunicipalityResponse, HttpStatus.OK));

        // Mock subsequent calls for statistical data
        when(statbelRestTemplate.getForEntity(anyString(), eq(String.class)))
            .thenThrow(new RestClientException("Use mock data"));

        // When
        StatisticalDataResponse response = statisticalDataService.getStatisticalDataByMunicipality(municipalityName);

        // Then
        assertNotNull(response);
        assertEquals(expectedNisCode, response.getNisCode());
        assertEquals(municipalityName, response.getMunicipalityName());
        assertTrue(response.getDataQualityScore() > 0);
    }

    @Test
    void getStatisticalDataByMunicipality_ShouldUseFallback_WhenResolutionFails() {
        // Given
        String municipalityName = "Brussels";

        when(statbelRestTemplate.getForEntity(anyString(), eq(String.class)))
            .thenThrow(new RestClientException("Municipality resolution failed"));

        // When
        StatisticalDataResponse response = statisticalDataService.getStatisticalDataByMunicipality(municipalityName);

        // Then
        assertNotNull(response);
        assertEquals("21004", response.getNisCode()); // Mock NIS code for Brussels
        assertEquals(municipalityName, response.getMunicipalityName());
        assertTrue(response.getDataQualityScore() > 0);
    }

    @Test
    void getStatisticalDataByNisCode_ShouldHandlePartialData() {
        // Given
        String nisCode = "11002"; // Antwerp
        
        String mockPopulationResponse = """
            {
                "total_population": 529247,
                "municipality_name": "Antwerp",
                "primary_language": "nl"
            }
        """;

        when(statbelRestTemplate.getForEntity(
            eq("/population?nis=" + nisCode + "&year=2024"), eq(String.class)))
            .thenReturn(new ResponseEntity<>(mockPopulationResponse, HttpStatus.OK));

        when(statbelRestTemplate.getForEntity(
            eq("/economic?nis=" + nisCode + "&year=2024"), eq(String.class)))
            .thenThrow(new RestClientException("Economic data not available"));

        // When
        StatisticalDataResponse response = statisticalDataService.getStatisticalDataByNisCode(nisCode);

        // Then
        assertNotNull(response);
        assertEquals(529247L, response.getTotalPopulation());
        assertEquals("Antwerp", response.getMunicipalityName());
        assertEquals("nl", response.getPrimaryLanguage());
        
        // Economic data should be mock data since API failed
        assertNotNull(response.getMedianIncome());
        assertNotNull(response.getUnemploymentRate());
        
        // Data quality should reflect partial data
        assertTrue(response.getDataQualityScore() > 30);
        assertTrue(response.hasPopulationData());
        assertTrue(response.hasEconomicData()); // From mock data
    }

    @Test
    void statisticalDataResponse_ShouldCalculateDataAvailability() {
        // Given
        StatisticalDataResponse response = new StatisticalDataResponse();
        
        // When - no data initially
        assertFalse(response.hasPopulationData());
        assertFalse(response.hasEconomicData());
        assertFalse(response.hasHousingData());
        assertFalse(response.hasDemographicData());

        // Add population data
        response.setTotalPopulation(100000L);
        assertTrue(response.hasPopulationData());

        // Add economic data
        response.setMedianIncome(BigDecimal.valueOf(30000));
        assertTrue(response.hasEconomicData());

        // Add housing data
        response.setAverageHousingPricePerSqm(BigDecimal.valueOf(2500));
        assertTrue(response.hasHousingData());

        // Add demographic data
        response.setAverageAge(BigDecimal.valueOf(40));
        assertTrue(response.hasDemographicData());
    }

    @Test
    void getStatisticalDataByNisCode_ShouldSetCorrectTimestamp() {
        // Given
        String nisCode = "44021"; // Ghent

        when(statbelRestTemplate.getForEntity(anyString(), eq(String.class)))
            .thenThrow(new RestClientException("Use mock data"));

        // When
        StatisticalDataResponse response = statisticalDataService.getStatisticalDataByNisCode(nisCode);

        // Then
        assertNotNull(response.getRetrievedAt());
        assertTrue(response.getRetrievedAt().isBefore(java.time.Instant.now().plusSeconds(1)));
    }

    @Test
    void getStatisticalDataByMunicipality_ShouldHandleUnknownMunicipality() {
        // Given
        String unknownMunicipality = "UnknownTown";

        when(statbelRestTemplate.getForEntity(anyString(), eq(String.class)))
            .thenThrow(new RestClientException("Municipality not found"));

        // When
        StatisticalDataResponse response = statisticalDataService.getStatisticalDataByMunicipality(unknownMunicipality);

        // Then
        assertNotNull(response);
        assertEquals(unknownMunicipality, response.getMunicipalityName());
        assertEquals("99999", response.getNisCode()); // Default mock NIS code
        assertTrue(response.getDataQualityScore() > 0); // Should still have mock data
    }
}