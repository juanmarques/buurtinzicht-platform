package com.buurtinzicht.web.controller;

import com.buurtinzicht.BuurtinzichtBackendApplication;
import com.buurtinzicht.spatial.dto.SpatialQueryRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = BuurtinzichtBackendApplication.class)
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class SpatialControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "B2C_USER")
    void findNeighborhoods_ShouldReturnResults() throws Exception {
        SpatialQueryRequest request = new SpatialQueryRequest();
        request.setLatitude(BigDecimal.valueOf(50.8505));
        request.setLongitude(BigDecimal.valueOf(4.3488));
        request.setRadiusKm(BigDecimal.valueOf(10.0));
        request.setLimit(5);

        mockMvc.perform(post("/api/spatial/neighborhoods/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.neighborhoods").exists())
                .andExpect(jsonPath("$.searchCenter.latitude").value(50.8505))
                .andExpect(jsonPath("$.searchCenter.longitude").value(4.3488))
                .andExpect(jsonPath("$.searchRadiusKm").value(10.0));
    }

    @Test
    @WithMockUser(roles = "B2C_USER")
    void findNeighborhoodByPoint_ShouldReturnNeighborhood() throws Exception {
        mockMvc.perform(get("/api/spatial/neighborhoods/at-point")
                .param("latitude", "50.8505")
                .param("longitude", "4.3488")
                .param("language", "nl"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.nisCode").exists());
    }

    @Test
    @WithMockUser(roles = "B2C_USER")
    void getNeighborhoodDetails_ShouldReturnDetails() throws Exception {
        mockMvc.perform(get("/api/spatial/neighborhoods/21004")
                .param("language", "fr"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nisCode").value("21004"))
                .andExpect(jsonPath("$.localizedName").exists());
    }

    @Test
    @WithMockUser(roles = "B2C_USER")
    void searchNeighborhoodsByName_ShouldReturnResults() throws Exception {
        mockMvc.perform(get("/api/spatial/neighborhoods/search")
                .param("q", "Brussels")
                .param("limit", "5")
                .param("language", "en"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "B2C_USER")
    void calculateDistance_ShouldReturnDistance() throws Exception {
        mockMvc.perform(get("/api/spatial/neighborhoods/distance")
                .param("from", "21004")
                .param("to", "11002"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNumber());
    }

    @Test
    @WithMockUser(roles = "B2B_USER")
    void compareNeighborhoods_ShouldReturnComparison() throws Exception {
        mockMvc.perform(get("/api/spatial/neighborhoods/compare")
                .param("nis1", "21004")
                .param("nis2", "11002"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.neighborhood1").exists())
                .andExpect(jsonPath("$.neighborhood2").exists())
                .andExpect(jsonPath("$.distance").exists());
    }

    @Test
    @WithMockUser(roles = "B2B_USER")
    void getSpatialStatistics_ShouldReturnStats() throws Exception {
        mockMvc.perform(get("/api/spatial/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalNeighborhoods").exists())
                .andExpect(jsonPath("$.averageArea").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllMunicipalities_ShouldReturnList() throws Exception {
        mockMvc.perform(get("/api/spatial/municipalities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void findNeighborhoods_ShouldRequireAuthentication() throws Exception {
        SpatialQueryRequest request = new SpatialQueryRequest();
        request.setLatitude(BigDecimal.valueOf(50.8505));
        request.setLongitude(BigDecimal.valueOf(4.3488));
        request.setRadiusKm(BigDecimal.valueOf(10.0));

        mockMvc.perform(post("/api/spatial/neighborhoods/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}