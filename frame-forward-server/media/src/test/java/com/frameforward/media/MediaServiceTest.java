package com.frameforward.media;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;

class MediaServiceTest {
    private final MediaService service = new MediaService(mock(MediaMapper.class), "target/test-media");
    @Test
    void rejectsCorruptJpeg() {
        var file = new MockMultipartFile("file", "bad.jpg", "image/jpeg", "not a jpeg".getBytes());
        assertThrows(MediaService.InvalidMediaException.class, () -> service.ingest("owner", file));
    }
    @Test
    void rejectsOversizedJpeg() {
        var file = new MockMultipartFile("file", "large.jpg", "image/jpeg", new byte[(int) MediaService.MAX_BYTES + 1]);
        assertThrows(MediaService.TooLargeException.class, () -> service.ingest("owner", file));
    }

    @Test
    void storesOriginalAndSanitizedCopiesForADecodedJpeg(@TempDir Path root) throws Exception {
        MediaMapper mapper = mock(MediaMapper.class);
        var file = new MockMultipartFile("file", "photo.jpg", "image/jpeg", jpeg(12, 8));

        MediaService.MediaResponse response = new MediaService(mapper, root.toString()).ingest("owner", file);

        ArgumentCaptor<MediaEntity> saved = ArgumentCaptor.forClass(MediaEntity.class);
        verify(mapper).insert(saved.capture());
        assertEquals(saved.getValue().id, response.id());
        assertEquals(12, response.width());
        assertEquals(8, response.height());
        assertTrue(Files.exists(Path.of(saved.getValue().originalPath)));
        assertTrue(Files.exists(Path.of(saved.getValue().aiCopyPath)));
    }
    @Test
    void cannotFetchAnotherOwnersMedia() {
        MediaMapper mapper = mock(MediaMapper.class);
        MediaService owned = new MediaService(mapper, "target/test-media");
        when(mapper.selectOne(org.mockito.ArgumentMatchers.any())).thenReturn(null);
        assertThrows(MediaService.NotFoundException.class, () -> owned.get("different-owner", "media-id"));
    }
    @Test
    void correctsPortraitExifOrientationForAiCopy() throws Exception {
        var method = MediaService.class.getDeclaredMethod("orient", BufferedImage.class, int.class);
        method.setAccessible(true);
        BufferedImage source = new BufferedImage(40, 20, BufferedImage.TYPE_INT_RGB);
        BufferedImage corrected = (BufferedImage) method.invoke(null, source, 6);
        org.junit.jupiter.api.Assertions.assertEquals(20, corrected.getWidth());
        org.junit.jupiter.api.Assertions.assertEquals(40, corrected.getHeight());
    }
    @Test
    void removesOriginalAndDerivativeForOneWorkOnly(@TempDir Path root) throws Exception {
        MediaMapper mapper = mock(MediaMapper.class);
        Path original = Files.writeString(root.resolve("original.jpg"), "original");
        Path derivative = Files.writeString(root.resolve("preview.jpg"), "preview");
        Path unrelated = Files.writeString(root.resolve("unrelated.jpg"), "unrelated");
        MediaEntity target = new MediaEntity("target", "owner", "hash", 1, 1, original.toString(),
                derivative.toString(), "{}");
        when(mapper.selectOne(org.mockito.ArgumentMatchers.any())).thenReturn(target);

        new MediaService(mapper, root.toString()).deleteForWork("owner", "target");

        org.junit.jupiter.api.Assertions.assertFalse(Files.exists(original));
        org.junit.jupiter.api.Assertions.assertFalse(Files.exists(derivative));
        org.junit.jupiter.api.Assertions.assertTrue(Files.exists(unrelated));
        verify(mapper).deleteById("target");
    }
    @Test
    void removesEveryOwnedFileWithoutTouchingAnotherAccount(@TempDir Path root) throws Exception {
        MediaMapper mapper = mock(MediaMapper.class);
        Path first = Files.writeString(root.resolve("first.jpg"), "first"),
                second = Files.writeString(root.resolve("second.jpg"), "second"),
                shared = Files.writeString(root.resolve("shared.jpg"), "shared");
        MediaEntity one = new MediaEntity("one", "owner", "one", 1, 1, first.toString(), first.toString(), "{}"),
                two = new MediaEntity("two", "owner", "two", 1, 1, second.toString(), second.toString(), "{}");
        when(mapper.selectList(org.mockito.ArgumentMatchers.any())).thenReturn(List.of(one, two));
        when(mapper.selectOne(org.mockito.ArgumentMatchers.any())).thenReturn(one, two);
        new MediaService(mapper, root.toString()).deleteForAccount("owner");
        org.junit.jupiter.api.Assertions.assertFalse(Files.exists(first));
        org.junit.jupiter.api.Assertions.assertFalse(Files.exists(second));
        org.junit.jupiter.api.Assertions.assertTrue(Files.exists(shared));
        verify(mapper).deleteById("one");
        verify(mapper).deleteById("two");
    }

    private static byte[] jpeg(int width, int height) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        assertTrue(ImageIO.write(new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB), "jpeg", output));
        return output.toByteArray();
    }
}
