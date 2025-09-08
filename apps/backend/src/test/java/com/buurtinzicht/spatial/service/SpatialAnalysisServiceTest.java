package com.buurtinzicht.spatial.service;

import com.buurtinzicht.domain.model.Neighborhood;
import com.buurtinzicht.domain.repository.NeighborhoodRepository;
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
class SpatialAnalysisServiceTest {

    @Mock
    private NeighborhoodRepository neighborhoodRepository;

    private SpatialAnalysisService spatialAnalysisService;
    private GeometryFactory geometryFactory;

    @BeforeEach
    void setUp() {
        spatialAnalysisService = new SpatialAnalysisService(neighborhoodRepository);
        geometryFactory = new GeometryFactory();
    }

    @Test
    void compareNeighborhoods_ShouldReturnComparison_WhenBothNeighborhoodsExist() {
        // Given
        String nis1 = "21004";
        String nis2 = "11002";

        Neighborhood brussels = createMockNeighborhood("21004", "Brussels", "City of Brussels");
        brussels.setPopulation(179277L);
        brussels.setAreaKm2(BigDecimal.valueOf(32.61));
        brussels.setUrbanizationLevel(Neighborhood.UrbanizationLevel.METROPOLITAN);

        Neighborhood antwerp = createMockNeighborhood("11002", "Antwerp", "Antwerp");
        antwerp.setPopulation(529247L);
        antwerp.setAreaKm2(BigDecimal.valueOf(204.51));
        antwerp.setUrbanizationLevel(Neighborhood.UrbanizationLevel.METROPOLITAN);

        when(neighborhoodRepository.findByNisCode(nis1)).thenReturn(Optional.of(brussels));
        when(neighborhoodRepository.findByNisCode(nis2)).thenReturn(Optional.of(antwerp));

        Object[] distanceResult = {nis1, nis2, 45.67};
        when(neighborhoodRepository.calculateDistanceBetweenNeighborhoods(nis1, nis2))
            .thenReturn(Optional.of(distanceResult));

        // When
        SpatialAnalysisService.NeighborhoodComparisonResult result = 
            spatialAnalysisService.compareNeighborhoods(nis1, nis2);

        // Then
        assertNotNull(result);
        assertEquals("Brussels", result.getNeighborhood1().getName());
        assertEquals("Antwerp", result.getNeighborhood2().getName());
        assertEquals(BigDecimal.valueOf(45.67), result.getDistance());
        assertNotNull(result.getPopulationComparison());
        assertNotNull(result.getAreaComparison());
        assertTrue(result.getPopulationComparison().getDifference() < 0); // Brussels has less population
        assertTrue(result.getAreaComparison().getDifference() < 0); // Brussels has smaller area
    }

    @Test
    void compareNeighborhoods_ShouldThrowException_WhenNeighborhoodNotFound() {
        // Given
        String nis1 = "99999";
        String nis2 = "11002";

        when(neighborhoodRepository.findByNisCode(nis1)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
            spatialAnalysisService.compareNeighborhoods(nis1, nis2));
    }

    @Test
    void calculateSpatialStatistics_ShouldReturnStats_WhenRegionProvided() {
        // Given
        String region = "Brussels-Capital Region";
        
        List<Neighborhood> neighborhoods = Arrays.asList(
            createMockNeighborhoodWithStats("21004", "Brussels", 179277L, BigDecimal.valueOf(32.61)),
            createMockNeighborhoodWithStats("21005", "Etterbeek", 47414L, BigDecimal.valueOf(3.15))
        );

        when(neighborhoodRepository.findByRegionAndIsActiveTrue(region)).thenReturn(neighborhoods);

        // When
        SpatialAnalysisService.SpatialStatistics result = 
            spatialAnalysisService.calculateSpatialStatistics(region);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getTotalNeighborhoods());
        assertEquals(BigDecimal.valueOf(17.88), result.getAverageArea()); // (32.61 + 3.15) / 2
        assertEquals(Long.valueOf(226691), result.getTotalPopulation()); // 179277 + 47414
        assertEquals(Long.valueOf(113345), result.getAveragePopulation()); // 226691 / 2
        assertEquals(region, result.getRegion());
    }

    @Test
    void calculateSpatialStatistics_ShouldReturnAllStats_WhenNoRegionProvided() {
        // Given
        List<Neighborhood> allNeighborhoods = Arrays.asList(
            createMockNeighborhoodWithStats("21004", "Brussels", 179277L, BigDecimal.valueOf(32.61)),
            createMockNeighborhoodWithStats("11002", "Antwerp", 529247L, BigDecimal.valueOf(204.51)),
            createMockNeighborhoodWithStats("44021", "Ghent", 262219L, BigDecimal.valueOf(156.18))
        );

        when(neighborhoodRepository.findByIsActiveTrue()).thenReturn(allNeighborhoods);

        // When
        SpatialAnalysisService.SpatialStatistics result = 
            spatialAnalysisService.calculateSpatialStatistics(null);

        // Then
        assertNotNull(result);
        assertEquals(3, result.getTotalNeighborhoods());
        assertEquals(BigDecimal.valueOf(131.10), result.getAverageArea()); // (32.61 + 204.51 + 156.18) / 3
        assertEquals(Long.valueOf(970743), result.getTotalPopulation());
        assertEquals(Long.valueOf(323581), result.getAveragePopulation());
        assertNull(result.getRegion());
    }

    @Test
    void findNeighborhoodClusters_ShouldReturnClusters_WhenLanguageFilterProvided() {
        // Given
        int minSize = 2;
        String language = "fr";

        List<Neighborhood> frenchNeighborhoods = Arrays.asList(
            createMockNeighborhoodWithLanguage("21004", "Brussels", "fr"),
            createMockNeighborhoodWithLanguage("52011", "Charleroi", "fr"),
            createMockNeighborhoodWithLanguage("62063", "Liège", "fr")
        );

        when(neighborhoodRepository.findByPrimaryLanguageAndIsActiveTrue(language))
            .thenReturn(frenchNeighborhoods);

        // When
        List<SpatialAnalysisService.NeighborhoodCluster> result = 
            spatialAnalysisService.findNeighborhoodClusters(minSize, language);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size()); // Should form one cluster
        SpatialAnalysisService.NeighborhoodCluster cluster = result.get(0);
        assertEquals(3, cluster.getNeighborhoods().size());
        assertEquals("fr", cluster.getPrimaryLanguage());
        assertTrue(cluster.getAveragePopulation() > 0);
    }

    @Test
    void findNeighborhoodClusters_ShouldReturnAllClusters_WhenNoLanguageFilter() {
        // Given
        int minSize = 3;

        List<Neighborhood> allNeighborhoods = Arrays.asList(
            createMockNeighborhoodWithLanguage("21004", "Brussels", "fr"),
            createMockNeighborhoodWithLanguage("11002", "Antwerp", "nl"),
            createMockNeighborhoodWithLanguage("44021", "Ghent", "nl"),
            createMockNeighborhoodWithLanguage("31005", "Bruges", "nl")
        );

        when(neighborhoodRepository.findByIsActiveTrue()).thenReturn(allNeighborhoods);

        // When
        List<SpatialAnalysisService.NeighborhoodCluster> result = 
            spatialAnalysisService.findNeighborhoodClusters(minSize, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size()); // Dutch cluster should meet minimum size
        SpatialAnalysisService.NeighborhoodCluster cluster = result.get(0);
        assertEquals("nl", cluster.getPrimaryLanguage());
        assertEquals(3, cluster.getNeighborhoods().size());
    }

    private Neighborhood createMockNeighborhood(String nisCode, String name, String municipality) {
        Neighborhood neighborhood = new Neighborhood();
        neighborhood.setId(UUID.randomUUID());
        neighborhood.setNisCode(nisCode);
        neighborhood.setName(name);
        neighborhood.setMunicipality(municipality);
        neighborhood.setIsActive(true);

        Point centroid = geometryFactory.createPoint(new Coordinate(4.3488, 50.8505));
        neighborhood.setCentroid(centroid);

        return neighborhood;
    }

    private Neighborhood createMockNeighborhoodWithStats(String nisCode, String name, Long population, BigDecimal area) {
        Neighborhood neighborhood = createMockNeighborhood(nisCode, name, name);
        neighborhood.setPopulation(population);
        neighborhood.setAreaKm2(area);
        if (population != null && area != null && area.compareTo(BigDecimal.ZERO) > 0) {
            neighborhood.setPopulationDensity(
                BigDecimal.valueOf(population).divide(area, 2, BigDecimal.ROUND_HALF_UP)
            );
        }
        return neighborhood;
    }

    private Neighborhood createMockNeighborhoodWithLanguage(String nisCode, String name, String language) {
        Neighborhood neighborhood = createMockNeighborhood(nisCode, name, name);
        neighborhood.setPrimaryLanguage(language);
        neighborhood.setPopulation(100000L);
        neighborhood.setAreaKm2(BigDecimal.valueOf(50.0));
        return neighborhood;
    }
}