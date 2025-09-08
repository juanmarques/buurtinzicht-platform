package com.buurtinzicht.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.locationtech.jts.geom.Point;

/**
 * Entity representing a Belgian address with spatial information.
 * Contains both address components and geographic coordinates.
 */
@Entity
@Table(name = "addresses", indexes = {
    @Index(name = "idx_addresses_location", columnList = "location"),
    @Index(name = "idx_addresses_postal_code", columnList = "postalCode"),
    @Index(name = "idx_addresses_municipality", columnList = "municipality")
})
public class Address extends BaseEntity {
    
    @NotBlank
    @Size(max = 255)
    @Column(name = "street_name", nullable = false)
    private String streetName;
    
    @NotBlank
    @Size(max = 20)
    @Column(name = "house_number", nullable = false)
    private String houseNumber;
    
    @Size(max = 20)
    @Column(name = "box_number")
    private String boxNumber;
    
    @NotBlank
    @Size(max = 10)
    @Column(name = "postal_code", nullable = false)
    private String postalCode;
    
    @NotBlank
    @Size(max = 100)
    @Column(name = "municipality", nullable = false)
    private String municipality;
    
    @NotBlank
    @Size(max = 100)
    @Column(name = "province", nullable = false)
    private String province;
    
    @NotBlank
    @Size(max = 10)
    @Column(name = "country", nullable = false)
    private String country = "BE";
    
    @Column(name = "location", columnDefinition = "geometry(Point,4326)")
    private Point location;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "neighborhood_id")
    private Neighborhood neighborhood;
    
    // Constructors
    public Address() {
    }
    
    public Address(String streetName, String houseNumber, String postalCode, 
                   String municipality, String province) {
        this.streetName = streetName;
        this.houseNumber = houseNumber;
        this.postalCode = postalCode;
        this.municipality = municipality;
        this.province = province;
    }
    
    // Getters and Setters
    public String getStreetName() {
        return streetName;
    }
    
    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }
    
    public String getHouseNumber() {
        return houseNumber;
    }
    
    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }
    
    public String getBoxNumber() {
        return boxNumber;
    }
    
    public void setBoxNumber(String boxNumber) {
        this.boxNumber = boxNumber;
    }
    
    public String getPostalCode() {
        return postalCode;
    }
    
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    
    public String getMunicipality() {
        return municipality;
    }
    
    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }
    
    public String getProvince() {
        return province;
    }
    
    public void setProvince(String province) {
        this.province = province;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public Point getLocation() {
        return location;
    }
    
    public void setLocation(Point location) {
        this.location = location;
    }
    
    public Neighborhood getNeighborhood() {
        return neighborhood;
    }
    
    public void setNeighborhood(Neighborhood neighborhood) {
        this.neighborhood = neighborhood;
    }
    
    // Business Methods
    
    /**
     * Returns the full address as a formatted string.
     */
    public String getFullAddress() {
        StringBuilder address = new StringBuilder();
        address.append(streetName).append(" ").append(houseNumber);
        if (boxNumber != null && !boxNumber.trim().isEmpty()) {
            address.append("/").append(boxNumber);
        }
        address.append(", ").append(postalCode).append(" ").append(municipality);
        return address.toString();
    }
    
    /**
     * Returns the coordinates as an array [longitude, latitude].
     */
    public double[] getCoordinates() {
        if (location == null) {
            return null;
        }
        return new double[]{location.getX(), location.getY()};
    }
    
    /**
     * Returns the latitude from the location point.
     */
    public Double getLatitude() {
        return location != null ? location.getY() : null;
    }
    
    /**
     * Returns the longitude from the location point.
     */
    public Double getLongitude() {
        return location != null ? location.getX() : null;
    }
    
    @Override
    public String toString() {
        return "Address{" +
                "id=" + getId() +
                ", streetName='" + streetName + '\'' +
                ", houseNumber='" + houseNumber + '\'' +
                ", boxNumber='" + boxNumber + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", municipality='" + municipality + '\'' +
                ", province='" + province + '\'' +
                ", country='" + country + '\'' +
                ", coordinates=" + (location != null ? "[" + location.getX() + ", " + location.getY() + "]" : "null") +
                '}';
    }
}