package com.frameforward.bootstrap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class UserEquipmentIntegrationTests {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper json;
    @Autowired
    JdbcTemplate jdbc;

    @Test
    void ownedEquipmentIsPrivateAndFirstCameraBecomesPrimary() throws Exception {
        var owner = register("equipmentowner");
        var other = register("equipmentother");
        var item = add(owner, "CAMERA", "sony-a6700");

        mvc.perform(get("/equipment").header("Authorization", owner)).andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1)).andExpect(jsonPath("$.items[0].primary").value(true));
        mvc.perform(delete("/equipment/{id}", item).header("Authorization", other)).andExpect(status().isNotFound());
        mvc.perform(get("/equipment").header("Authorization", other)).andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(0));
    }

    @Test
    void primarySwitchIsAtomicAndCombinationsExcludeCrossMountDefaults() throws Exception {
        var owner = register("equipmentprimary");
        var first = add(owner, "CAMERA", "sony-a6700");
        var second = add(owner, "CAMERA", "nikon-z8");
        add(owner, "LENS", "sigma-18-50-dc-dn");
        var canonLens = add(owner, "LENS", "canon-rf-24-70-l");

        mvc.perform(put("/equipment/cameras/{id}/primary", second).header("Authorization", owner))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(second))
                .andExpect(jsonPath("$.primary").value(true));
        Integer switched = jdbc.queryForObject(
                "SELECT COUNT(*) FROM user_equipment WHERE id IN (?, ?) AND is_primary = TRUE", Integer.class, first,
                second);
        org.assertj.core.api.Assertions.assertThat(switched).isEqualTo(1);
        mvc.perform(get("/equipment/body-lens-combinations").header("Authorization", owner)).andExpect(status().isOk())
                .andExpect(jsonPath("$.items[?(@.cameraEquipmentId == '" + second + "' && @.lensEquipmentId == '"
                        + canonLens + "')].compatible").value(false))
                .andExpect(jsonPath("$.items[?(@.cameraEquipmentId == '" + second + "' && @.lensEquipmentId == '"
                        + canonLens + "')].defaultEligible").value(false));
    }

    private String register(String prefix) throws Exception {
        var name = prefix + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        var response = mvc
                .perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(new Register(name, name + "@example.com", "ValidPass1!"))))
                .andExpect(status().isCreated()).andReturn();
        return "Bearer " + json.readTree(response.getResponse().getContentAsString()).get("accessToken").asText();
    }

    private String add(String authorization, String kind, String catalogItemId) throws Exception {
        var response = mvc
                .perform(post("/equipment").header("Authorization", authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(new Create(kind, catalogItemId, null))))
                .andExpect(status().isCreated()).andReturn();
        return json.readTree(response.getResponse().getContentAsString()).get("id").asText();
    }

    record Register(String username, String email, String password) {
    }
    record Create(String kind, String catalogItemId, String nickname) {
    }
}
