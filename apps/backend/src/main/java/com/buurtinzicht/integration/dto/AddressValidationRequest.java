package com.buurtinzicht.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Address validation request for Belgian addresses")
public class AddressValidationRequest {

    @Schema(description = "Street name", example = "Koning Albert II-laan")
    @Size(max = 100, message = "Street name must not exceed 100 characters")
    private String street;

    @Schema(description = "House number", example = "35")
    @Size(max = 10, message = "House number must not exceed 10 characters")
    private String houseNumber;

    @Schema(description = "Box number (optional)", example = "1")
    @Size(max = 10, message = "Box number must not exceed 10 characters")
    private String boxNumber;

    @Schema(description = "Postal code", example = "1000")
    @NotBlank(message = "Postal code is required")
    @Pattern(regexp = "^[0-9]{4}$", message = "Postal code must be 4 digits")
    private String postalCode;

    @Schema(description = "City name", example = "Brussels")
    @NotBlank(message = "City name is required")
    @Size(max = 100, message = "City name must not exceed 100 characters")
    private String city;

    @Schema(description = "Country code", example = "BE", allowableValues = {"BE"})
    @Pattern(regexp = "^BE$", message = "Only Belgian addresses are supported")
    private String country = "BE";

    public AddressValidationRequest() {}

    public AddressValidationRequest(String street, String houseNumber, String postalCode, String city) {
        this.street = street;
        this.houseNumber = houseNumber;
        this.postalCode = postalCode;
        this.city = city;
    }

    public String getFullAddressString() {
        StringBuilder sb = new StringBuilder();
        if (street != null) {
            sb.append(street);
        }
        if (houseNumber != null) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(houseNumber);
        }
        if (boxNumber != null) {
            sb.append("/").append(boxNumber);
        }
        if (postalCode != null || city != null) {
            if (sb.length() > 0) sb.append(", ");
            if (postalCode != null) {
                sb.append(postalCode);
            }
            if (city != null) {
                if (postalCode != null) sb.append(" ");
                sb.append(city);
            }
        }
        return sb.toString();
    }

    // Getters and setters
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getHouseNumber() { return houseNumber; }
    public void setHouseNumber(String houseNumber) { this.houseNumber = houseNumber; }

    public String getBoxNumber() { return boxNumber; }
    public void setBoxNumber(String boxNumber) { this.boxNumber = boxNumber; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    @Override
    public String toString() {
        return "AddressValidationRequest{" +
                "street='" + street + '\'' +
                ", houseNumber='" + houseNumber + '\'' +
                ", boxNumber='" + boxNumber + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}