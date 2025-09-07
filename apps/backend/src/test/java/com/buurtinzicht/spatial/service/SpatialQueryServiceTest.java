package com.buurtinzicht.spatial.service;

import com.buurtinzicht.domain.model.Neighborhood;
import com.buurtinzicht.domain.repository.NeighborhoodRepository;
import com.buurtinzicht.spatial.dto.SpatialQueryRequest;
import com.buurtinzicht.spatial.dto.SpatialQueryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class SpatialQueryServiceTest {

    @Mock
    private NeighborhoodRepository neighborhoodRepository;

    private SpatialQueryService spatialQueryService;
    private GeometryFactory geometryFactory;

    @BeforeEach
    void setUp() {
        spatialQueryService = new SpatialQueryService(neighborhoodRepository);
        geometryFactory = new GeometryFactory();
    }

    @Test
    void findNeighborhoods_ShouldReturnResults_WhenNeighborhoodsFound() {
        // Given
        SpatialQueryRequest request = new SpatialQueryRequest();
        request.setLatitude(BigDecimal.valueOf(50.8505));
        request.setLongitude(BigDecimal.valueOf(4.3488));
        request.setRadiusKm(BigDecimal.valueOf(5.0));
        request.setLimit(10);

        Neighborhood brussels = createMockNeighborhood("21004", "Brussels", "City of Brussels");
        Object[] result1 = {brussels, 2.5};

        Neighborhood ixelles = createMockNeighborhood("21009", "Ixelles", "Ixelles");
        Object[] result2 = {ixelles, 3.2};

        List<Object[]> mockResults = Arrays.asList(result1, result2);

        when(neighborhoodRepository.findNeighborhoodsWithFilters(
            eq(50.8505), eq(4.3488), eq(5000.0), isNull(), isNull(), isNull(), isNull(), eq(10)))
            .thenReturn(mockResults);

        // When
        SpatialQueryResponse response = spatialQueryService.findNeighborhoods(request);

        // Then
        assertNotNull(response);
        assertEquals(2, response.getReturnedResults());
        assertEquals(2, response.getTotalResults());
        assertNotNull(response.getNeighborhoods());
        assertEquals(2, response.getNeighborhoods().size());

        SpatialQueryResponse.NeighborhoodResult firstResult = response.getNeighborhoods().get(0);
        assertEquals("21004", firstResult.getNisCode());
        assertEquals("Brussels", firstResult.getName());
        assertEquals("City of Brussels", firstResult.getMunicipality());
        assertEquals(BigDecimal.valueOf(2.5), firstResult.getDistanceKm());

        assertNotNull(response.getSearchCenter());
        assertEquals(BigDecimal.valueOf(50.8505), response.getSearchCenter().getLatitude());
        assertEquals(BigDecimal.valueOf(4.3488), response.getSearchCenter().getLongitude());
        assertEquals(BigDecimal.valueOf(5.0), response.getSearchRadiusKm());
    }

    @Test
    void findNeighborhoods_ShouldApplyFilters_WhenFiltersProvided() {
        // Given
        SpatialQueryRequest request = new SpatialQueryRequest();
        request.setLatitude(BigDecimal.valueOf(50.8505));
        request.setLongitude(BigDecimal.valueOf(4.3488));
        request.setRadiusKm(BigDecimal.valueOf(10.0));
        request.setUrbanizationLevel("URBAN");
        request.setPrimaryLanguage("fr");
        request.setMinPopulation(50000L);
        request.setMaxPopulation(500000L);
        request.setLimit(5);

        Neighborhood brussels = createMockNeighborhood("21004", "Brussels", "City of Brussels");
        brussels.setUrbanizationLevel(Neighborhood.UrbanizationLevel.URBAN);
        brussels.setPrimaryLanguage("fr");
        brussels.setPopulation(179277L);

        Object[] result = {brussels, 1.2};
        List<Object[]> mockResults = Arrays.asList(result);

        when(neighborhoodRepository.findNeighborhoodsWithFilters(
            eq(50.8505), eq(4.3488), eq(10000.0), eq("URBAN"), eq("fr"), eq(50000L), eq(500000L), eq(5)))
            .thenReturn(mockResults);

        // When
        SpatialQueryResponse response = spatialQueryService.findNeighborhoods(request);

        // Then
        assertNotNull(response);
        assertEquals(1, response.getReturnedResults());
        SpatialQueryResponse.NeighborhoodResult result1 = response.getNeighborhoods().get(0);
        assertEquals("URBAN", result1.getUrbanizationLevel());
    }

    @Test
    void findNeighborhoodByPoint_ShouldReturnNeighborhood_WhenPointContained() {
        // Given
        BigDecimal latitude = BigDecimal.valueOf(50.8505);
        BigDecimal longitude = BigDecimal.valueOf(4.3488);

        Neighborhood brussels = createMockNeighborhood("21004", "Brussels", "City of Brussels");

        when(neighborhoodRepository.findNeighborhoodContainingPoint(50.8505, 4.3488))
            .thenReturn(Optional.of(brussels));

        // When
        Optional<Neighborhood> result = spatialQueryService.findNeighborhoodByPoint(latitude, longitude);

        // Then
        assertTrue(result.isPresent());
        assertEquals("Brussels", result.get().getName());
        assertEquals("21004", result.get().getNisCode());
    }

    @Test
    void findNeighborhoodByPoint_ShouldReturnEmpty_WhenNoNeighborhoodContainsPoint() {
        // Given
        BigDecimal latitude = BigDecimal.valueOf(51.0);
        BigDecimal longitude = BigDecimal.valueOf(5.0);

        when(neighborhoodRepository.findNeighborhoodContainingPoint(51.0, 5.0))
            .thenReturn(Optional.empty());

        // When
        Optional<Neighborhood> result = spatialQueryService.findNeighborhoodByPoint(latitude, longitude);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void findNeighboringAreas_ShouldReturnNeighbors_WhenNeighborsExist() {
        // Given
        String nisCode = "21004";
        
        Neighborhood ixelles = createMockNeighborhood("21009", "Ixelles", "Ixelles");
        Neighborhood etterbeek = createMockNeighborhood("21005", "Etterbeek", "Etterbeek");
        List<Neighborhood> neighbors = Arrays.asList(ixelles, etterbeek);

        when(neighborhoodRepository.findNeighboringAreas(nisCode))
            .thenReturn(neighbors);

        // When
        List<Neighborhood> result = spatialQueryService.findNeighboringAreas(nisCode);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Ixelles", result.get(0).getName());
        assertEquals("Etterbeek", result.get(1).getName());
    }

    @Test
    void searchByName_ShouldReturnMatches_WhenNameMatches() {
        // Given
        String searchTerm = "Brussels";
        int limit = 5;

        Neighborhood brussels = createMockNeighborhood("21004", "Brussels", "City of Brussels");
        Object[] result1 = {brussels, 1.0}; // rank

        List<Object[]> mockResults = Arrays.asList(result1);

        when(neighborhoodRepository.searchNeighborhoodsByName("Brussels", "Brussels", limit))
            .thenReturn(mockResults);

        // When
        List<Neighborhood> results = spatialQueryService.searchByName(searchTerm, limit);

        // Then
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Brussels", results.get(0).getName());
    }

    @Test
    void getNeighborhoodBoundaryAsGeoJSON_ShouldReturnGeoJSON_WhenBoundaryExists() {
        // Given
        String neighborhoodId = UUID.randomUUID().toString();
        String geoJson = "{\"type\":\"MultiPolygon\",\"coordinates\":[[[[4.3320,50.8267],[4.3730,50.8267],[4.3730,50.8650],[4.3320,50.8650],[4.3320,50.8267]]]]}";

        when(neighborhoodRepository.findBoundaryAsGeoJSON(UUID.fromString(neighborhoodId)))
            .thenReturn(Optional.of(geoJson));

        // When
        Optional<String> result = spatialQueryService.getNeighborhoodBoundaryAsGeoJSON(neighborhoodId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(geoJson, result.get());
    }

    @Test
    void getNeighborhoodBoundaryAsGeoJSON_ShouldReturnEmpty_WhenInvalidId() {
        // Given
        String invalidId = "invalid-uuid";

        // When
        Optional<String> result = spatialQueryService.getNeighborhoodBoundaryAsGeoJSON(invalidId);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void calculateDistance_ShouldReturnDistance_WhenBothNeighborhoodsExist() {
        // Given
        String fromNisCode = "21004";
        String toNisCode = "11002";
        Double distance = 45.67;

        Object[] mockResult = {fromNisCode, toNisCode, distance};

        when(neighborhoodRepository.calculateDistanceBetweenNeighborhoods(fromNisCode, toNisCode))
            .thenReturn(Optional.of(mockResult));

        // When
        BigDecimal result = spatialQueryService.calculateDistance(fromNisCode, toNisCode);

        // Then
        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(45.67), result);
    }

    @Test
    void calculateDistance_ShouldReturnNull_WhenNeighborhoodNotFound() {
        // Given
        String fromNisCode = "99999";
        String toNisCode = "11002";

        when(neighborhoodRepository.calculateDistanceBetweenNeighborhoods(fromNisCode, toNisCode))
            .thenReturn(Optional.empty());

        // When
        BigDecimal result = spatialQueryService.calculateDistance(fromNisCode, toNisCode);

        // Then
        assertNull(result);
    }

    @Test
    void getNeighborhoodDetails_ShouldReturnDetails_WhenNeighborhoodExists() {
        // Given
        String nisCode = "21004";
        String language = "fr";

        Neighborhood brussels = createMockNeighborhood("21004", "Brussels", "City of Brussels");
        brussels.setNameFr("Bruxelles");
        brussels.setPopulation(179277L);
        brussels.setAreaKm2(BigDecimal.valueOf(32.61));
        brussels.setPrimaryLanguage("fr");

        when(neighborhoodRepository.findByNisCode(nisCode))
            .thenReturn(Optional.of(brussels));

        // When
        SpatialQueryResponse.NeighborhoodResult result = 
            spatialQueryService.getNeighborhoodDetails(nisCode, language);

        // Then
        assertNotNull(result);
        assertEquals("21004", result.getNisCode());
        assertEquals("Brussels", result.getName());
        assertEquals("Bruxelles", result.getLocalizedName());
        assertEquals(Long.valueOf(179277), result.getPopulation());
        assertEquals(BigDecimal.valueOf(32.61), result.getAreaKm2());
    }

    @Test
    void getNeighborhoodDetails_ShouldReturnNull_WhenNeighborhoodNotFound() {
        // Given
        String nisCode = "99999";
        String language = "nl";

        when(neighborhoodRepository.findByNisCode(nisCode))
            .thenReturn(Optional.empty());

        // When
        SpatialQueryResponse.NeighborhoodResult result = 
            spatialQueryService.getNeighborhoodDetails(nisCode, language);

        // Then
        assertNull(result);
    }

    @Test
    void getAllMunicipalities_ShouldReturnMunicipalities() {
        // Given
        List<String> municipalities = Arrays.asList("Brussels", "Antwerp", "Ghent");

        when(neighborhoodRepository.findAllActiveMunicipalities())
            .thenReturn(municipalities);

        // When
        List<String> result = spatialQueryService.getAllMunicipalities();

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains("Brussels"));
        assertTrue(result.contains("Antwerp"));
        assertTrue(result.contains("Ghent"));
    }

    private Neighborhood createMockNeighborhood(String nisCode, String name, String municipality) {
        Neighborhood neighborhood = new Neighborhood();
        neighborhood.setId(UUID.randomUUID());
        neighborhood.setNisCode(nisCode);
        neighborhood.setName(name);
        neighborhood.setMunicipality(municipality);
        neighborhood.setIsActive(true);

        // Create a mock centroid point
        Point centroid = geometryFactory.createPoint(new Coordinate(4.3488, 50.8505));
        neighborhood.setCentroid(centroid);

        return neighborhood;
    }
}