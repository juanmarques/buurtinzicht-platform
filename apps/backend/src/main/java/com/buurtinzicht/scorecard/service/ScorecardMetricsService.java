package com.buurtinzicht.scorecard.service;

import com.buurtinzicht.domain.model.Neighborhood;
import com.buurtinzicht.external.service.BelgianGovernmentDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Service for calculating individual scorecard metrics.
 * Each method represents a specific quality of life indicator.
 */
@Service
public class ScorecardMetricsService {

    private static final Logger logger = LoggerFactory.getLogger(ScorecardMetricsService.class);

    private final BelgianGovernmentDataService governmentDataService;

    @Autowired
    public ScorecardMetricsService(BelgianGovernmentDataService governmentDataService) {
        this.governmentDataService = governmentDataService;
    }

    /**
     * Calculate all individual metric scores for a neighborhood.
     */
    @Cacheable(value = "metricScores", key = "#neighborhood.nisCode")
    public Map<String, BigDecimal> calculateAllMetrics(Neighborhood neighborhood) {
        logger.info("Calculating all metrics for neighborhood: {}", neighborhood.getName());
        
        long startTime = System.currentTimeMillis();
        Map<String, BigDecimal> metrics = new HashMap<>();

        // Infrastructure Metrics
        metrics.put("transportation", calculateTransportationScore(neighborhood));
        metrics.put("publicServices", calculatePublicServicesScore(neighborhood));
        metrics.put("connectivity", calculateConnectivityScore(neighborhood));

        // Economic Metrics  
        metrics.put("costOfLiving", calculateCostOfLivingScore(neighborhood));
        metrics.put("employment", calculateEmploymentScore(neighborhood));
        metrics.put("housingMarket", calculateHousingMarketScore(neighborhood));

        // Social & Cultural Metrics
        metrics.put("education", calculateEducationScore(neighborhood));
        metrics.put("healthcare", calculateHealthcareScore(neighborhood));
        metrics.put("culturalAmenities", calculateCulturalAmenitiesScore(neighborhood));
        metrics.put("communityEngagement", calculateCommunityEngagementScore(neighborhood));

        // Environmental Metrics
        metrics.put("airQuality", calculateAirQualityScore(neighborhood));
        metrics.put("greenSpaces", calculateGreenSpacesScore(neighborhood));
        metrics.put("noisePollution", calculateNoisePollutionScore(neighborhood));

        // Safety & Security Metrics
        metrics.put("crimeSafety", calculateCrimeSafetyScore(neighborhood));
        metrics.put("emergencyServices", calculateEmergencyServicesScore(neighborhood));

        long executionTime = System.currentTimeMillis() - startTime;
        logger.info("Calculated {} metrics for {} in {}ms", 
            metrics.size(), neighborhood.getName(), executionTime);

        return metrics;
    }

    // Infrastructure Metrics

    public BigDecimal calculateTransportationScore(Neighborhood neighborhood) {
        logger.debug("Calculating transportation score for {}", neighborhood.getName());
        
        try {
            // Base score from urbanization level
            BigDecimal baseScore = getUrbanizationTransportScore(neighborhood);
            
            // Adjust based on population density (more density = better transit typically)
            BigDecimal densityAdjustment = calculateDensityTransportAdjustment(neighborhood);
            
            // Regional adjustments for Belgium
            BigDecimal regionalAdjustment = calculateRegionalTransportAdjustment(neighborhood);
            
            BigDecimal score = baseScore.add(densityAdjustment).add(regionalAdjustment);
            
            return constrainScore(score);
            
        } catch (Exception e) {
            logger.error("Error calculating transportation score for {}: {}", 
                neighborhood.getName(), e.getMessage());
            return getDefaultScore(75.0); // Default to reasonable score
        }
    }

    public BigDecimal calculatePublicServicesScore(Neighborhood neighborhood) {
        logger.debug("Calculating public services score for {}", neighborhood.getName());
        
        try {
            BigDecimal score = BigDecimal.valueOf(70.0); // Base score
            
            // Government administrative regions typically have better services
            if ("Brussels-Capital Region".equals(neighborhood.getRegion())) {
                score = score.add(BigDecimal.valueOf(15.0));
            } else if (neighborhood.getRegion() != null && 
                      (neighborhood.getRegion().contains("Flanders") || 
                       neighborhood.getRegion().contains("Wallonia"))) {
                score = score.add(BigDecimal.valueOf(8.0));
            }
            
            // Urban areas typically have better service access
            if (neighborhood.getUrbanizationLevel() != null) {
                switch (neighborhood.getUrbanizationLevel()) {
                    case METROPOLITAN -> score = score.add(BigDecimal.valueOf(12.0));
                    case URBAN -> score = score.add(BigDecimal.valueOf(8.0));
                    case SUBURBAN -> score = score.add(BigDecimal.valueOf(4.0));
                    case RURAL -> score = score.subtract(BigDecimal.valueOf(5.0));
                }
            }
            
            return constrainScore(score);
            
        } catch (Exception e) {
            logger.error("Error calculating public services score: {}", e.getMessage());
            return getDefaultScore(72.0);
        }
    }

    public BigDecimal calculateConnectivityScore(Neighborhood neighborhood) {
        logger.debug("Calculating connectivity score for {}", neighborhood.getName());
        
        try {
            BigDecimal score = BigDecimal.valueOf(80.0); // Belgium has good connectivity
            
            // Major cities have excellent connectivity
            String name = neighborhood.getName().toLowerCase();
            if (name.contains("brussels") || name.contains("antwerp") || 
                name.contains("ghent") || name.contains("liège")) {
                score = score.add(BigDecimal.valueOf(15.0));
            }
            
            // Population density affects connectivity quality
            BigDecimal densityScore = calculateConnectivityDensityBonus(neighborhood);
            score = score.add(densityScore);
            
            return constrainScore(score);
            
        } catch (Exception e) {
            logger.error("Error calculating connectivity score: {}", e.getMessage());
            return getDefaultScore(82.0);
        }
    }

    // Economic Metrics

    public BigDecimal calculateCostOfLivingScore(Neighborhood neighborhood) {
        logger.debug("Calculating cost of living score for {}", neighborhood.getName());
        
        try {
            BigDecimal score = BigDecimal.valueOf(65.0); // Base score (lower is more expensive)
            
            // Brussels is most expensive
            if (neighborhood.getName().toLowerCase().contains("brussels")) {
                score = score.subtract(BigDecimal.valueOf(15.0));
            }
            
            // Major cities are more expensive
            String name = neighborhood.getName().toLowerCase();
            if (name.contains("antwerp") || name.contains("ghent") || name.contains("liège")) {
                score = score.subtract(BigDecimal.valueOf(10.0));
            }
            
            // Rural areas are typically cheaper
            if (neighborhood.getUrbanizationLevel() == Neighborhood.UrbanizationLevel.RURAL) {
                score = score.add(BigDecimal.valueOf(12.0));
            }
            
            return constrainScore(score);
            
        } catch (Exception e) {
            logger.error("Error calculating cost of living score: {}", e.getMessage());
            return getDefaultScore(68.0);
        }
    }

    public BigDecimal calculateEmploymentScore(Neighborhood neighborhood) {
        logger.debug("Calculating employment score for {}", neighborhood.getName());
        
        try {
            BigDecimal score = BigDecimal.valueOf(72.0); // Belgian average
            
            // Economic centers have better employment
            if ("Brussels-Capital Region".equals(neighborhood.getRegion())) {
                score = score.add(BigDecimal.valueOf(18.0));
            }
            
            // Port cities like Antwerp have strong employment
            if (neighborhood.getName().toLowerCase().contains("antwerp")) {
                score = score.add(BigDecimal.valueOf(12.0));
            }
            
            // Industrial regions in Flanders typically have good employment
            if (neighborhood.getRegion() != null && 
                neighborhood.getRegion().contains("Flanders")) {
                score = score.add(BigDecimal.valueOf(8.0));
            }
            
            return constrainScore(score);
            
        } catch (Exception e) {
            logger.error("Error calculating employment score: {}", e.getMessage());
            return getDefaultScore(74.0);
        }
    }

    public BigDecimal calculateHousingMarketScore(Neighborhood neighborhood) {
        logger.debug("Calculating housing market score for {}", neighborhood.getName());
        
        try {
            BigDecimal score = BigDecimal.valueOf(60.0); // Base score
            
            // Balanced market is better (not too expensive, not declining)
            if (neighborhood.getUrbanizationLevel() == Neighborhood.UrbanizationLevel.SUBURBAN) {
                score = score.add(BigDecimal.valueOf(15.0));
            } else if (neighborhood.getUrbanizationLevel() == Neighborhood.UrbanizationLevel.URBAN) {
                score = score.add(BigDecimal.valueOf(10.0));
            }
            
            // Add some variation based on regional characteristics
            if (neighborhood.getRegion() != null) {
                if (neighborhood.getRegion().contains("Brussels")) {
                    score = score.subtract(BigDecimal.valueOf(5.0)); // High demand, expensive
                } else if (neighborhood.getRegion().contains("Flanders")) {
                    score = score.add(BigDecimal.valueOf(8.0)); // Generally stable
                }
            }
            
            return constrainScore(score);
            
        } catch (Exception e) {
            logger.error("Error calculating housing market score: {}", e.getMessage());
            return getDefaultScore(65.0);
        }
    }

    // Social & Cultural Metrics

    public BigDecimal calculateEducationScore(Neighborhood neighborhood) {
        logger.debug("Calculating education score for {}", neighborhood.getName());
        
        try {
            BigDecimal score = BigDecimal.valueOf(78.0); // Belgium has good education system
            
            // University cities get bonus
            String name = neighborhood.getName().toLowerCase();
            if (name.contains("leuven") || name.contains("ghent") || 
                name.contains("brussels") || name.contains("liège")) {
                score = score.add(BigDecimal.valueOf(12.0));
            }
            
            // Urban areas typically have more educational options
            if (neighborhood.getUrbanizationLevel() != null) {
                switch (neighborhood.getUrbanizationLevel()) {
                    case METROPOLITAN -> score = score.add(BigDecimal.valueOf(10.0));
                    case URBAN -> score = score.add(BigDecimal.valueOf(6.0));
                    case SUBURBAN -> score = score.add(BigDecimal.valueOf(3.0));
                    case RURAL -> score = score.subtract(BigDecimal.valueOf(4.0));
                }
            }
            
            return constrainScore(score);
            
        } catch (Exception e) {
            logger.error("Error calculating education score: {}", e.getMessage());
            return getDefaultScore(80.0);
        }
    }

    public BigDecimal calculateHealthcareScore(Neighborhood neighborhood) {
        logger.debug("Calculating healthcare score for {}", neighborhood.getName());
        
        try {
            BigDecimal score = BigDecimal.valueOf(85.0); // Belgium has excellent healthcare
            
            // Major cities have more specialized healthcare
            String name = neighborhood.getName().toLowerCase();
            if (name.contains("brussels") || name.contains("antwerp") || name.contains("liège")) {
                score = score.add(BigDecimal.valueOf(8.0));
            }
            
            // Rural areas may have less access
            if (neighborhood.getUrbanizationLevel() == Neighborhood.UrbanizationLevel.RURAL) {
                score = score.subtract(BigDecimal.valueOf(8.0));
            }
            
            return constrainScore(score);
            
        } catch (Exception e) {
            logger.error("Error calculating healthcare score: {}", e.getMessage());
            return getDefaultScore(87.0);
        }
    }

    public BigDecimal calculateCulturalAmenitiesScore(Neighborhood neighborhood) {
        logger.debug("Calculating cultural amenities score for {}", neighborhood.getName());
        
        try {
            BigDecimal score = BigDecimal.valueOf(60.0); // Base score
            
            // Historic cities have rich cultural amenities
            String name = neighborhood.getName().toLowerCase();
            if (name.contains("brussels") || name.contains("bruges") || 
                name.contains("ghent") || name.contains("antwerp")) {
                score = score.add(BigDecimal.valueOf(25.0));
            }
            
            // Other urban centers also have good amenities
            if (neighborhood.getUrbanizationLevel() != null) {
                switch (neighborhood.getUrbanizationLevel()) {
                    case METROPOLITAN -> score = score.add(BigDecimal.valueOf(15.0));
                    case URBAN -> score = score.add(BigDecimal.valueOf(10.0));
                    case SUBURBAN -> score = score.add(BigDecimal.valueOf(5.0));
                    case RURAL -> score = score.subtract(BigDecimal.valueOf(10.0));
                }
            }
            
            return constrainScore(score);
            
        } catch (Exception e) {
            logger.error("Error calculating cultural amenities score: {}", e.getMessage());
            return getDefaultScore(68.0);
        }
    }

    public BigDecimal calculateCommunityEngagementScore(Neighborhood neighborhood) {
        logger.debug("Calculating community engagement score for {}", neighborhood.getName());
        
        try {
            BigDecimal score = BigDecimal.valueOf(72.0); // Base score
            
            // Smaller communities often have higher engagement
            if (neighborhood.getUrbanizationLevel() == Neighborhood.UrbanizationLevel.SUBURBAN ||
                neighborhood.getUrbanizationLevel() == Neighborhood.UrbanizationLevel.RURAL) {
                score = score.add(BigDecimal.valueOf(8.0));
            }
            
            // Add some regional variation
            if (neighborhood.getRegion() != null && neighborhood.getRegion().contains("Flanders")) {
                score = score.add(BigDecimal.valueOf(5.0)); // Strong community traditions
            }
            
            return constrainScore(score);
            
        } catch (Exception e) {
            logger.error("Error calculating community engagement score: {}", e.getMessage());
            return getDefaultScore(74.0);
        }
    }

    // Environmental Metrics

    public BigDecimal calculateAirQualityScore(Neighborhood neighborhood) {
        logger.debug("Calculating air quality score for {}", neighborhood.getName());
        
        try {
            BigDecimal score = BigDecimal.valueOf(75.0); // Belgium average
            
            // Industrial areas and major cities have lower air quality
            String name = neighborhood.getName().toLowerCase();
            if (name.contains("antwerp") || name.contains("charleroi")) {
                score = score.subtract(BigDecimal.valueOf(12.0)); // Industrial ports/cities
            } else if (name.contains("brussels")) {
                score = score.subtract(BigDecimal.valueOf(8.0)); // Traffic pollution
            }
            
            // Rural areas have better air quality
            if (neighborhood.getUrbanizationLevel() == Neighborhood.UrbanizationLevel.RURAL) {
                score = score.add(BigDecimal.valueOf(15.0));
            }
            
            return constrainScore(score);
            
        } catch (Exception e) {
            logger.error("Error calculating air quality score: {}", e.getMessage());
            return getDefaultScore(73.0);
        }
    }

    public BigDecimal calculateGreenSpacesScore(Neighborhood neighborhood) {
        logger.debug("Calculating green spaces score for {}", neighborhood.getName());
        
        try {
            BigDecimal score = BigDecimal.valueOf(65.0); // Base score
            
            // Brussels has good parks for a capital
            if (neighborhood.getName().toLowerCase().contains("brussels")) {
                score = score.add(BigDecimal.valueOf(15.0));
            }
            
            // Suburban and rural areas typically have more green space
            if (neighborhood.getUrbanizationLevel() != null) {
                switch (neighborhood.getUrbanizationLevel()) {
                    case RURAL -> score = score.add(BigDecimal.valueOf(25.0));
                    case SUBURBAN -> score = score.add(BigDecimal.valueOf(12.0));
                    case URBAN -> score = score.add(BigDecimal.valueOf(5.0));
                    case METROPOLITAN -> score = score.subtract(BigDecimal.valueOf(5.0));
                }
            }
            
            return constrainScore(score);
            
        } catch (Exception e) {
            logger.error("Error calculating green spaces score: {}", e.getMessage());
            return getDefaultScore(68.0);
        }
    }

    public BigDecimal calculateNoisePollutionScore(Neighborhood neighborhood) {
        logger.debug("Calculating noise pollution score for {}", neighborhood.getName());
        
        try {
            BigDecimal score = BigDecimal.valueOf(70.0); // Base score (higher = less noise)
            
            // Major cities and transport hubs have more noise
            String name = neighborhood.getName().toLowerCase();
            if (name.contains("brussels") || name.contains("antwerp")) {
                score = score.subtract(BigDecimal.valueOf(15.0));
            }
            
            // Urban density affects noise levels
            if (neighborhood.getUrbanizationLevel() != null) {
                switch (neighborhood.getUrbanizationLevel()) {
                    case RURAL -> score = score.add(BigDecimal.valueOf(20.0));
                    case SUBURBAN -> score = score.add(BigDecimal.valueOf(10.0));
                    case URBAN -> score = score.subtract(BigDecimal.valueOf(8.0));
                    case METROPOLITAN -> score = score.subtract(BigDecimal.valueOf(15.0));
                }
            }
            
            return constrainScore(score);
            
        } catch (Exception e) {
            logger.error("Error calculating noise pollution score: {}", e.getMessage());
            return getDefaultScore(72.0);
        }
    }

    // Safety & Security Metrics

    public BigDecimal calculateCrimeSafetyScore(Neighborhood neighborhood) {
        logger.debug("Calculating crime safety score for {}", neighborhood.getName());
        
        try {
            BigDecimal score = BigDecimal.valueOf(80.0); // Belgium is generally safe
            
            // Some urban areas may have higher crime rates
            String name = neighborhood.getName().toLowerCase();
            if (name.contains("brussels")) {
                score = score.subtract(BigDecimal.valueOf(8.0)); // Capital city challenges
            } else if (name.contains("charleroi")) {
                score = score.subtract(BigDecimal.valueOf(10.0)); // Economic challenges
            }
            
            // Suburban areas are typically safer
            if (neighborhood.getUrbanizationLevel() == Neighborhood.UrbanizationLevel.SUBURBAN ||
                neighborhood.getUrbanizationLevel() == Neighborhood.UrbanizationLevel.RURAL) {
                score = score.add(BigDecimal.valueOf(8.0));
            }
            
            return constrainScore(score);
            
        } catch (Exception e) {
            logger.error("Error calculating crime safety score: {}", e.getMessage());
            return getDefaultScore(82.0);
        }
    }

    public BigDecimal calculateEmergencyServicesScore(Neighborhood neighborhood) {
        logger.debug("Calculating emergency services score for {}", neighborhood.getName());
        
        try {
            BigDecimal score = BigDecimal.valueOf(88.0); // Belgium has excellent emergency services
            
            // Urban areas have faster response times
            if (neighborhood.getUrbanizationLevel() != null) {
                switch (neighborhood.getUrbanizationLevel()) {
                    case METROPOLITAN -> score = score.add(BigDecimal.valueOf(7.0));
                    case URBAN -> score = score.add(BigDecimal.valueOf(4.0));
                    case SUBURBAN -> score = score.add(BigDecimal.valueOf(1.0));
                    case RURAL -> score = score.subtract(BigDecimal.valueOf(6.0));
                }
            }
            
            return constrainScore(score);
            
        } catch (Exception e) {
            logger.error("Error calculating emergency services score: {}", e.getMessage());
            return getDefaultScore(90.0);
        }
    }

    // Helper Methods

    private BigDecimal getUrbanizationTransportScore(Neighborhood neighborhood) {
        if (neighborhood.getUrbanizationLevel() == null) {
            return BigDecimal.valueOf(70.0);
        }
        
        return switch (neighborhood.getUrbanizationLevel()) {
            case METROPOLITAN -> BigDecimal.valueOf(85.0);
            case URBAN -> BigDecimal.valueOf(75.0);
            case SUBURBAN -> BigDecimal.valueOf(65.0);
            case RURAL -> BigDecimal.valueOf(45.0);
        };
    }

    private BigDecimal calculateDensityTransportAdjustment(Neighborhood neighborhood) {
        if (neighborhood.getPopulationDensity() == null) {
            return BigDecimal.ZERO;
        }
        
        // Higher density usually means better public transport
        BigDecimal density = neighborhood.getPopulationDensity();
        if (density.compareTo(BigDecimal.valueOf(2000)) > 0) {
            return BigDecimal.valueOf(8.0);
        } else if (density.compareTo(BigDecimal.valueOf(1000)) > 0) {
            return BigDecimal.valueOf(4.0);
        } else if (density.compareTo(BigDecimal.valueOf(500)) < 0) {
            return BigDecimal.valueOf(-5.0);
        }
        
        return BigDecimal.ZERO;
    }

    private BigDecimal calculateRegionalTransportAdjustment(Neighborhood neighborhood) {
        if (neighborhood.getRegion() == null) {
            return BigDecimal.ZERO;
        }
        
        // Brussels-Capital Region has excellent public transport
        if ("Brussels-Capital Region".equals(neighborhood.getRegion())) {
            return BigDecimal.valueOf(10.0);
        }
        
        return BigDecimal.ZERO;
    }

    private BigDecimal calculateConnectivityDensityBonus(Neighborhood neighborhood) {
        if (neighborhood.getPopulationDensity() == null) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal density = neighborhood.getPopulationDensity();
        if (density.compareTo(BigDecimal.valueOf(1500)) > 0) {
            return BigDecimal.valueOf(5.0);
        } else if (density.compareTo(BigDecimal.valueOf(500)) > 0) {
            return BigDecimal.valueOf(2.0);
        }
        
        return BigDecimal.ZERO;
    }

    private BigDecimal constrainScore(BigDecimal score) {
        if (score.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }
        if (score.compareTo(BigDecimal.valueOf(100.0)) > 0) {
            return BigDecimal.valueOf(100.0);
        }
        return score.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal getDefaultScore(double defaultValue) {
        return BigDecimal.valueOf(defaultValue).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Add some realistic variation to scores to simulate real data differences.
     */
    private BigDecimal addRealisticVariation(BigDecimal baseScore, double variationPercent) {
        double variation = ThreadLocalRandom.current().nextGaussian() * variationPercent;
        BigDecimal adjustedScore = baseScore.add(BigDecimal.valueOf(variation));
        return constrainScore(adjustedScore);
    }
}