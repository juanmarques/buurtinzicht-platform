package com.buurtinzicht.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Request object for updating user profile")
public class UserProfileUpdateRequest {

    @Schema(description = "User's first name", example = "John")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;

    @Schema(description = "User's last name", example = "Doe")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;

    @Schema(
        description = "User's preferred language", 
        example = "nl", 
        allowableValues = {"nl", "fr", "en", "de"}
    )
    @NotBlank(message = "Preferred language is required")
    @Pattern(
        regexp = "^(nl|fr|en|de)$", 
        message = "Preferred language must be one of: nl, fr, en, de"
    )
    @Size(max = 5, message = "Language code must not exceed 5 characters")
    private String preferredLanguage;

    public UserProfileUpdateRequest() {}

    public UserProfileUpdateRequest(String firstName, String lastName, String preferredLanguage) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.preferredLanguage = preferredLanguage;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    @Override
    public String toString() {
        return "UserProfileUpdateRequest{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", preferredLanguage='" + preferredLanguage + '\'' +
                '}';
    }
}