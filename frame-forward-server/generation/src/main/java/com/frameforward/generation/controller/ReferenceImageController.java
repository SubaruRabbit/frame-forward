package com.frameforward.generation.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.frameforward.ai.model.dto.AiTaskCreated;
import com.frameforward.auth.service.AuthService;
import com.frameforward.generation.model.dto.ReferenceImageRequest;
import com.frameforward.generation.service.ReferenceImageService;

@RestController
@RequestMapping("/reference-images")
public class ReferenceImageController {
    private final ReferenceImageService referenceImages;
    public ReferenceImageController(ReferenceImageService referenceImages) {
        this.referenceImages = referenceImages;
    }
    @PostMapping
    ResponseEntity<AiTaskCreated> create(@RequestHeader(name = "Authorization", required = false) String authorization,
            @RequestHeader("Idempotency-Key") String key, @RequestBody ReferenceImageRequest request) {
        return ResponseEntity.accepted().body(referenceImages.create(AuthService.bearer(authorization), key, request));
    }
}
