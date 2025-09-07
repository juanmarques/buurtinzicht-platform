package com.buurtinzicht.domain.repository;

import com.buurtinzicht.domain.model.Neighborhood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NeighborhoodRepository extends JpaRepository<Neighborhood, UUID> {

    Optional<Neighborhood> findByNisCode(String nisCode);

    List<Neighborhood> findByMunicipality(String municipality);

    List<Neighborhood> findByProvince(String province);

    List<Neighborhood> findByRegion(String region);

    List<Neighborhood> findByPrimaryLanguage(String primaryLanguage);

    List<Neighborhood> findByUrbanizationLevel(Neighborhood.UrbanizationLevel urbanizationLevel);

    List<Neighborhood> findByIsActiveTrue();

    @Query("SELECT n FROM Neighborhood n WHERE n.isActive = true AND n.population >= :minPopulation")
    List<Neighborhood> findActiveNeighborhoodsWithMinPopulation(@Param("minPopulation") Long minPopulation);

    @Query("SELECT n FROM Neighborhood n WHERE n.isActive = true AND n.population BETWEEN :minPop AND :maxPop")
    List<Neighborhood> findActiveNeighborhoodsByPopulationRange(@Param("minPop") Long minPopulation, @Param("maxPop") Long maxPopulation);

    @Query("SELECT n FROM Neighborhood n WHERE n.postalCodes LIKE %:postalCode%")
    List<Neighborhood> findByPostalCodeContaining(@Param("postalCode") String postalCode);

    // Spatial queries using PostGIS functions
    @Query(value = """
        SELECT n.*, ST_Distance_Sphere(n.centroid, ST_Point(:longitude, :latitude)) / 1000.0 as distance_km
        FROM neighborhoods n 
        WHERE n.is_active = true
        AND ST_DWithin(n.centroid, ST_Point(:longitude, :latitude), :radiusMeters)
        ORDER BY distance_km
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> findNeighborhoodsWithinRadius(
        @Param("latitude") double latitude,
        @Param("longitude") double longitude,
        @Param("radiusMeters") double radiusMeters,
        @Param("limit") int limit
    );

    @Query(value = """
        SELECT n.* 
        FROM neighborhoods n
        WHERE n.is_active = true
        AND ST_Contains(n.boundary, ST_Point(:longitude, :latitude))
        LIMIT 1
        """, nativeQuery = true)
    Optional<Neighborhood> findNeighborhoodContainingPoint(
        @Param("latitude") double latitude,
        @Param("longitude") double longitude
    );

    @Query(value = """
        SELECT n.*, ST_Distance_Sphere(n.centroid, ST_Point(:longitude, :latitude)) / 1000.0 as distance_km
        FROM neighborhoods n
        WHERE n.is_active = true
        AND (:urbanizationLevel IS NULL OR n.urbanization_level = :urbanizationLevel)
        AND (:primaryLanguage IS NULL OR n.primary_language = :primaryLanguage)
        AND (:minPopulation IS NULL OR n.population >= :minPopulation)
        AND (:maxPopulation IS NULL OR n.population <= :maxPopulation)
        AND ST_DWithin(n.centroid, ST_Point(:longitude, :latitude), :radiusMeters)
        ORDER BY distance_km
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> findNeighborhoodsWithFilters(
        @Param("latitude") double latitude,
        @Param("longitude") double longitude,
        @Param("radiusMeters") double radiusMeters,
        @Param("urbanizationLevel") String urbanizationLevel,
        @Param("primaryLanguage") String primaryLanguage,
        @Param("minPopulation") Long minPopulation,
        @Param("maxPopulation") Long maxPopulation,
        @Param("limit") int limit
    );

    @Query(value = """
        SELECT n.* 
        FROM neighborhoods n1, neighborhoods n2
        WHERE n1.nis_code = :nisCode 
        AND n1.id != n2.id
        AND n2.is_active = true
        AND ST_Touches(n1.boundary, n2.boundary)
        ORDER BY n2.name
        """, nativeQuery = true)
    List<Neighborhood> findNeighboringAreas(@Param("nisCode") String nisCode);

    @Query(value = """
        SELECT n.*
        FROM neighborhoods n
        WHERE n.is_active = true
        AND ST_Intersects(n.boundary, ST_Buffer(ST_Point(:longitude, :latitude), :bufferMeters))
        ORDER BY ST_Distance(n.centroid, ST_Point(:longitude, :latitude))
        LIMIT :limit
        """, nativeQuery = true)
    List<Neighborhood> findNeighborhoodsIntersectingBuffer(
        @Param("latitude") double latitude,
        @Param("longitude") double longitude,
        @Param("bufferMeters") double bufferMeters,
        @Param("limit") int limit
    );

    @Query("SELECT COUNT(n) FROM Neighborhood n WHERE n.isActive = true")
    long countActiveNeighborhoods();

    @Query("SELECT COUNT(n) FROM Neighborhood n WHERE n.isActive = true AND n.urbanizationLevel = :level")
    long countByUrbanizationLevel(@Param("level") Neighborhood.UrbanizationLevel level);

    @Query("SELECT COUNT(n) FROM Neighborhood n WHERE n.isActive = true AND n.primaryLanguage = :language")
    long countByPrimaryLanguage(@Param("language") String language);

    @Query(value = """
        SELECT ST_AsGeoJSON(n.boundary) 
        FROM neighborhoods n 
        WHERE n.id = :id
        """, nativeQuery = true)
    Optional<String> findBoundaryAsGeoJSON(@Param("id") UUID id);

    @Query(value = """
        SELECT ST_AsGeoJSON(ST_Centroid(n.boundary))
        FROM neighborhoods n
        WHERE n.id = :id
        """, nativeQuery = true)
    Optional<String> findCentroidAsGeoJSON(@Param("id") UUID id);

    @Query(value = """
        SELECT 
            ST_Area(ST_Transform(n.boundary, 3857)) / 1000000.0 as area_km2,
            ST_Perimeter(ST_Transform(n.boundary, 3857)) / 1000.0 as perimeter_km
        FROM neighborhoods n 
        WHERE n.id = :id
        """, nativeQuery = true)
    Optional<Object[]> calculateAreaAndPerimeter(@Param("id") UUID id);

    @Query(value = """
        SELECT n1.nis_code, n2.nis_code, ST_Distance_Sphere(n1.centroid, n2.centroid) / 1000.0 as distance_km
        FROM neighborhoods n1, neighborhoods n2
        WHERE n1.nis_code = :fromNisCode
        AND n2.nis_code = :toNisCode
        AND n1.is_active = true
        AND n2.is_active = true
        """, nativeQuery = true)
    Optional<Object[]> calculateDistanceBetweenNeighborhoods(
        @Param("fromNisCode") String fromNisCode,
        @Param("toNisCode") String toNisCode
    );

    // Full-text search in neighborhood names
    @Query(value = """
        SELECT n.*, ts_rank(to_tsvector('dutch', n.name || ' ' || n.municipality), to_tsquery('dutch', :searchQuery)) as rank
        FROM neighborhoods n
        WHERE n.is_active = true
        AND (to_tsvector('dutch', n.name || ' ' || n.municipality) @@ to_tsquery('dutch', :searchQuery)
             OR n.name ILIKE %:searchTerm%
             OR n.municipality ILIKE %:searchTerm%
             OR n.name_nl ILIKE %:searchTerm%
             OR n.name_fr ILIKE %:searchTerm%)
        ORDER BY rank DESC, n.name
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> searchNeighborhoodsByName(
        @Param("searchQuery") String searchQuery,
        @Param("searchTerm") String searchTerm,
        @Param("limit") int limit
    );

    @Query("SELECT DISTINCT n.municipality FROM Neighborhood n WHERE n.isActive = true ORDER BY n.municipality")
    List<String> findAllActiveMunicipalities();

    @Query("SELECT DISTINCT n.province FROM Neighborhood n WHERE n.isActive = true AND n.province IS NOT NULL ORDER BY n.province")
    List<String> findAllActiveProvinces();

    @Query("SELECT DISTINCT n.region FROM Neighborhood n WHERE n.isActive = true AND n.region IS NOT NULL ORDER BY n.region")
    List<String> findAllActiveRegions();
}