package com.frameforward.media.controller;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.frameforward.auth.service.AuthService;
import com.frameforward.media.model.dto.MediaResponse;
import com.frameforward.media.model.dto.UploadProgress;
import com.frameforward.media.service.MediaService;
@RestController
@RequestMapping("/media")
public class MediaController {
    private final MediaService media;
    private final AuthService auth;
    public MediaController(MediaService media, AuthService auth) {
        this.media = media;
        this.auth = auth;
    }
    @PostMapping(value = "/jpeg", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<MediaResponse> upload(@RequestHeader(name = "Authorization", required = false) String authorization,
            @RequestPart("file") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(media.ingest(auth.requireAccountId(AuthService.bearer(authorization)), file));
    }
    @GetMapping("/{id}")
    MediaResponse get(@RequestHeader(name = "Authorization", required = false) String authorization,
            @PathVariable String id) {
        return media.get(auth.requireAccountId(AuthService.bearer(authorization)), id);
    }
    @GetMapping("/uploads/{id}")
    UploadProgress progress(@RequestHeader(name = "Authorization", required = false) String authorization,
            @PathVariable String id) {
        return media.progress(auth.requireAccountId(AuthService.bearer(authorization)), id);
    }
}
