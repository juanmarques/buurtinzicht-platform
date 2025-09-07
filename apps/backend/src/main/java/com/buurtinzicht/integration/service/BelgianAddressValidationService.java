package com.buurtinzicht.integration.service;

import com.buurtinzicht.integration.dto.AddressValidationRequest;
import com.buurtinzicht.integration.dto.AddressValidationResponse;
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
import java.util.HashMap;
import java.util.Map;

@Service
public class BelgianAddressValidationService {

    private static final Logger logger = LoggerFactory.getLogger(BelgianAddressValidationService.class);

    private final RestTemplate geo6RestTemplate;
    private final RestTemplate bpostRestTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public BelgianAddressValidationService(
            @Qualifier("geo6RestTemplate") RestTemplate geo6RestTemplate,
            @Qualifier("bpostRestTemplate") RestTemplate bpostRestTemplate,
            ObjectMapper objectMapper) {
        this.geo6RestTemplate = geo6RestTemplate;
        this.bpostRestTemplate = bpostRestTemplate;
        this.objectMapper = objectMapper;
    }

    @Cacheable(value = "addressValidation", key = "#request.getFullAddressString()")
    public AddressValidationResponse validateAddress(AddressValidationRequest request) {
        logger.info("Validating address: {}", request.getFullAddressString());

        // Try GEO6 first (most comprehensive for Belgium)
        AddressValidationResponse response = validateWithGeo6(request);
        if (response.isValid()) {
            return response;
        }

        // Fallback to bpost validation
        logger.info("GEO6 validation failed, trying bpost for address: {}", request.getFullAddressString());
        response = validateWithBpost(request);
        if (response.isValid()) {
            return response;
        }

        // Return failed validation
        response.setErrorMessage("Address could not be validated with any available service");
        return response;
    }

    private AddressValidationResponse validateWithGeo6(AddressValidationRequest request) {
        AddressValidationResponse response = new AddressValidationResponse(false);
        response.setSource("GEO6");

        try {
            String query = buildGeo6Query(request);
            String url = "/search?q=" + query + "&format=json&limit=1";

            logger.debug("Calling GEO6 API: {}", url);
            ResponseEntity<String> apiResponse = geo6RestTemplate.getForEntity(url, String.class);

            if (apiResponse.getStatusCode() == HttpStatus.OK && apiResponse.getBody() != null) {
                return parseGeo6Response(apiResponse.getBody(), response);
            } else {
                response.setErrorMessage("GEO6 API returned non-200 status: " + apiResponse.getStatusCode());
            }

        } catch (RestClientException e) {
            logger.warn("GEO6 API call failed: {}", e.getMessage());
            response.setErrorMessage("GEO6 API call failed: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error validating with GEO6", e);
            response.setErrorMessage("Unexpected error: " + e.getMessage());
        }

        return response;
    }

    private AddressValidationResponse validateWithBpost(AddressValidationRequest request) {
        AddressValidationResponse response = new AddressValidationResponse(false);
        response.setSource("bpost");

        try {
            Map<String, Object> requestBody = buildBpostRequest(request);
            
            logger.debug("Calling bpost API with request: {}", requestBody);
            ResponseEntity<String> apiResponse = bpostRestTemplate.postForEntity(
                "/validation/address", requestBody, String.class);

            if (apiResponse.getStatusCode() == HttpStatus.OK && apiResponse.getBody() != null) {
                return parseBpostResponse(apiResponse.getBody(), response);
            } else {
                response.setErrorMessage("bpost API returned non-200 status: " + apiResponse.getStatusCode());
            }

        } catch (RestClientException e) {
            logger.warn("bpost API call failed: {}", e.getMessage());
            response.setErrorMessage("bpost API call failed: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error validating with bpost", e);
            response.setErrorMessage("Unexpected error: " + e.getMessage());
        }

        return response;
    }

    private String buildGeo6Query(AddressValidationRequest request) {
        StringBuilder query = new StringBuilder();
        
        if (request.getStreet() != null) {
            query.append(request.getStreet());
        }
        
        if (request.getHouseNumber() != null) {
            if (query.length() > 0) query.append(" ");
            query.append(request.getHouseNumber());
        }
        
        if (request.getBoxNumber() != null) {
            query.append("/").append(request.getBoxNumber());
        }
        
        if (request.getPostalCode() != null) {
            if (query.length() > 0) query.append(" ");
            query.append(request.getPostalCode());
        }
        
        if (request.getCity() != null) {
            if (query.length() > 0) query.append(" ");
            query.append(request.getCity());
        }

        return query.toString().trim();
    }

    private Map<String, Object> buildBpostRequest(AddressValidationRequest request) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("country", "BE");
        
        if (request.getStreet() != null) {
            requestBody.put("streetName", request.getStreet());
        }
        
        if (request.getHouseNumber() != null) {
            requestBody.put("streetNumber", request.getHouseNumber());
        }
        
        if (request.getBoxNumber() != null) {
            requestBody.put("box", request.getBoxNumber());
        }
        
        if (request.getPostalCode() != null) {
            requestBody.put("postalCode", request.getPostalCode());
        }
        
        if (request.getCity() != null) {
            requestBody.put("locality", request.getCity());
        }

        return requestBody;
    }

    private AddressValidationResponse parseGeo6Response(String responseBody, AddressValidationResponse response) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode features = root.get("features");
            
            if (features != null && features.isArray() && features.size() > 0) {
                JsonNode firstResult = features.get(0);
                JsonNode properties = firstResult.get("properties");
                JsonNode geometry = firstResult.get("geometry");
                
                response.setValid(true);
                response.setConfidenceScore(95.0); // GEO6 typically has high confidence
                
                if (properties != null) {
                    response.setStandardizedStreet(getStringValue(properties, "street"));
                    response.setStandardizedHouseNumber(getStringValue(properties, "number"));
                    response.setStandardizedBoxNumber(getStringValue(properties, "box"));
                    response.setStandardizedPostalCode(getStringValue(properties, "postcode"));
                    response.setStandardizedCity(getStringValue(properties, "locality"));
                    response.setMunicipality(getStringValue(properties, "municipality"));
                    response.setProvince(getStringValue(properties, "province"));
                    response.setRegion(getStringValue(properties, "region"));
                    response.setNisCode(getStringValue(properties, "nis"));
                    response.setLanguage(getStringValue(properties, "language"));
                }
                
                if (geometry != null && geometry.get("coordinates") != null) {
                    JsonNode coordinates = geometry.get("coordinates");
                    if (coordinates.isArray() && coordinates.size() >= 2) {
                        response.setLongitude(coordinates.get(0).decimalValue());
                        response.setLatitude(coordinates.get(1).decimalValue());
                    }
                }
            }
            
        } catch (Exception e) {
            logger.error("Error parsing GEO6 response", e);
            response.setValid(false);
            response.setErrorMessage("Failed to parse GEO6 response: " + e.getMessage());
        }
        
        return response;
    }

    private AddressValidationResponse parseBpostResponse(String responseBody, AddressValidationResponse response) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            
            // bpost response structure may vary - adapt based on actual API documentation
            JsonNode validationResult = root.get("validation");
            if (validationResult != null && validationResult.get("valid").asBoolean()) {
                response.setValid(true);
                response.setConfidenceScore(80.0); // Lower confidence than GEO6
                
                JsonNode address = root.get("standardizedAddress");
                if (address != null) {
                    response.setStandardizedStreet(getStringValue(address, "streetName"));
                    response.setStandardizedHouseNumber(getStringValue(address, "streetNumber"));
                    response.setStandardizedBoxNumber(getStringValue(address, "box"));
                    response.setStandardizedPostalCode(getStringValue(address, "postalCode"));
                    response.setStandardizedCity(getStringValue(address, "locality"));
                    response.setMunicipality(getStringValue(address, "municipality"));
                }
            }
            
        } catch (Exception e) {
            logger.error("Error parsing bpost response", e);
            response.setValid(false);
            response.setErrorMessage("Failed to parse bpost response: " + e.getMessage());
        }
        
        return response;
    }

    private String getStringValue(JsonNode node, String fieldName) {
        JsonNode field = node.get(fieldName);
        return field != null && !field.isNull() ? field.asText() : null;
    }

    public AddressValidationResponse geocodeAddress(String addressString) {
        // Create a simple request from address string
        AddressValidationRequest request = parseAddressString(addressString);
        return validateAddress(request);
    }

    private AddressValidationRequest parseAddressString(String addressString) {
        // Simple parser - in production, you'd want more sophisticated parsing
        AddressValidationRequest request = new AddressValidationRequest();
        
        String[] parts = addressString.split(",");
        if (parts.length >= 2) {
            String streetPart = parts[0].trim();
            String cityPart = parts[1].trim();
            
            // Extract street and house number
            String[] streetTokens = streetPart.split("\\s+");
            if (streetTokens.length >= 2) {
                String lastToken = streetTokens[streetTokens.length - 1];
                if (lastToken.matches("\\d+.*")) {
                    // Last token looks like a house number
                    request.setHouseNumber(lastToken);
                    request.setStreet(streetPart.substring(0, streetPart.lastIndexOf(lastToken)).trim());
                } else {
                    request.setStreet(streetPart);
                }
            } else {
                request.setStreet(streetPart);
            }
            
            // Extract postal code and city
            String[] cityTokens = cityPart.trim().split("\\s+");
            if (cityTokens.length >= 2 && cityTokens[0].matches("\\d{4}")) {
                request.setPostalCode(cityTokens[0]);
                request.setCity(cityPart.substring(5).trim()); // Skip postal code
            } else {
                request.setCity(cityPart);
            }
        } else {
            // Simple case - just use the whole string as street
            request.setStreet(addressString);
        }
        
        return request;
    }
}