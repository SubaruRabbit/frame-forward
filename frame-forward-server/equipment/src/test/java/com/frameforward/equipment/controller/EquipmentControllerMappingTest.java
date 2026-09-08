package com.frameforward.equipment.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.frameforward.auth.service.AuthService;
import com.frameforward.equipment.business.*;
import com.frameforward.equipment.model.dto.*;
import com.frameforward.equipment.service.*;

class EquipmentControllerMappingTest {
    private final AuthService auth = mock(AuthService.class);
    private final CatalogService catalog = mock(CatalogService.class);
    private final UserEquipmentService equipment = mock(UserEquipmentService.class);
    private final MockMvc mvc;

    EquipmentControllerMappingTest() throws ClassNotFoundException {
        mvc = MockMvcBuilders
                .standaloneSetup(new CatalogController(auth, catalog), new UserEquipmentController(equipment, auth))
                .setControllerAdvice(new CatalogExceptionHandler(), new UserEquipmentExceptionHandler(),
                        Class.forName("com.frameforward.auth.controller.AuthExceptionHandler"))
                .build();
    }

    @Test
    void catalogRoutesKeepVersionAndCompatibilityShape() throws Exception {
        when(auth.requireAccountId("token")).thenReturn("account");
        when(catalog.cameras()).thenReturn(List.of(new Camera("camera", "Sony", "model", "E", "FULL_FRAME")));
        when(catalog.lenses("Sony")).thenReturn(List.of());
        when(catalog.accessories()).thenReturn(List.of(new AccessoryType("tripod", "三脚架")));
        when(catalog.compatibility("camera", "lens")).thenReturn(new Compatibility("camera", "lens", true, false, "NATIVE"));
        mvc.perform(get("/catalog/cameras").header("Authorization", "Bearer token"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.version").value(CatalogService.VERSION))
                .andExpect(jsonPath("$.items[0].id").value("camera"));
        mvc.perform(get("/catalog/lenses").param("brand", "Sony").header("Authorization", "Bearer token"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.items").isArray());
        mvc.perform(get("/catalog/accessories").header("Authorization", "Bearer token"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.items[0].id").value("tripod"));
        mvc.perform(get("/catalog/compatibility").param("cameraId", "camera").param("lensId", "lens")
                .header("Authorization", "Bearer token")).andExpect(status().isOk()).andExpect(jsonPath("$.mode").value("NATIVE"));
    }

    @Test
    void equipmentRoutesKeepPayloadsAndStatusCodes() throws Exception {
        when(auth.requireAccountId("token")).thenReturn("account");
        var item = new UserEquipmentItem("id", "CAMERA", "camera", "主机", true);
        when(equipment.list("account")).thenReturn(List.of(item));
        when(equipment.add("account", "CAMERA", "camera", "主机")).thenReturn(item);
        when(equipment.setPrimaryCamera("account", "id")).thenReturn(item);
        when(equipment.combinations("account")).thenReturn(List.of(new BodyLensCombination("id", "lens", true, false, "NATIVE", true)));
        mvc.perform(get("/equipment").header("Authorization", "Bearer token")).andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].primary").value(true));
        mvc.perform(post("/equipment").header("Authorization", "Bearer token").contentType(MediaType.APPLICATION_JSON)
                .content("{\"kind\":\"CAMERA\",\"catalogItemId\":\"camera\",\"nickname\":\"主机\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").value("id"));
        mvc.perform(put("/equipment/cameras/id/primary").header("Authorization", "Bearer token"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.primary").value(true));
        mvc.perform(get("/equipment/body-lens-combinations").header("Authorization", "Bearer token"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.items[0].defaultEligible").value(true));
        mvc.perform(delete("/equipment/id").header("Authorization", "Bearer token")).andExpect(status().isNoContent());
        verify(equipment).remove("account", "id");
    }

    @Test
    void catalogAuthenticationAndMissingEntryKeepErrors() throws Exception {
        mvc.perform(get("/catalog/cameras")).andExpect(status().isUnauthorized());
        mvc.perform(get("/catalog/cameras").header("Authorization", "Basic token"))
                .andExpect(status().isUnauthorized());
        when(catalog.compatibility(anyString(), anyString())).thenThrow(new CatalogNotFoundException());
        mvc.perform(get("/catalog/compatibility").param("cameraId", "missing").param("lensId", "lens")
                .header("Authorization", "Bearer token")).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CATALOG_ITEM_NOT_FOUND"));
    }

    @Test
    void equipmentBusinessErrorsKeepHttpMapping() throws Exception {
        when(auth.requireAccountId("token")).thenReturn("account");
        doThrow(new EquipmentNotFoundException()).when(equipment).remove("account", "id");
        mvc.perform(delete("/equipment/id").header("Authorization", "Bearer token")).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("EQUIPMENT_NOT_FOUND"));
        when(equipment.add(eq("account"), anyString(), anyString(), isNull()))
                .thenThrow(new DuplicateEquipmentException(), new InvalidEquipmentException());
        mvc.perform(post("/equipment").header("Authorization", "Bearer token").contentType(MediaType.APPLICATION_JSON)
                .content("{\"kind\":\"CAMERA\",\"catalogItemId\":\"camera\"}"))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("EQUIPMENT_ALREADY_OWNED"));
        mvc.perform(post("/equipment").header("Authorization", "Bearer token").contentType(MediaType.APPLICATION_JSON)
                .content("{\"kind\":\"INVALID\",\"catalogItemId\":\"camera\"}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("INVALID_EQUIPMENT"));
        when(equipment.setPrimaryCamera("account", "id")).thenThrow(new NotCameraException());
        mvc.perform(put("/equipment/cameras/id/primary").header("Authorization", "Bearer token"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("INVALID_EQUIPMENT"));
    }
}
