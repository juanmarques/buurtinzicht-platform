package com.buurtinzicht.web.controller;

import com.buurtinzicht.integration.dto.AddressValidationRequest;
import com.buurtinzicht.integration.dto.AddressValidationResponse;
import com.buurtinzicht.integration.dto.StatisticalDataResponse;
import com.buurtinzicht.integration.service.BelgianAddressValidationService;
import com.buurtinzicht.integration.service.BelgianStatisticalDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/belgian-data")
@Tag(name = "Belgian Data", description = "Integration with Belgian government APIs for address validation and statistical data")
@SecurityRequirement(name = "Bearer Authentication")
public class BelgianDataController {

    private static final Logger logger = LoggerFactory.getLogger(BelgianDataController.class);

    private final BelgianAddressValidationService addressValidationService;
    private final BelgianStatisticalDataService statisticalDataService;

    @Autowired
    public BelgianDataController(
            BelgianAddressValidationService addressValidationService,
            BelgianStatisticalDataService statisticalDataService) {
        this.addressValidationService = addressValidationService;
        this.statisticalDataService = statisticalDataService;
    }

    @Operation(
        summary = "Validate Belgian address",
        description = "Validates and standardizes a Belgian address using official government APIs. " +
                     "Returns standardized address components and geocoding information if available."
    )
    @ApiResponse(responseCode = "200", description = "Address validation completed")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @PostMapping("/addresses/validate")
    @PreAuthorize("hasAnyRole('B2C_USER', 'B2B_USER', 'ADMIN')")
    public ResponseEntity<AddressValidationResponse> validateAddress(
            @Valid @RequestBody AddressValidationRequest request) {
        
        logger.info("Address validation requested for: {}", request.getFullAddressString());
        
        AddressValidationResponse response = addressValidationService.validateAddress(request);
        
        logger.info("Address validation completed for: {} - Valid: {}, Source: {}", 
            request.getFullAddressString(), response.isValid(), response.getSource());
        
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Geocode address string",
        description = "Geocodes a free-form Belgian address string and returns validation results. " +
                     "Useful for converting user input to standardized addresses."
    )
    @ApiResponse(responseCode = "200", description = "Address geocoding completed")
    @ApiResponse(responseCode = "400", description = "Invalid address string")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @GetMapping("/addresses/geocode")
    @PreAuthorize("hasAnyRole('B2C_USER', 'B2B_USER', 'ADMIN')")
    public ResponseEntity<AddressValidationResponse> geocodeAddress(
            @Parameter(description = "Address string to geocode", example = "Koning Albert II-laan 35, 1000 Brussels")
            @RequestParam String address) {
        
        logger.info("Address geocoding requested for: {}", address);
        
        AddressValidationResponse response = addressValidationService.geocodeAddress(address);
        
        logger.info("Address geocoding completed for: {} - Valid: {}", address, response.isValid());
        
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get statistical data by NIS code",
        description = "Retrieves comprehensive statistical data for a Belgian municipality using its NIS code. " +
                     "Includes population, demographics, economic indicators, and housing data."
    )
    @ApiResponse(responseCode = "200", description = "Statistical data retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "404", description = "Municipality not found")
    @GetMapping("/statistics/nis/{nisCode}")
    @PreAuthorize("hasAnyRole('B2C_USER', 'B2B_USER', 'ADMIN')")
    public ResponseEntity<StatisticalDataResponse> getStatisticalDataByNisCode(
            @Parameter(description = "Belgian NIS code", example = "21004")
            @PathVariable String nisCode) {
        
        logger.info("Statistical data requested for NIS code: {}", nisCode);
        
        StatisticalDataResponse response = statisticalDataService.getStatisticalDataByNisCode(nisCode);
        
        if (response.getDataQualityScore() != null && response.getDataQualityScore() > 0) {
            logger.info("Statistical data retrieved for NIS code: {} - Quality score: {}%", 
                nisCode, response.getDataQualityScore());
            return ResponseEntity.ok(response);
        } else {
            logger.warn("No statistical data found for NIS code: {}", nisCode);
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
        summary = "Get statistical data by municipality name",
        description = "Retrieves comprehensive statistical data for a Belgian municipality by name. " +
                     "The system will first resolve the municipality name to a NIS code."
    )
    @ApiResponse(responseCode = "200", description = "Statistical data retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "404", description = "Municipality not found")
    @GetMapping("/statistics/municipality/{municipalityName}")
    @PreAuthorize("hasAnyRole('B2C_USER', 'B2B_USER', 'ADMIN')")
    public ResponseEntity<StatisticalDataResponse> getStatisticalDataByMunicipality(
            @Parameter(description = "Municipality name", example = "Brussels")
            @PathVariable String municipalityName) {
        
        logger.info("Statistical data requested for municipality: {}", municipalityName);
        
        StatisticalDataResponse response = statisticalDataService.getStatisticalDataByMunicipality(municipalityName);
        
        if (response.getDataQualityScore() != null && response.getDataQualityScore() > 0) {
            logger.info("Statistical data retrieved for municipality: {} (NIS: {}) - Quality score: {}%", 
                municipalityName, response.getNisCode(), response.getDataQualityScore());
            return ResponseEntity.ok(response);
        } else {
            logger.warn("No statistical data found for municipality: {}", municipalityName);
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
        summary = "Get neighborhood insights",
        description = "Combines address validation and statistical data to provide comprehensive neighborhood insights. " +
                     "Useful for getting all available information about a location in a single request."
    )
    @ApiResponse(responseCode = "200", description = "Neighborhood insights retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @PostMapping("/neighborhoods/insights")
    @PreAuthorize("hasAnyRole('B2C_USER', 'B2B_USER', 'ADMIN')")
    public ResponseEntity<NeighborhoodInsightsResponse> getNeighborhoodInsights(
            @Valid @RequestBody AddressValidationRequest address) {
        
        logger.info("Neighborhood insights requested for: {}", address.getFullAddressString());
        
        // First validate the address
        AddressValidationResponse addressResponse = addressValidationService.validateAddress(address);
        
        NeighborhoodInsightsResponse insights = new NeighborhoodInsightsResponse();
        insights.setAddress(addressResponse);
        
        if (addressResponse.isValid() && addressResponse.getNisCode() != null) {
            // Get statistical data for the municipality
            StatisticalDataResponse statisticalData = 
                statisticalDataService.getStatisticalDataByNisCode(addressResponse.getNisCode());
            insights.setStatistics(statisticalData);
        }
        
        logger.info("Neighborhood insights completed for: {} - Address valid: {}, Statistics available: {}", 
            address.getFullAddressString(), 
            addressResponse.isValid(), 
            insights.getStatistics() != null);
        
        return ResponseEntity.ok(insights);
    }

    // Inner class for combined response
    public static class NeighborhoodInsightsResponse {
        private AddressValidationResponse address;
        private StatisticalDataResponse statistics;

        public AddressValidationResponse getAddress() { return address; }
        public void setAddress(AddressValidationResponse address) { this.address = address; }

        public StatisticalDataResponse getStatistics() { return statistics; }
        public void setStatistics(StatisticalDataResponse statistics) { this.statistics = statistics; }
    }
}