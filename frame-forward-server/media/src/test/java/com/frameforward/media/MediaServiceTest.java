package com.frameforward.media;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.io.TempDir;

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
  @Test void removesOriginalAndDerivativeForOneWorkOnly(@TempDir Path root) throws Exception {
    MediaMapper mapper = mock(MediaMapper.class);
    Path original = Files.writeString(root.resolve("original.jpg"), "original");
    Path derivative = Files.writeString(root.resolve("preview.jpg"), "preview");
    Path unrelated = Files.writeString(root.resolve("unrelated.jpg"), "unrelated");
    MediaEntity target = new MediaEntity("target", "owner", "hash", 1, 1, original.toString(), derivative.toString(), "{}");
    when(mapper.selectOne(org.mockito.ArgumentMatchers.any())).thenReturn(target);

    new MediaService(mapper, root.toString()).deleteForWork("owner", "target");

    org.junit.jupiter.api.Assertions.assertFalse(Files.exists(original));
    org.junit.jupiter.api.Assertions.assertFalse(Files.exists(derivative));
    org.junit.jupiter.api.Assertions.assertTrue(Files.exists(unrelated));
    verify(mapper).deleteById("target");
  }
  @Test void removesEveryOwnedFileWithoutTouchingAnotherAccount(@TempDir Path root) throws Exception {
    MediaMapper mapper = mock(MediaMapper.class);
    Path first = Files.writeString(root.resolve("first.jpg"), "first"), second = Files.writeString(root.resolve("second.jpg"), "second"), shared = Files.writeString(root.resolve("shared.jpg"), "shared");
    MediaEntity one = new MediaEntity("one", "owner", "one", 1, 1, first.toString(), first.toString(), "{}"), two = new MediaEntity("two", "owner", "two", 1, 1, second.toString(), second.toString(), "{}");
    when(mapper.selectList(org.mockito.ArgumentMatchers.any())).thenReturn(List.of(one, two));
    when(mapper.selectOne(org.mockito.ArgumentMatchers.any())).thenReturn(one, two);
    new MediaService(mapper, root.toString()).deleteForAccount("owner");
    org.junit.jupiter.api.Assertions.assertFalse(Files.exists(first));
    org.junit.jupiter.api.Assertions.assertFalse(Files.exists(second));
    org.junit.jupiter.api.Assertions.assertTrue(Files.exists(shared));
    verify(mapper).deleteById("one"); verify(mapper).deleteById("two");
  }
}
