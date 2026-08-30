package com.frameforward.media;
import com.frameforward.auth.AuthController;
import com.frameforward.auth.AuthService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
@RestController @RequestMapping("/media") public class MediaController {
  private final MediaService media; private final AuthService auth;
  public MediaController(MediaService media,AuthService auth){this.media=media;this.auth=auth;}
  @PostMapping(value="/jpeg",consumes=MediaType.MULTIPART_FORM_DATA_VALUE) ResponseEntity<MediaService.MediaResponse> upload(@RequestHeader(name="Authorization",required=false) String authorization,@RequestPart("file") MultipartFile file){return ResponseEntity.status(HttpStatus.CREATED).body(media.ingest(auth.requireAccountId(AuthController.bearer(authorization)),file));}
  @GetMapping("/{id}") MediaService.MediaResponse get(@RequestHeader(name="Authorization",required=false) String authorization,@PathVariable String id){return media.get(auth.requireAccountId(AuthController.bearer(authorization)),id);}
  @GetMapping("/uploads/{id}") MediaService.UploadProgress progress(@RequestHeader(name="Authorization",required=false) String authorization,@PathVariable String id){return media.progress(auth.requireAccountId(AuthController.bearer(authorization)),id);}
}
