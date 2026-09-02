package com.frameforward.equipment;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.frameforward.auth.AuthController;
import com.frameforward.auth.AuthService;

@RestController
@RequestMapping("/equipment")
public class UserEquipmentController {
    private final UserEquipmentService equipment;
    private final AuthService auth;

    public UserEquipmentController(UserEquipmentService equipment, AuthService auth) {
        this.equipment = equipment;
        this.auth = auth;
    }

    @GetMapping
    UserEquipmentList list(@RequestHeader(name = "Authorization", required = false) String authorization) {
        return new UserEquipmentList(equipment.list(accountId(authorization)));
    }

    @PostMapping
    ResponseEntity<UserEquipmentService.Item> add(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @RequestBody CreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                equipment.add(accountId(authorization), request.kind(), request.catalogItemId(), request.nickname()));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> remove(@RequestHeader(name = "Authorization", required = false) String authorization,
            @PathVariable String id) {
        equipment.remove(accountId(authorization), id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/cameras/{id}/primary")
    UserEquipmentService.Item setPrimary(@RequestHeader(name = "Authorization", required = false) String authorization,
            @PathVariable String id) {
        return equipment.setPrimaryCamera(accountId(authorization), id);
    }

    @GetMapping("/body-lens-combinations")
    BodyLensCombinationList combinations(
            @RequestHeader(name = "Authorization", required = false) String authorization) {
        return new BodyLensCombinationList(equipment.combinations(accountId(authorization)));
    }

    private String accountId(String authorization) {
        return auth.requireAccountId(AuthController.bearer(authorization));
    }
    record CreateRequest(String kind, String catalogItemId, String nickname) {
    }
    record UserEquipmentList(List<UserEquipmentService.Item> items) {
    }
    record BodyLensCombinationList(List<UserEquipmentService.BodyLensCombination> items) {
    }
}
