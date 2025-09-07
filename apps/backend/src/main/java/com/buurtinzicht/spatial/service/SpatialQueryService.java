package com.buurtinzicht.spatial.service;

import com.buurtinzicht.domain.model.Neighborhood;
import com.buurtinzicht.domain.repository.NeighborhoodRepository;
import com.buurtinzicht.spatial.dto.SpatialQueryRequest;
import com.buurtinzicht.spatial.dto.SpatialQueryResponse;
import org.locationtech.jts.geom.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class SpatialQueryService {

    private static final Logger logger = LoggerFactory.getLogger(SpatialQueryService.class);

    private final NeighborhoodRepository neighborhoodRepository;

    @Autowired
    public SpatialQueryService(NeighborhoodRepository neighborhoodRepository) {
        this.neighborhoodRepository = neighborhoodRepository;
    }

    @Cacheable(value = "spatialQueries", key = "#request.latitude + '_' + #request.longitude + '_' + #request.radiusKm")
    public SpatialQueryResponse findNeighborhoods(SpatialQueryRequest request) {
        logger.info("Executing spatial query for coordinates: {}, {} with radius: {} km", 
            request.getLatitude(), request.getLongitude(), request.getRadiusKm());

        long startTime = System.currentTimeMillis();

        SpatialQueryResponse response = new SpatialQueryResponse();
        response.setSearchCenter(new SpatialQueryResponse.SearchPoint(request.getLatitude(), request.getLongitude()));
        response.setSearchRadiusKm(request.getRadiusKm());

        try {
            List<Object[]> results = neighborhoodRepository.findNeighborhoodsWithFilters(
                request.getLatitude().doubleValue(),
                request.getLongitude().doubleValue(),
                request.getRadiusKm().doubleValue() * 1000, // Convert to meters
                request.getUrbanizationLevel(),
                request.getPrimaryLanguage(),
                request.getMinPopulation(),
                request.getMaxPopulation(),
                request.getLimit()
            );

            List<SpatialQueryResponse.NeighborhoodResult> neighborhoods = new ArrayList<>();
            
            for (Object[] row : results) {
                Neighborhood neighborhood = (Neighborhood) row[0];
                BigDecimal distanceKm = BigDecimal.valueOf((Double) row[1]).setScale(2, RoundingMode.HALF_UP);

                SpatialQueryResponse.NeighborhoodResult result = convertToNeighborhoodResult(
                    neighborhood, distanceKm, request);
                neighborhoods.add(result);
            }

            response.setNeighborhoods(neighborhoods);
            response.setTotalResults(neighborhoods.size());
            response.setReturnedResults(neighborhoods.size());

            long executionTime = System.currentTimeMillis() - startTime;
            response.setExecutionTimeMs(executionTime);

            logger.info("Spatial query completed: found {} neighborhoods in {} ms", 
                neighborhoods.size(), executionTime);

        } catch (Exception e) {
            logger.error("Error executing spatial query", e);
            throw new RuntimeException("Spatial query failed: " + e.getMessage(), e);
        }

        return response;
    }

    public Optional<Neighborhood> findNeighborhoodByPoint(BigDecimal latitude, BigDecimal longitude) {
        logger.debug("Finding neighborhood containing point: {}, {}", latitude, longitude);
        
        return neighborhoodRepository.findNeighborhoodContainingPoint(
            latitude.doubleValue(), longitude.doubleValue());
    }

    public List<Neighborhood> findNeighboringAreas(String nisCode) {
        logger.debug("Finding neighboring areas for NIS code: {}", nisCode);
        
        return neighborhoodRepository.findNeighboringAreas(nisCode);
    }

    public List<Neighborhood> searchByName(String searchTerm, int limit) {
        logger.debug("Searching neighborhoods by name: {}", searchTerm);
        
        // Prepare search query for full-text search
        String searchQuery = searchTerm.trim().replaceAll("\\s+", " & ");
        
        List<Object[]> results = neighborhoodRepository.searchNeighborhoodsByName(
            searchQuery, searchTerm, limit);

        List<Neighborhood> neighborhoods = new ArrayList<>();
        for (Object[] row : results) {
            neighborhoods.add((Neighborhood) row[0]);
        }

        return neighborhoods;
    }

    @Cacheable(value = "neighborhoodBoundaries", key = "#neighborhoodId")
    public Optional<String> getNeighborhoodBoundaryAsGeoJSON(String neighborhoodId) {
        logger.debug("Retrieving boundary GeoJSON for neighborhood: {}", neighborhoodId);
        
        try {
            return neighborhoodRepository.findBoundaryAsGeoJSON(
                java.util.UUID.fromString(neighborhoodId));
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid neighborhood ID format: {}", neighborhoodId);
            return Optional.empty();
        }
    }

    public BigDecimal calculateDistance(String fromNisCode, String toNisCode) {
        logger.debug("Calculating distance between {} and {}", fromNisCode, toNisCode);
        
        Optional<Object[]> result = neighborhoodRepository.calculateDistanceBetweenNeighborhoods(
            fromNisCode, toNisCode);
        
        if (result.isPresent()) {
            Double distance = (Double) result.get()[2];
            return BigDecimal.valueOf(distance).setScale(2, RoundingMode.HALF_UP);
        }
        
        return null;
    }

    public SpatialQueryResponse.NeighborhoodResult getNeighborhoodDetails(String nisCode, String language) {
        logger.debug("Getting neighborhood details for NIS code: {}", nisCode);
        
        Optional<Neighborhood> neighborhoodOpt = neighborhoodRepository.findByNisCode(nisCode);
        
        if (neighborhoodOpt.isEmpty()) {
            return null;
        }

        Neighborhood neighborhood = neighborhoodOpt.get();
        return convertToNeighborhoodResult(neighborhood, null, language, false);
    }

    public List<String> getAllMunicipalities() {
        return neighborhoodRepository.findAllActiveMunicipalities();
    }

    public List<String> getAllProvinces() {
        return neighborhoodRepository.findAllActiveProvinces();
    }

    public List<String> getAllRegions() {
        return neighborhoodRepository.findAllActiveRegions();
    }

    public long getStatistics() {
        return neighborhoodRepository.countActiveNeighborhoods();
    }

    public List<Neighborhood> getNeighborhoodsByPopulationRange(Long minPopulation, Long maxPopulation) {
        if (minPopulation != null && maxPopulation != null) {
            return neighborhoodRepository.findActiveNeighborhoodsByPopulationRange(minPopulation, maxPopulation);
        } else if (minPopulation != null) {
            return neighborhoodRepository.findActiveNeighborhoodsWithMinPopulation(minPopulation);
        } else {
            return neighborhoodRepository.findByIsActiveTrue();
        }
    }

    private SpatialQueryResponse.NeighborhoodResult convertToNeighborhoodResult(
            Neighborhood neighborhood, BigDecimal distanceKm, SpatialQueryRequest request) {
        
        return convertToNeighborhoodResult(
            neighborhood, 
            distanceKm, 
            request.getPrimaryLanguage() != null ? request.getPrimaryLanguage() : "nl",
            request.getIncludeBoundaries() != null ? request.getIncludeBoundaries() : false
        );
    }

    private SpatialQueryResponse.NeighborhoodResult convertToNeighborhoodResult(
            Neighborhood neighborhood, BigDecimal distanceKm, String language, boolean includeBoundaries) {
        
        SpatialQueryResponse.NeighborhoodResult result = new SpatialQueryResponse.NeighborhoodResult();
        
        result.setId(neighborhood.getId().toString());
        result.setNisCode(neighborhood.getNisCode());
        result.setName(neighborhood.getName());
        result.setLocalizedName(neighborhood.getLocalizedName(language));
        result.setMunicipality(neighborhood.getMunicipality());
        result.setProvince(neighborhood.getProvince());
        result.setRegion(neighborhood.getRegion());
        result.setDistanceKm(distanceKm);
        result.setAreaKm2(neighborhood.getAreaKm2());
        result.setPopulation(neighborhood.getPopulation());
        result.setPopulationDensity(neighborhood.getPopulationDensity());
        result.setPrimaryLanguage(neighborhood.getPrimaryLanguage());
        result.setUrbanizationLevel(neighborhood.getUrbanizationLevel() != null ? 
            neighborhood.getUrbanizationLevel().toString() : null);

        // Set centroid
        if (neighborhood.getCentroid() != null) {
            Point centroid = neighborhood.getCentroid();
            result.setCentroid(new SpatialQueryResponse.Point(
                BigDecimal.valueOf(centroid.getY()),
                BigDecimal.valueOf(centroid.getX())
            ));
        }

        // Set postal codes
        if (neighborhood.getPostalCodes() != null) {
            result.setPostalCodes(Arrays.asList(neighborhood.getPostalCodeArray()));
        }

        // Set elevation data
        if (neighborhood.hasElevationData()) {
            result.setElevation(new SpatialQueryResponse.ElevationData(
                neighborhood.getElevationMin(),
                neighborhood.getElevationMax(),
                neighborhood.getElevationAvg()
            ));
        }

        // Include boundary if requested
        if (includeBoundaries) {
            Optional<String> boundaryGeoJSON = getNeighborhoodBoundaryAsGeoJSON(neighborhood.getId().toString());
            boundaryGeoJSON.ifPresent(result::setBoundaryGeoJson);
        }

        return result;
    }
}