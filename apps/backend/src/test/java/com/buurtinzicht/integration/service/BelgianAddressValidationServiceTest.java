package com.buurtinzicht.integration.service;

import com.buurtinzicht.integration.dto.AddressValidationRequest;
import com.buurtinzicht.integration.dto.AddressValidationResponse;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class BelgianAddressValidationServiceTest {

    @Mock
    private RestTemplate geo6RestTemplate;

    @Mock
    private RestTemplate bpostRestTemplate;

    private ObjectMapper objectMapper;
    private BelgianAddressValidationService addressValidationService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        addressValidationService = new BelgianAddressValidationService(
            geo6RestTemplate, bpostRestTemplate, objectMapper);
    }

    @Test
    void validateAddress_ShouldReturnValidResponse_WhenGeo6Succeeds() {
        // Given
        AddressValidationRequest request = new AddressValidationRequest(
            "Koning Albert II-laan", "35", "1000", "Brussels");

        String mockGeo6Response = """
            {
                "features": [{
                    "properties": {
                        "street": "Koning Albert II-laan",
                        "number": "35",
                        "postcode": "1000",
                        "locality": "Brussels",
                        "municipality": "City of Brussels",
                        "province": "Brussels-Capital Region",
                        "region": "Brussels-Capital Region",
                        "nis": "21004",
                        "language": "fr"
                    },
                    "geometry": {
                        "coordinates": [4.3488, 50.8505]
                    }
                }]
            }
        """;

        when(geo6RestTemplate.getForEntity(anyString(), eq(String.class)))
            .thenReturn(new ResponseEntity<>(mockGeo6Response, HttpStatus.OK));

        // When
        AddressValidationResponse response = addressValidationService.validateAddress(request);

        // Then
        assertTrue(response.isValid());
        assertEquals("GEO6", response.getSource());
        assertEquals("Koning Albert II-laan", response.getStandardizedStreet());
        assertEquals("35", response.getStandardizedHouseNumber());
        assertEquals("1000", response.getStandardizedPostalCode());
        assertEquals("Brussels", response.getStandardizedCity());
        assertEquals("City of Brussels", response.getMunicipality());
        assertEquals("21004", response.getNisCode());
        assertEquals("fr", response.getLanguage());
        assertNotNull(response.getLatitude());
        assertNotNull(response.getLongitude());
        assertTrue(response.getConfidenceScore() > 90);
    }

    @Test
    void validateAddress_ShouldFallbackToBpost_WhenGeo6Fails() {
        // Given
        AddressValidationRequest request = new AddressValidationRequest(
            "Test Street", "123", "1000", "Brussels");

        String mockBpostResponse = """
            {
                "validation": {
                    "valid": true
                },
                "standardizedAddress": {
                    "streetName": "Test Street",
                    "streetNumber": "123",
                    "postalCode": "1000",
                    "locality": "Brussels",
                    "municipality": "City of Brussels"
                }
            }
        """;

        when(geo6RestTemplate.getForEntity(anyString(), eq(String.class)))
            .thenThrow(new RestClientException("GEO6 API unavailable"));

        when(bpostRestTemplate.postForEntity(anyString(), any(), eq(String.class)))
            .thenReturn(new ResponseEntity<>(mockBpostResponse, HttpStatus.OK));

        // When
        AddressValidationResponse response = addressValidationService.validateAddress(request);

        // Then
        assertTrue(response.isValid());
        assertEquals("bpost", response.getSource());
        assertEquals("Test Street", response.getStandardizedStreet());
        assertEquals("123", response.getStandardizedHouseNumber());
        assertEquals("1000", response.getStandardizedPostalCode());
        assertEquals("Brussels", response.getStandardizedCity());
        assertTrue(response.getConfidenceScore() > 70);
    }

    @Test
    void validateAddress_ShouldReturnInvalid_WhenBothServicesFail() {
        // Given
        AddressValidationRequest request = new AddressValidationRequest(
            "Invalid Street", "999", "9999", "Invalid City");

        when(geo6RestTemplate.getForEntity(anyString(), eq(String.class)))
            .thenThrow(new RestClientException("GEO6 API unavailable"));

        when(bpostRestTemplate.postForEntity(anyString(), any(), eq(String.class)))
            .thenThrow(new RestClientException("bpost API unavailable"));

        // When
        AddressValidationResponse response = addressValidationService.validateAddress(request);

        // Then
        assertFalse(response.isValid());
        assertNotNull(response.getErrorMessage());
        assertTrue(response.getErrorMessage().contains("could not be validated"));
    }

    @Test
    void validateAddress_ShouldHandleEmptyGeo6Response() {
        // Given
        AddressValidationRequest request = new AddressValidationRequest(
            "Nonexistent Street", "999", "1000", "Brussels");

        String emptyGeo6Response = """
            {
                "features": []
            }
        """;

        String mockBpostResponse = """
            {
                "validation": {
                    "valid": false
                }
            }
        """;

        when(geo6RestTemplate.getForEntity(anyString(), eq(String.class)))
            .thenReturn(new ResponseEntity<>(emptyGeo6Response, HttpStatus.OK));

        when(bpostRestTemplate.postForEntity(anyString(), any(), eq(String.class)))
            .thenReturn(new ResponseEntity<>(mockBpostResponse, HttpStatus.OK));

        // When
        AddressValidationResponse response = addressValidationService.validateAddress(request);

        // Then
        assertFalse(response.isValid());
    }

    @Test
    void geocodeAddress_ShouldParseSimpleAddressString() {
        // Given
        String addressString = "Koning Albert II-laan 35, 1000 Brussels";

        String mockGeo6Response = """
            {
                "features": [{
                    "properties": {
                        "street": "Koning Albert II-laan",
                        "number": "35",
                        "postcode": "1000",
                        "locality": "Brussels",
                        "municipality": "City of Brussels",
                        "nis": "21004"
                    },
                    "geometry": {
                        "coordinates": [4.3488, 50.8505]
                    }
                }]
            }
        """;

        when(geo6RestTemplate.getForEntity(anyString(), eq(String.class)))
            .thenReturn(new ResponseEntity<>(mockGeo6Response, HttpStatus.OK));

        // When
        AddressValidationResponse response = addressValidationService.geocodeAddress(addressString);

        // Then
        assertTrue(response.isValid());
        assertEquals("Koning Albert II-laan", response.getStandardizedStreet());
        assertEquals("35", response.getStandardizedHouseNumber());
        assertEquals("1000", response.getStandardizedPostalCode());
        assertEquals("Brussels", response.getStandardizedCity());
        assertTrue(response.hasGeoLocation());
    }

    @Test
    void getFullAddressString_ShouldFormatCorrectly() {
        // Given
        AddressValidationRequest request = new AddressValidationRequest(
            "Koning Albert II-laan", "35", "1000", "Brussels");
        request.setBoxNumber("1");

        // When
        String fullAddress = request.getFullAddressString();

        // Then
        assertEquals("Koning Albert II-laan 35/1, 1000 Brussels", fullAddress);
    }

    @Test
    void validateAddress_ShouldValidateRequestFields() {
        // Given
        AddressValidationRequest request = new AddressValidationRequest();
        request.setPostalCode("1000");
        request.setCity("Brussels");

        String mockGeo6Response = """
            {
                "features": [{
                    "properties": {
                        "postcode": "1000",
                        "locality": "Brussels",
                        "municipality": "City of Brussels",
                        "nis": "21004"
                    }
                }]
            }
        """;

        when(geo6RestTemplate.getForEntity(anyString(), eq(String.class)))
            .thenReturn(new ResponseEntity<>(mockGeo6Response, HttpStatus.OK));

        // When
        AddressValidationResponse response = addressValidationService.validateAddress(request);

        // Then
        assertTrue(response.isValid());
        assertEquals("1000", response.getStandardizedPostalCode());
        assertEquals("Brussels", response.getStandardizedCity());
    }
}