package com.buurtinzicht.spatial.service;

import com.buurtinzicht.domain.model.Neighborhood;
import com.buurtinzicht.domain.repository.NeighborhoodRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SpatialAnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(SpatialAnalysisService.class);

    private final NeighborhoodRepository neighborhoodRepository;
    private final GeometryFactory geometryFactory;

    @Autowired
    public SpatialAnalysisService(NeighborhoodRepository neighborhoodRepository) {
        this.neighborhoodRepository = neighborhoodRepository;
        this.geometryFactory = new GeometryFactory();
    }

    public SpatialAnalysisResult analyzeSpatialDistribution(List<Point> points, double analysisRadiusKm) {
        logger.info("Analyzing spatial distribution for {} points with radius {} km", points.size(), analysisRadiusKm);

        SpatialAnalysisResult result = new SpatialAnalysisResult();
        
        if (points.isEmpty()) {
            return result;
        }

        // Calculate center of mass
        Point centerOfMass = calculateCenterOfMass(points);
        result.setCenterOfMass(centerOfMass);

        // Calculate spatial statistics
        result.setPointCount(points.size());
        result.setAnalysisRadius(BigDecimal.valueOf(analysisRadiusKm));

        // Calculate density clusters
        result.setClusters(findDensityClusters(points, analysisRadiusKm));

        // Calculate spatial dispersion
        result.setSpatialDispersion(calculateSpatialDispersion(points, centerOfMass));

        // Find neighborhoods affected
        result.setAffectedNeighborhoods(findAffectedNeighborhoods(points, analysisRadiusKm));

        return result;
    }

    public NeighborhoodComparisonResult compareNeighborhoods(String nisCode1, String nisCode2) {
        logger.info("Comparing neighborhoods: {} vs {}", nisCode1, nisCode2);

        Optional<Neighborhood> n1 = neighborhoodRepository.findByNisCode(nisCode1);
        Optional<Neighborhood> n2 = neighborhoodRepository.findByNisCode(nisCode2);

        if (n1.isEmpty() || n2.isEmpty()) {
            throw new IllegalArgumentException("One or both neighborhoods not found");
        }

        Neighborhood neighborhood1 = n1.get();
        Neighborhood neighborhood2 = n2.get();

        NeighborhoodComparisonResult result = new NeighborhoodComparisonResult();
        result.setNeighborhood1(neighborhood1);
        result.setNeighborhood2(neighborhood2);

        // Calculate distance between centroids
        if (neighborhood1.getCentroid() != null && neighborhood2.getCentroid() != null) {
            BigDecimal distance = calculateDistance(neighborhood1.getCentroid(), neighborhood2.getCentroid());
            result.setDistanceKm(distance);
        }

        // Compare areas
        if (neighborhood1.getAreaKm2() != null && neighborhood2.getAreaKm2() != null) {
            BigDecimal areaRatio = neighborhood1.getAreaKm2().divide(neighborhood2.getAreaKm2(), 4, RoundingMode.HALF_UP);
            result.setAreaRatio(areaRatio);
        }

        // Compare populations
        if (neighborhood1.getPopulation() != null && neighborhood2.getPopulation() != null) {
            double populationRatio = (double) neighborhood1.getPopulation() / neighborhood2.getPopulation();
            result.setPopulationRatio(BigDecimal.valueOf(populationRatio).setScale(4, RoundingMode.HALF_UP));
        }

        // Compare population densities
        if (neighborhood1.getPopulationDensity() != null && neighborhood2.getPopulationDensity() != null) {
            BigDecimal densityRatio = neighborhood1.getPopulationDensity()
                .divide(neighborhood2.getPopulationDensity(), 4, RoundingMode.HALF_UP);
            result.setPopulationDensityRatio(densityRatio);
        }

        // Check if they are adjacent
        List<Neighborhood> neighbors = neighborhoodRepository.findNeighboringAreas(nisCode1);
        boolean adjacent = neighbors.stream().anyMatch(n -> n.getNisCode().equals(nisCode2));
        result.setAdjacent(adjacent);

        // Similarity score based on various factors
        result.setSimilarityScore(calculateSimilarityScore(neighborhood1, neighborhood2));

        return result;
    }

    public List<NeighborhoodCluster> findNeighborhoodClusters(int minClusterSize, String primaryLanguage) {
        logger.info("Finding neighborhood clusters with min size {} and language {}", minClusterSize, primaryLanguage);

        List<Neighborhood> neighborhoods;
        if (primaryLanguage != null) {
            neighborhoods = neighborhoodRepository.findByPrimaryLanguage(primaryLanguage);
        } else {
            neighborhoods = neighborhoodRepository.findByIsActiveTrue();
        }

        return performClusterAnalysis(neighborhoods, minClusterSize);
    }

    public SpatialStatistics calculateSpatialStatistics(String region) {
        logger.info("Calculating spatial statistics for region: {}", region);

        List<Neighborhood> neighborhoods = region != null ? 
            neighborhoodRepository.findByRegion(region) : 
            neighborhoodRepository.findByIsActiveTrue();

        SpatialStatistics stats = new SpatialStatistics();
        stats.setRegion(region);
        stats.setTotalNeighborhoods(neighborhoods.size());

        if (neighborhoods.isEmpty()) {
            return stats;
        }

        // Calculate area statistics
        List<BigDecimal> areas = neighborhoods.stream()
            .map(Neighborhood::getAreaKm2)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        if (!areas.isEmpty()) {
            stats.setTotalAreaKm2(areas.stream().reduce(BigDecimal.ZERO, BigDecimal::add));
            stats.setAverageAreaKm2(stats.getTotalAreaKm2().divide(
                BigDecimal.valueOf(areas.size()), 4, RoundingMode.HALF_UP));
            stats.setMinAreaKm2(areas.stream().min(BigDecimal::compareTo).orElse(null));
            stats.setMaxAreaKm2(areas.stream().max(BigDecimal::compareTo).orElse(null));
        }

        // Calculate population statistics
        List<Long> populations = neighborhoods.stream()
            .map(Neighborhood::getPopulation)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        if (!populations.isEmpty()) {
            stats.setTotalPopulation(populations.stream().mapToLong(Long::longValue).sum());
            stats.setAveragePopulation(stats.getTotalPopulation() / populations.size());
            stats.setMinPopulation(populations.stream().min(Long::compareTo).orElse(null));
            stats.setMaxPopulation(populations.stream().max(Long::compareTo).orElse(null));
        }

        // Calculate population density statistics
        List<BigDecimal> densities = neighborhoods.stream()
            .map(Neighborhood::getPopulationDensity)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        if (!densities.isEmpty()) {
            BigDecimal totalDensity = densities.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            stats.setAveragePopulationDensity(totalDensity.divide(
                BigDecimal.valueOf(densities.size()), 2, RoundingMode.HALF_UP));
            stats.setMinPopulationDensity(densities.stream().min(BigDecimal::compareTo).orElse(null));
            stats.setMaxPopulationDensity(densities.stream().max(BigDecimal::compareTo).orElse(null));
        }

        // Calculate urbanization distribution
        Map<Neighborhood.UrbanizationLevel, Long> urbanizationCounts = neighborhoods.stream()
            .filter(n -> n.getUrbanizationLevel() != null)
            .collect(Collectors.groupingBy(
                Neighborhood::getUrbanizationLevel, 
                Collectors.counting()));
        stats.setUrbanizationDistribution(urbanizationCounts);

        // Calculate language distribution
        Map<String, Long> languageCounts = neighborhoods.stream()
            .filter(n -> n.getPrimaryLanguage() != null)
            .collect(Collectors.groupingBy(
                Neighborhood::getPrimaryLanguage,
                Collectors.counting()));
        stats.setLanguageDistribution(languageCounts);

        return stats;
    }

    private Point calculateCenterOfMass(List<Point> points) {
        double totalX = 0;
        double totalY = 0;

        for (Point point : points) {
            totalX += point.getX();
            totalY += point.getY();
        }

        double centerX = totalX / points.size();
        double centerY = totalY / points.size();

        return geometryFactory.createPoint(new Coordinate(centerX, centerY));
    }

    private List<SpatialCluster> findDensityClusters(List<Point> points, double radiusKm) {
        List<SpatialCluster> clusters = new ArrayList<>();
        Set<Point> processed = new HashSet<>();

        for (Point point : points) {
            if (processed.contains(point)) continue;

            List<Point> nearbyPoints = findPointsWithinRadius(point, points, radiusKm);
            if (nearbyPoints.size() >= 2) { // Minimum cluster size
                SpatialCluster cluster = new SpatialCluster();
                cluster.setCenter(point);
                cluster.setPoints(nearbyPoints);
                cluster.setDensity(nearbyPoints.size());
                clusters.add(cluster);
                processed.addAll(nearbyPoints);
            }
        }

        return clusters;
    }

    private List<Point> findPointsWithinRadius(Point center, List<Point> allPoints, double radiusKm) {
        return allPoints.stream()
            .filter(point -> calculateDistance(center, point).doubleValue() <= radiusKm)
            .collect(Collectors.toList());
    }

    private BigDecimal calculateSpatialDispersion(List<Point> points, Point center) {
        double totalDistanceSquared = 0;

        for (Point point : points) {
            double distance = calculateDistance(center, point).doubleValue();
            totalDistanceSquared += distance * distance;
        }

        double variance = totalDistanceSquared / points.size();
        return BigDecimal.valueOf(Math.sqrt(variance)).setScale(4, RoundingMode.HALF_UP);
    }

    private List<Neighborhood> findAffectedNeighborhoods(List<Point> points, double radiusKm) {
        Set<Neighborhood> affected = new HashSet<>();

        for (Point point : points) {
            Optional<Neighborhood> neighborhood = neighborhoodRepository.findNeighborhoodContainingPoint(
                point.getY(), point.getX());
            neighborhood.ifPresent(affected::add);

            // Also find nearby neighborhoods
            List<Object[]> nearby = neighborhoodRepository.findNeighborhoodsWithinRadius(
                point.getY(), point.getX(), radiusKm * 1000, 10);
            
            for (Object[] row : nearby) {
                affected.add((Neighborhood) row[0]);
            }
        }

        return new ArrayList<>(affected);
    }

    private BigDecimal calculateDistance(Point p1, Point p2) {
        // Haversine formula for great-circle distance
        double lat1 = Math.toRadians(p1.getY());
        double lon1 = Math.toRadians(p1.getX());
        double lat2 = Math.toRadians(p2.getY());
        double lon2 = Math.toRadians(p2.getX());

        double dlat = lat2 - lat1;
        double dlon = lon2 - lon1;

        double a = Math.sin(dlat/2) * Math.sin(dlat/2) + 
                  Math.cos(lat1) * Math.cos(lat2) * 
                  Math.sin(dlon/2) * Math.sin(dlon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double earthRadiusKm = 6371.0;
        return BigDecimal.valueOf(earthRadiusKm * c).setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateSimilarityScore(Neighborhood n1, Neighborhood n2) {
        int totalFactors = 0;
        int matchingFactors = 0;

        // Compare urbanization level
        if (n1.getUrbanizationLevel() != null && n2.getUrbanizationLevel() != null) {
            totalFactors++;
            if (n1.getUrbanizationLevel().equals(n2.getUrbanizationLevel())) {
                matchingFactors++;
            }
        }

        // Compare primary language
        if (n1.getPrimaryLanguage() != null && n2.getPrimaryLanguage() != null) {
            totalFactors++;
            if (n1.getPrimaryLanguage().equals(n2.getPrimaryLanguage())) {
                matchingFactors++;
            }
        }

        // Compare region
        if (n1.getRegion() != null && n2.getRegion() != null) {
            totalFactors++;
            if (n1.getRegion().equals(n2.getRegion())) {
                matchingFactors++;
            }
        }

        // Compare population density (within 20% tolerance)
        if (n1.getPopulationDensity() != null && n2.getPopulationDensity() != null) {
            totalFactors++;
            BigDecimal ratio = n1.getPopulationDensity().divide(n2.getPopulationDensity(), 4, RoundingMode.HALF_UP);
            if (ratio.compareTo(BigDecimal.valueOf(0.8)) >= 0 && ratio.compareTo(BigDecimal.valueOf(1.2)) <= 0) {
                matchingFactors++;
            }
        }

        return totalFactors > 0 ? 
            BigDecimal.valueOf((double) matchingFactors / totalFactors).setScale(4, RoundingMode.HALF_UP) : 
            BigDecimal.ZERO;
    }

    private List<NeighborhoodCluster> performClusterAnalysis(List<Neighborhood> neighborhoods, int minClusterSize) {
        // Simple clustering based on proximity and similarity
        List<NeighborhoodCluster> clusters = new ArrayList<>();
        Set<Neighborhood> processed = new HashSet<>();

        for (Neighborhood neighborhood : neighborhoods) {
            if (processed.contains(neighborhood)) continue;

            List<Neighborhood> clusterMembers = new ArrayList<>();
            clusterMembers.add(neighborhood);
            processed.add(neighborhood);

            // Find neighboring areas
            List<Neighborhood> neighbors = neighborhoodRepository.findNeighboringAreas(neighborhood.getNisCode());
            for (Neighborhood neighbor : neighbors) {
                if (!processed.contains(neighbor) && isSimilar(neighborhood, neighbor)) {
                    clusterMembers.add(neighbor);
                    processed.add(neighbor);
                }
            }

            if (clusterMembers.size() >= minClusterSize) {
                NeighborhoodCluster cluster = new NeighborhoodCluster();
                cluster.setMembers(clusterMembers);
                cluster.setSize(clusterMembers.size());
                cluster.setCenterNeighborhood(neighborhood);
                clusters.add(cluster);
            }
        }

        return clusters;
    }

    private boolean isSimilar(Neighborhood n1, Neighborhood n2) {
        return calculateSimilarityScore(n1, n2).compareTo(BigDecimal.valueOf(0.6)) >= 0;
    }

    // Inner classes for results
    public static class SpatialAnalysisResult {
        private Point centerOfMass;
        private int pointCount;
        private BigDecimal analysisRadius;
        private List<SpatialCluster> clusters;
        private BigDecimal spatialDispersion;
        private List<Neighborhood> affectedNeighborhoods;

        // Getters and setters
        public Point getCenterOfMass() { return centerOfMass; }
        public void setCenterOfMass(Point centerOfMass) { this.centerOfMass = centerOfMass; }

        public int getPointCount() { return pointCount; }
        public void setPointCount(int pointCount) { this.pointCount = pointCount; }

        public BigDecimal getAnalysisRadius() { return analysisRadius; }
        public void setAnalysisRadius(BigDecimal analysisRadius) { this.analysisRadius = analysisRadius; }

        public List<SpatialCluster> getClusters() { return clusters; }
        public void setClusters(List<SpatialCluster> clusters) { this.clusters = clusters; }

        public BigDecimal getSpatialDispersion() { return spatialDispersion; }
        public void setSpatialDispersion(BigDecimal spatialDispersion) { this.spatialDispersion = spatialDispersion; }

        public List<Neighborhood> getAffectedNeighborhoods() { return affectedNeighborhoods; }
        public void setAffectedNeighborhoods(List<Neighborhood> affectedNeighborhoods) { this.affectedNeighborhoods = affectedNeighborhoods; }
    }

    public static class SpatialCluster {
        private Point center;
        private List<Point> points;
        private int density;

        public Point getCenter() { return center; }
        public void setCenter(Point center) { this.center = center; }

        public List<Point> getPoints() { return points; }
        public void setPoints(List<Point> points) { this.points = points; }

        public int getDensity() { return density; }
        public void setDensity(int density) { this.density = density; }
    }

    public static class NeighborhoodComparisonResult {
        private Neighborhood neighborhood1;
        private Neighborhood neighborhood2;
        private BigDecimal distanceKm;
        private BigDecimal areaRatio;
        private BigDecimal populationRatio;
        private BigDecimal populationDensityRatio;
        private boolean adjacent;
        private BigDecimal similarityScore;

        // Getters and setters
        public Neighborhood getNeighborhood1() { return neighborhood1; }
        public void setNeighborhood1(Neighborhood neighborhood1) { this.neighborhood1 = neighborhood1; }

        public Neighborhood getNeighborhood2() { return neighborhood2; }
        public void setNeighborhood2(Neighborhood neighborhood2) { this.neighborhood2 = neighborhood2; }

        public BigDecimal getDistanceKm() { return distanceKm; }
        public void setDistanceKm(BigDecimal distanceKm) { this.distanceKm = distanceKm; }

        public BigDecimal getAreaRatio() { return areaRatio; }
        public void setAreaRatio(BigDecimal areaRatio) { this.areaRatio = areaRatio; }

        public BigDecimal getPopulationRatio() { return populationRatio; }
        public void setPopulationRatio(BigDecimal populationRatio) { this.populationRatio = populationRatio; }

        public BigDecimal getPopulationDensityRatio() { return populationDensityRatio; }
        public void setPopulationDensityRatio(BigDecimal populationDensityRatio) { this.populationDensityRatio = populationDensityRatio; }

        public boolean isAdjacent() { return adjacent; }
        public void setAdjacent(boolean adjacent) { this.adjacent = adjacent; }

        public BigDecimal getSimilarityScore() { return similarityScore; }
        public void setSimilarityScore(BigDecimal similarityScore) { this.similarityScore = similarityScore; }
    }

    public static class NeighborhoodCluster {
        private List<Neighborhood> members;
        private int size;
        private Neighborhood centerNeighborhood;

        public List<Neighborhood> getMembers() { return members; }
        public void setMembers(List<Neighborhood> members) { this.members = members; }

        public int getSize() { return size; }
        public void setSize(int size) { this.size = size; }

        public Neighborhood getCenterNeighborhood() { return centerNeighborhood; }
        public void setCenterNeighborhood(Neighborhood centerNeighborhood) { this.centerNeighborhood = centerNeighborhood; }
    }

    public static class SpatialStatistics {
        private String region;
        private int totalNeighborhoods;
        private BigDecimal totalAreaKm2;
        private BigDecimal averageAreaKm2;
        private BigDecimal minAreaKm2;
        private BigDecimal maxAreaKm2;
        private Long totalPopulation;
        private Long averagePopulation;
        private Long minPopulation;
        private Long maxPopulation;
        private BigDecimal averagePopulationDensity;
        private BigDecimal minPopulationDensity;
        private BigDecimal maxPopulationDensity;
        private Map<Neighborhood.UrbanizationLevel, Long> urbanizationDistribution;
        private Map<String, Long> languageDistribution;

        // Getters and setters
        public String getRegion() { return region; }
        public void setRegion(String region) { this.region = region; }

        public int getTotalNeighborhoods() { return totalNeighborhoods; }
        public void setTotalNeighborhoods(int totalNeighborhoods) { this.totalNeighborhoods = totalNeighborhoods; }

        public BigDecimal getTotalAreaKm2() { return totalAreaKm2; }
        public void setTotalAreaKm2(BigDecimal totalAreaKm2) { this.totalAreaKm2 = totalAreaKm2; }

        public BigDecimal getAverageAreaKm2() { return averageAreaKm2; }
        public void setAverageAreaKm2(BigDecimal averageAreaKm2) { this.averageAreaKm2 = averageAreaKm2; }

        public BigDecimal getMinAreaKm2() { return minAreaKm2; }
        public void setMinAreaKm2(BigDecimal minAreaKm2) { this.minAreaKm2 = minAreaKm2; }

        public BigDecimal getMaxAreaKm2() { return maxAreaKm2; }
        public void setMaxAreaKm2(BigDecimal maxAreaKm2) { this.maxAreaKm2 = maxAreaKm2; }

        public Long getTotalPopulation() { return totalPopulation; }
        public void setTotalPopulation(Long totalPopulation) { this.totalPopulation = totalPopulation; }

        public Long getAveragePopulation() { return averagePopulation; }
        public void setAveragePopulation(Long averagePopulation) { this.averagePopulation = averagePopulation; }

        public Long getMinPopulation() { return minPopulation; }
        public void setMinPopulation(Long minPopulation) { this.minPopulation = minPopulation; }

        public Long getMaxPopulation() { return maxPopulation; }
        public void setMaxPopulation(Long maxPopulation) { this.maxPopulation = maxPopulation; }

        public BigDecimal getAveragePopulationDensity() { return averagePopulationDensity; }
        public void setAveragePopulationDensity(BigDecimal averagePopulationDensity) { this.averagePopulationDensity = averagePopulationDensity; }

        public BigDecimal getMinPopulationDensity() { return minPopulationDensity; }
        public void setMinPopulationDensity(BigDecimal minPopulationDensity) { this.minPopulationDensity = minPopulationDensity; }

        public BigDecimal getMaxPopulationDensity() { return maxPopulationDensity; }
        public void setMaxPopulationDensity(BigDecimal maxPopulationDensity) { this.maxPopulationDensity = maxPopulationDensity; }

        public Map<Neighborhood.UrbanizationLevel, Long> getUrbanizationDistribution() { return urbanizationDistribution; }
        public void setUrbanizationDistribution(Map<Neighborhood.UrbanizationLevel, Long> urbanizationDistribution) { this.urbanizationDistribution = urbanizationDistribution; }

        public Map<String, Long> getLanguageDistribution() { return languageDistribution; }
        public void setLanguageDistribution(Map<String, Long> languageDistribution) { this.languageDistribution = languageDistribution; }
    }
}