package com.buurtinzicht.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;

import java.math.BigDecimal;

@Entity
@Table(name = "neighborhoods", indexes = {
    @Index(name = "idx_neighborhoods_nis_code", columnList = "nisCode", unique = true),
    @Index(name = "idx_neighborhoods_name", columnList = "name"),
    @Index(name = "idx_neighborhoods_municipality", columnList = "municipality"),
    @Index(name = "idx_neighborhoods_province", columnList = "province"),
    @Index(name = "idx_neighborhoods_region", columnList = "region"),
    @Index(name = "idx_neighborhoods_boundary", columnList = "boundary")
})
public class Neighborhood extends BaseEntity {

    @NotBlank
    @Size(max = 10)
    @Column(name = "nis_code", nullable = false, unique = true)
    private String nisCode;

    @NotBlank
    @Size(max = 255)
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 255)
    @Column(name = "name_nl")
    private String nameNl;

    @Size(max = 255)
    @Column(name = "name_fr")
    private String nameFr;

    @Size(max = 255)
    @Column(name = "name_de")
    private String nameDe;

    @NotBlank
    @Size(max = 255)
    @Column(name = "municipality", nullable = false)
    private String municipality;

    @Size(max = 255)
    @Column(name = "province")
    private String province;

    @Size(max = 255)
    @Column(name = "region")
    private String region;

    @NotNull
    @Column(name = "boundary", nullable = false, columnDefinition = "geometry(MultiPolygon,4326)")
    private MultiPolygon boundary;

    @Column(name = "centroid", columnDefinition = "geometry(Point,4326)")
    private Point centroid;

    @Column(name = "area_km2", precision = 10, scale = 4)
    private BigDecimal areaKm2;

    @Column(name = "perimeter_km", precision = 10, scale = 4)
    private BigDecimal perimeterKm;

    @Column(name = "population")
    private Long population;

    @Column(name = "population_density", precision = 10, scale = 2)
    private BigDecimal populationDensity;

    @Size(max = 5)
    @Column(name = "primary_language")
    private String primaryLanguage = "nl";

    @Column(name = "postal_codes")
    private String postalCodes; // Comma-separated list

    @Column(name = "elevation_min", precision = 8, scale = 2)
    private BigDecimal elevationMin;

    @Column(name = "elevation_max", precision = 8, scale = 2)
    private BigDecimal elevationMax;

    @Column(name = "elevation_avg", precision = 8, scale = 2)
    private BigDecimal elevationAvg;

    @Enumerated(EnumType.STRING)
    @Column(name = "urbanization_level")
    private UrbanizationLevel urbanizationLevel;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    public enum UrbanizationLevel {
        RURAL,          // Rural area
        SUBURBAN,       // Suburban area  
        URBAN,          // Urban area
        METROPOLITAN    // Metropolitan area
    }

    // Constructors
    public Neighborhood() {}

    public Neighborhood(String nisCode, String name, String municipality, MultiPolygon boundary) {
        this.nisCode = nisCode;
        this.name = name;
        this.municipality = municipality;
        this.boundary = boundary;
    }

    // Business methods
    public String getLocalizedName(String language) {
        if (language == null) language = "nl";
        
        return switch (language.toLowerCase()) {
            case "fr" -> nameFr != null ? nameFr : name;
            case "de" -> nameDe != null ? nameDe : name;
            default -> nameNl != null ? nameNl : name;
        };
    }

    public String[] getPostalCodeArray() {
        if (postalCodes == null || postalCodes.trim().isEmpty()) {
            return new String[0];
        }
        return postalCodes.split(",");
    }

    public void setPostalCodeArray(String[] codes) {
        if (codes == null || codes.length == 0) {
            this.postalCodes = null;
        } else {
            this.postalCodes = String.join(",", codes);
        }
    }

    public boolean isInPostalCode(String postalCode) {
        if (postalCodes == null || postalCode == null) {
            return false;
        }
        return postalCodes.contains(postalCode);
    }

    public boolean hasPopulationData() {
        return population != null && population > 0;
    }

    public boolean hasElevationData() {
        return elevationMin != null || elevationMax != null || elevationAvg != null;
    }

    // Getters and setters
    public String getNisCode() { return nisCode; }
    public void setNisCode(String nisCode) { this.nisCode = nisCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNameNl() { return nameNl; }
    public void setNameNl(String nameNl) { this.nameNl = nameNl; }

    public String getNameFr() { return nameFr; }
    public void setNameFr(String nameFr) { this.nameFr = nameFr; }

    public String getNameDe() { return nameDe; }
    public void setNameDe(String nameDe) { this.nameDe = nameDe; }

    public String getMunicipality() { return municipality; }
    public void setMunicipality(String municipality) { this.municipality = municipality; }

    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public MultiPolygon getBoundary() { return boundary; }
    public void setBoundary(MultiPolygon boundary) { this.boundary = boundary; }

    public Point getCentroid() { return centroid; }
    public void setCentroid(Point centroid) { this.centroid = centroid; }

    public BigDecimal getAreaKm2() { return areaKm2; }
    public void setAreaKm2(BigDecimal areaKm2) { this.areaKm2 = areaKm2; }

    public BigDecimal getPerimeterKm() { return perimeterKm; }
    public void setPerimeterKm(BigDecimal perimeterKm) { this.perimeterKm = perimeterKm; }

    public Long getPopulation() { return population; }
    public void setPopulation(Long population) { this.population = population; }

    public BigDecimal getPopulationDensity() { return populationDensity; }
    public void setPopulationDensity(BigDecimal populationDensity) { this.populationDensity = populationDensity; }

    public String getPrimaryLanguage() { return primaryLanguage; }
    public void setPrimaryLanguage(String primaryLanguage) { this.primaryLanguage = primaryLanguage; }

    public String getPostalCodes() { return postalCodes; }
    public void setPostalCodes(String postalCodes) { this.postalCodes = postalCodes; }

    public BigDecimal getElevationMin() { return elevationMin; }
    public void setElevationMin(BigDecimal elevationMin) { this.elevationMin = elevationMin; }

    public BigDecimal getElevationMax() { return elevationMax; }
    public void setElevationMax(BigDecimal elevationMax) { this.elevationMax = elevationMax; }

    public BigDecimal getElevationAvg() { return elevationAvg; }
    public void setElevationAvg(BigDecimal elevationAvg) { this.elevationAvg = elevationAvg; }

    public UrbanizationLevel getUrbanizationLevel() { return urbanizationLevel; }
    public void setUrbanizationLevel(UrbanizationLevel urbanizationLevel) { this.urbanizationLevel = urbanizationLevel; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    @Override
    public String toString() {
        return "Neighborhood{" +
                "id=" + getId() +
                ", nisCode='" + nisCode + '\'' +
                ", name='" + name + '\'' +
                ", municipality='" + municipality + '\'' +
                ", province='" + province + '\'' +
                ", region='" + region + '\'' +
                ", population=" + population +
                ", areaKm2=" + areaKm2 +
                ", primaryLanguage='" + primaryLanguage + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}