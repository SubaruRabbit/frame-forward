package com.frameforward.generation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.frameforward.ai.AiTaskRuntime;
import com.frameforward.auth.AuthController;

@RestController
@RequestMapping("/reference-images")
public class ReferenceImageController {
    private final ReferenceImageService referenceImages;
    public ReferenceImageController(ReferenceImageService referenceImages) {
        this.referenceImages = referenceImages;
    }
    @PostMapping
    ResponseEntity<AiTaskRuntime.Created> create(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @RequestHeader("Idempotency-Key") String key, @RequestBody ReferenceImageService.Request request) {
        return ResponseEntity.accepted()
                .body(referenceImages.create(AuthController.bearer(authorization), key, request));
    }
}
