package com.frameforward.equipment.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.frameforward.auth.service.AuthService;
import com.frameforward.equipment.model.dto.AccessoryType;
import com.frameforward.equipment.model.dto.Camera;
import com.frameforward.equipment.model.dto.CatalogResponse;
import com.frameforward.equipment.model.dto.Compatibility;
import com.frameforward.equipment.model.dto.Lens;
import com.frameforward.equipment.service.CatalogService;

@RestController
@RequestMapping("/catalog")
public class CatalogController {
    private final AuthService auth;
    private final CatalogService catalog;
    public CatalogController(AuthService auth, CatalogService catalog) {
        this.auth = auth;
        this.catalog = catalog;
    }

    @GetMapping("/cameras")
    public CatalogResponse<Camera> cameras(
            @RequestHeader(name = "Authorization", required = false) String authorization) {
        authenticate(authorization);
        return new CatalogResponse<>(CatalogService.VERSION, catalog.cameras());
    }
    @GetMapping("/lenses")
    public CatalogResponse<Lens> lenses(@RequestHeader(name = "Authorization", required = false) String authorization,
            @RequestParam(required = false) String brand) {
        authenticate(authorization);
        return new CatalogResponse<>(CatalogService.VERSION, catalog.lenses(brand));
    }
    @GetMapping("/accessories")
    public CatalogResponse<AccessoryType> accessories(
            @RequestHeader(name = "Authorization", required = false) String authorization) {
        authenticate(authorization);
        return new CatalogResponse<>(CatalogService.VERSION, catalog.accessories());
    }
    @GetMapping("/compatibility")
    public Compatibility compatibility(@RequestHeader(name = "Authorization", required = false) String authorization,
            @RequestParam String cameraId, @RequestParam String lensId) {
        authenticate(authorization);
        return catalog.compatibility(cameraId, lensId);
    }
    private void authenticate(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer "))
            throw new AuthService.InvalidSessionException();
        auth.requireAccountId(authorization.substring(7));
    }

}
