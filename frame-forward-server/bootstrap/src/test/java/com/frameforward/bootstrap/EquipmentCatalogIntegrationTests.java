package com.frameforward.bootstrap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class EquipmentCatalogIntegrationTests {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    @Test void catalogIsAuthenticatedSeededFilterableAndCompatibilityAware() throws Exception {
        var suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        var result = mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(new Registration("catalog" + suffix, "catalog" + suffix + "@example.com", "ValidPass1!"))))
            .andExpect(status().isCreated()).andReturn();
        var accessToken = json.readTree(result.getResponse().getContentAsString()).get("accessToken").asText();
        var authorization = "Bearer " + accessToken;

        mvc.perform(get("/catalog/cameras")).andExpect(status().isUnauthorized());
        mvc.perform(get("/catalog/cameras").header("Authorization", authorization))
            .andExpect(status().isOk()).andExpect(jsonPath("$.version").value("p0-2026-01"))
            .andExpect(jsonPath("$.items.length()").value(8));
        mvc.perform(get("/catalog/lenses").param("brand", "Sigma").header("Authorization", authorization))
            .andExpect(status().isOk()).andExpect(jsonPath("$.items.length()").value(1))
            .andExpect(jsonPath("$.items[0].brand").value("Sigma"));
        mvc.perform(get("/catalog/accessories").header("Authorization", authorization))
            .andExpect(status().isOk()).andExpect(jsonPath("$.items.length()").value(7));
        mvc.perform(get("/catalog/compatibility").param("cameraId", "sony-a6700").param("lensId", "sigma-18-50-dc-dn").header("Authorization", authorization))
            .andExpect(status().isOk()).andExpect(jsonPath("$.compatible").value(true)).andExpect(jsonPath("$.cropModeRequired").value(false)).andExpect(jsonPath("$.mode").value("NATIVE"));
        mvc.perform(get("/catalog/compatibility").param("cameraId", "sony-a7-iv").param("lensId", "sigma-18-50-dc-dn").header("Authorization", authorization))
            .andExpect(status().isOk()).andExpect(jsonPath("$.compatible").value(true)).andExpect(jsonPath("$.cropModeRequired").value(true)).andExpect(jsonPath("$.mode").value("APS_C_CROP"));
        mvc.perform(get("/catalog/compatibility").param("cameraId", "nikon-z8").param("lensId", "canon-rf-24-70-l").header("Authorization", authorization))
            .andExpect(status().isOk()).andExpect(jsonPath("$.compatible").value(false)).andExpect(jsonPath("$.mode").value("CROSS_MOUNT"));
    }

    record Registration(String username, String email, String password) {}
}
