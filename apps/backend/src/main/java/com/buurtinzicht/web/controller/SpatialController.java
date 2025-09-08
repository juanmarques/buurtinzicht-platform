package com.buurtinzicht.web.controller;

import com.buurtinzicht.domain.model.Neighborhood;
import com.buurtinzicht.spatial.dto.SpatialQueryRequest;
import com.buurtinzicht.spatial.dto.SpatialQueryResponse;
import com.buurtinzicht.spatial.service.SpatialAnalysisService;
import com.buurtinzicht.spatial.service.SpatialQueryService;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/spatial")
@Tag(name = "Spatial", description = "GIS and spatial data processing operations")
@SecurityRequirement(name = "Bearer Authentication")
public class SpatialController {

    private static final Logger logger = LoggerFactory.getLogger(SpatialController.class);

    private final SpatialQueryService spatialQueryService;
    private final SpatialAnalysisService spatialAnalysisService;

    @Autowired
    public SpatialController(
            SpatialQueryService spatialQueryService,
            SpatialAnalysisService spatialAnalysisService) {
        this.spatialQueryService = spatialQueryService;
        this.spatialAnalysisService = spatialAnalysisService;
    }

    @Operation(
        summary = "Find neighborhoods by location",
        description = "Find neighborhoods within a specified radius of a geographic point. " +
                     "Supports filtering by population, language, and urbanization level."
    )
    @ApiResponse(responseCode = "200", description = "Neighborhoods found successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @PostMapping("/neighborhoods/search")
    @PreAuthorize("hasAnyRole('B2C_USER', 'B2B_USER', 'ADMIN')")
    public ResponseEntity<SpatialQueryResponse> findNeighborhoods(
            @Valid @RequestBody SpatialQueryRequest request) {
        
        logger.info("Spatial search requested: lat={}, lon={}, radius={}km", 
            request.getLatitude(), request.getLongitude(), request.getRadiusKm());
        
        SpatialQueryResponse response = spatialQueryService.findNeighborhoods(request);
        
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Find neighborhood by point",
        description = "Find the neighborhood that contains a specific geographic point."
    )
    @ApiResponse(responseCode = "200", description = "Neighborhood found")
    @ApiResponse(responseCode = "404", description = "No neighborhood found at this location")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @GetMapping("/neighborhoods/at-point")
    @PreAuthorize("hasAnyRole('B2C_USER', 'B2B_USER', 'ADMIN')")
    public ResponseEntity<SpatialQueryResponse.NeighborhoodResult> findNeighborhoodByPoint(
            @Parameter(description = "Latitude", example = "50.8505")
            @RequestParam BigDecimal latitude,
            @Parameter(description = "Longitude", example = "4.3488") 
            @RequestParam BigDecimal longitude,
            @Parameter(description = "Language for localized names", example = "nl")
            @RequestParam(defaultValue = "nl") String language) {
        
        logger.info("Finding neighborhood at point: {}, {}", latitude, longitude);
        
        Optional<Neighborhood> neighborhood = spatialQueryService.findNeighborhoodByPoint(latitude, longitude);
        
        if (neighborhood.isPresent()) {
            SpatialQueryResponse.NeighborhoodResult result = 
                spatialQueryService.getNeighborhoodDetails(neighborhood.get().getNisCode(), language);
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
        summary = "Get neighborhood details",
        description = "Get detailed information about a specific neighborhood by NIS code."
    )
    @ApiResponse(responseCode = "200", description = "Neighborhood details retrieved")
    @ApiResponse(responseCode = "404", description = "Neighborhood not found")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @GetMapping("/neighborhoods/{nisCode}")
    @PreAuthorize("hasAnyRole('B2C_USER', 'B2B_USER', 'ADMIN')")
    public ResponseEntity<SpatialQueryResponse.NeighborhoodResult> getNeighborhoodDetails(
            @Parameter(description = "Belgian NIS code", example = "21004")
            @PathVariable String nisCode,
            @Parameter(description = "Language for localized names", example = "nl")
            @RequestParam(defaultValue = "nl") String language) {
        
        logger.info("Getting neighborhood details for NIS code: {}", nisCode);
        
        SpatialQueryResponse.NeighborhoodResult result = 
            spatialQueryService.getNeighborhoodDetails(nisCode, language);
        
        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
        summary = "Get neighborhood boundary",
        description = "Get the geographic boundary of a neighborhood as GeoJSON."
    )
    @ApiResponse(responseCode = "200", description = "Boundary retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Neighborhood or boundary not found")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @GetMapping("/neighborhoods/{neighborhoodId}/boundary")
    @PreAuthorize("hasAnyRole('B2C_USER', 'B2B_USER', 'ADMIN')")
    public ResponseEntity<String> getNeighborhoodBoundary(
            @Parameter(description = "Neighborhood UUID", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String neighborhoodId) {
        
        logger.info("Getting boundary for neighborhood: {}", neighborhoodId);
        
        Optional<String> boundary = spatialQueryService.getNeighborhoodBoundaryAsGeoJSON(neighborhoodId);
        
        if (boundary.isPresent()) {
            return ResponseEntity.ok()
                .header("Content-Type", "application/geo+json")
                .body(boundary.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
        summary = "Find neighboring areas",
        description = "Find neighborhoods that are geographically adjacent to a given neighborhood."
    )
    @ApiResponse(responseCode = "200", description = "Neighboring areas found")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @GetMapping("/neighborhoods/{nisCode}/neighbors")
    @PreAuthorize("hasAnyRole('B2C_USER', 'B2B_USER', 'ADMIN')")
    public ResponseEntity<List<SpatialQueryResponse.NeighborhoodResult>> findNeighboringAreas(
            @Parameter(description = "Belgian NIS code", example = "21004")
            @PathVariable String nisCode,
            @Parameter(description = "Language for localized names", example = "nl")
            @RequestParam(defaultValue = "nl") String language) {
        
        logger.info("Finding neighboring areas for NIS code: {}", nisCode);
        
        List<Neighborhood> neighbors = spatialQueryService.findNeighboringAreas(nisCode);
        
        List<SpatialQueryResponse.NeighborhoodResult> results = neighbors.stream()
            .map(n -> spatialQueryService.getNeighborhoodDetails(n.getNisCode(), language))
            .toList();
        
        return ResponseEntity.ok(results);
    }

    @Operation(
        summary = "Search neighborhoods by name",
        description = "Search for neighborhoods by name using full-text search."
    )
    @ApiResponse(responseCode = "200", description = "Search completed")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @GetMapping("/neighborhoods/search")
    @PreAuthorize("hasAnyRole('B2C_USER', 'B2B_USER', 'ADMIN')")
    public ResponseEntity<List<SpatialQueryResponse.NeighborhoodResult>> searchNeighborhoodsByName(
            @Parameter(description = "Search term", example = "Brussels")
            @RequestParam String q,
            @Parameter(description = "Maximum number of results", example = "10")
            @RequestParam(defaultValue = "10") int limit,
            @Parameter(description = "Language for localized names", example = "nl")
            @RequestParam(defaultValue = "nl") String language) {
        
        logger.info("Searching neighborhoods by name: {}", q);
        
        List<Neighborhood> neighborhoods = spatialQueryService.searchByName(q, limit);
        
        List<SpatialQueryResponse.NeighborhoodResult> results = neighborhoods.stream()
            .map(n -> spatialQueryService.getNeighborhoodDetails(n.getNisCode(), language))
            .toList();
        
        return ResponseEntity.ok(results);
    }

    @Operation(
        summary = "Calculate distance between neighborhoods",
        description = "Calculate the distance between two neighborhoods using their centroids."
    )
    @ApiResponse(responseCode = "200", description = "Distance calculated successfully")
    @ApiResponse(responseCode = "404", description = "One or both neighborhoods not found")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @GetMapping("/neighborhoods/distance")
    @PreAuthorize("hasAnyRole('B2C_USER', 'B2B_USER', 'ADMIN')")
    public ResponseEntity<BigDecimal> calculateDistance(
            @Parameter(description = "From NIS code", example = "21004")
            @RequestParam String from,
            @Parameter(description = "To NIS code", example = "11002")
            @RequestParam String to) {
        
        logger.info("Calculating distance between {} and {}", from, to);
        
        BigDecimal distance = spatialQueryService.calculateDistance(from, to);
        
        if (distance != null) {
            return ResponseEntity.ok(distance);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
        summary = "Compare neighborhoods",
        description = "Compare two neighborhoods across various metrics including distance, area, and population."
    )
    @ApiResponse(responseCode = "200", description = "Comparison completed successfully")
    @ApiResponse(responseCode = "404", description = "One or both neighborhoods not found")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @GetMapping("/neighborhoods/compare")
    @PreAuthorize("hasAnyRole('B2B_USER', 'ADMIN')")
    public ResponseEntity<SpatialAnalysisService.NeighborhoodComparisonResult> compareNeighborhoods(
            @Parameter(description = "First neighborhood NIS code", example = "21004")
            @RequestParam String nis1,
            @Parameter(description = "Second neighborhood NIS code", example = "11002")
            @RequestParam String nis2) {
        
        logger.info("Comparing neighborhoods: {} vs {}", nis1, nis2);
        
        try {
            SpatialAnalysisService.NeighborhoodComparisonResult comparison = 
                spatialAnalysisService.compareNeighborhoods(nis1, nis2);
            return ResponseEntity.ok(comparison);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
        summary = "Get spatial statistics",
        description = "Get statistical analysis of spatial data for a region or all of Belgium."
    )
    @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('B2B_USER', 'ADMIN')")
    public ResponseEntity<SpatialAnalysisService.SpatialStatistics> getSpatialStatistics(
            @Parameter(description = "Region name (optional)", example = "Brussels-Capital Region")
            @RequestParam(required = false) String region) {
        
        logger.info("Getting spatial statistics for region: {}", region);
        
        SpatialAnalysisService.SpatialStatistics statistics = 
            spatialAnalysisService.calculateSpatialStatistics(region);
        
        return ResponseEntity.ok(statistics);
    }

    @Operation(
        summary = "Find neighborhood clusters",
        description = "Find clusters of similar neighborhoods based on geographic proximity and characteristics."
    )
    @ApiResponse(responseCode = "200", description = "Clusters found successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @GetMapping("/neighborhoods/clusters")
    @PreAuthorize("hasAnyRole('B2B_USER', 'ADMIN')")
    public ResponseEntity<List<SpatialAnalysisService.NeighborhoodCluster>> findNeighborhoodClusters(
            @Parameter(description = "Minimum cluster size", example = "3")
            @RequestParam(defaultValue = "3") int minSize,
            @Parameter(description = "Primary language filter", example = "nl")
            @RequestParam(required = false) String language) {
        
        logger.info("Finding neighborhood clusters with min size: {}, language: {}", minSize, language);
        
        List<SpatialAnalysisService.NeighborhoodCluster> clusters = 
            spatialAnalysisService.findNeighborhoodClusters(minSize, language);
        
        return ResponseEntity.ok(clusters);
    }

    @Operation(
        summary = "Get all municipalities",
        description = "Get a list of all municipalities in the system."
    )
    @ApiResponse(responseCode = "200", description = "Municipalities retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @GetMapping("/municipalities")
    @PreAuthorize("hasAnyRole('B2C_USER', 'B2B_USER', 'ADMIN')")
    public ResponseEntity<List<String>> getAllMunicipalities() {
        
        List<String> municipalities = spatialQueryService.getAllMunicipalities();
        return ResponseEntity.ok(municipalities);
    }

    @Operation(
        summary = "Get all provinces",
        description = "Get a list of all provinces in the system."
    )
    @ApiResponse(responseCode = "200", description = "Provinces retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @GetMapping("/provinces")
    @PreAuthorize("hasAnyRole('B2C_USER', 'B2B_USER', 'ADMIN')")
    public ResponseEntity<List<String>> getAllProvinces() {
        
        List<String> provinces = spatialQueryService.getAllProvinces();
        return ResponseEntity.ok(provinces);
    }

    @Operation(
        summary = "Get all regions",
        description = "Get a list of all regions in the system."
    )
    @ApiResponse(responseCode = "200", description = "Regions retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @GetMapping("/regions")
    @PreAuthorize("hasAnyRole('B2C_USER', 'B2B_USER', 'ADMIN')")
    public ResponseEntity<List<String>> getAllRegions() {
        
        List<String> regions = spatialQueryService.getAllRegions();
        return ResponseEntity.ok(regions);
    }
}