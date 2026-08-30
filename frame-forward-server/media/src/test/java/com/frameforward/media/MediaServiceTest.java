package com.frameforward.media;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import java.awt.image.BufferedImage;

class MediaServiceTest {
  private final MediaService service = new MediaService(mock(MediaMapper.class), "target/test-media");
  @Test void rejectsCorruptJpeg() {
    var file = new MockMultipartFile("file", "bad.jpg", "image/jpeg", "not a jpeg".getBytes());
    assertThrows(MediaService.InvalidMediaException.class, () -> service.ingest("owner", file));
  }
  @Test void rejectsOversizedJpeg() {
    var file = new MockMultipartFile("file", "large.jpg", "image/jpeg", new byte[(int) MediaService.MAX_BYTES + 1]);
    assertThrows(MediaService.TooLargeException.class, () -> service.ingest("owner", file));
  }
  @Test void cannotFetchAnotherOwnersMedia() {
    MediaMapper mapper = mock(MediaMapper.class);
    MediaService owned = new MediaService(mapper, "target/test-media");
    when(mapper.selectOne(org.mockito.ArgumentMatchers.any())).thenReturn(null);
    assertThrows(MediaService.NotFoundException.class, () -> owned.get("different-owner", "media-id"));
  }
  @Test void correctsPortraitExifOrientationForAiCopy() throws Exception {
    var method = MediaService.class.getDeclaredMethod("orient", BufferedImage.class, int.class);
    method.setAccessible(true);
    BufferedImage source = new BufferedImage(40, 20, BufferedImage.TYPE_INT_RGB);
    BufferedImage corrected = (BufferedImage) method.invoke(null, source, 6);
    org.junit.jupiter.api.Assertions.assertEquals(20, corrected.getWidth());
    org.junit.jupiter.api.Assertions.assertEquals(40, corrected.getHeight());
  }
}
