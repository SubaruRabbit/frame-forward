package com.frameforward.media.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.frameforward.media.manager.MediaManager;
import com.frameforward.media.model.entity.MediaEntity;

class MediaServiceBoundaryTest {
    private final MediaManager manager = mock(MediaManager.class);
    @TempDir
    Path root;

    @Test
    void rejectsEmptyAndUnreadableUploads() throws Exception {
        var service = new MediaService(manager, root.toString());
        assertThatThrownBy(() -> service.ingest("owner", null)).isInstanceOf(MediaService.InvalidMediaException.class);
        assertThatThrownBy(() -> service.ingest("owner", new MockMultipartFile("file", new byte[0])))
                .isInstanceOf(MediaService.InvalidMediaException.class);
        var file = mock(MultipartFile.class);
        when(file.getInputStream()).thenThrow(new IOException("unavailable"));
        assertThatThrownBy(() -> service.ingest("owner", file)).isInstanceOf(MediaService.InvalidMediaException.class)
                .hasMessage("JPEG could not be processed");
        verifyNoInteractions(manager);
    }

    @Test
    void rejectsAllInvalidSignaturePositionsAndUndecodableJpeg() {
        var service = new MediaService(manager, root.toString());
        for (byte[] bytes : new byte[][]{{1}, {0, 0, 0}, {(byte) 255, 0, 0}, {(byte) 255, (byte) 216, 0},
                {(byte) 255, (byte) 216, (byte) 255}}) {
            assertThatThrownBy(() -> service.ingest("owner", new MockMultipartFile("file", bytes)))
                    .isInstanceOf(MediaService.InvalidMediaException.class);
        }
    }

    @Test
    void duplicateUploadReusesOwnedRecordWithoutSaving() throws Exception {
        var existing = entity("existing", null, null);
        when(manager.findExisting(eq("owner"), anyString())).thenReturn(existing);
        var response = new MediaService(manager, root.toString()).ingest("owner",
                new MockMultipartFile("file", new byte[]{1}));
        assertThat(response.id()).isEqualTo("existing");
        verify(manager, never()).save(any());
        try (var files = Files.list(root.resolve("tmp"))) {
            assertThat(files).isEmpty();
        }
    }

    @Test
    void generatedRegistrationAndProgressPreserveMetadataAndOwnership() {
        var service = new MediaService(manager, root.toString());
        var response = service.registerGenerated("owner", "https://example.test/image.jpg", 20, 10);
        var saved = org.mockito.ArgumentCaptor.forClass(MediaEntity.class);
        verify(manager).save(saved.capture());
        assertThat(saved.getValue().ownerId).isEqualTo("owner");
        assertThat(saved.getValue().originalPath).isEqualTo(saved.getValue().aiCopyPath);
        assertThat(response.width()).isEqualTo(20);
        assertThat(response.height()).isEqualTo(10);
        when(manager.findOwnedEntity("owner", response.id())).thenReturn(saved.getValue());
        assertThat(service.progress("owner", response.id()).progress()).isEqualTo(100);
        assertThatThrownBy(() -> service.progress("other", response.id()))
                .isInstanceOf(MediaService.NotFoundException.class);
    }

    @Test
    void cleanupSkipsMissingRemoteBlankAndOutsideRootFiles() throws Exception {
        var managed = root.resolve("managed");
        var outside = Files.writeString(root.resolve("outside.jpg"), "keep");
        var service = new MediaService(manager, managed.toString());
        service.deleteForWork("owner", "missing");
        verify(manager, never()).delete(anyString());
        for (String path : new String[]{null, " ", "https://example.test/image.jpg", outside.toString()}) {
            when(manager.findOwnedEntity("owner", "id")).thenReturn(entity("id", path, path));
            service.deleteForWork("owner", "id");
        }
        assertThat(Files.readString(outside)).isEqualTo("keep");
        verify(manager, times(4)).delete("id");
    }

    @Test
    void cleanupFailureDoesNotDeleteDatabaseRecord() throws Exception {
        Path nonEmpty = Files.createDirectory(root.resolve("non-empty"));
        Files.writeString(nonEmpty.resolve("child"), "keep");
        when(manager.findOwnedEntity("owner", "id")).thenReturn(entity("id", nonEmpty.toString(), null));
        assertThatThrownBy(() -> new MediaService(manager, root.toString()).deleteForWork("owner", "id"))
                .isInstanceOf(MediaService.CleanupFailedException.class).hasCauseInstanceOf(IOException.class);
        verify(manager, never()).delete(anyString());
    }

    @Test
    void preservesOrientationDefaultsRotationsAndExifAllowlist() {
        var source = new BufferedImage(40, 20, BufferedImage.TYPE_INT_RGB);
        for (int orientation : new int[]{1, 2, 3, 5, 6, 8, 9}) {
            BufferedImage result = ReflectionTestUtils.invokeMethod(MediaService.class, "orient", source, orientation);
            assertThat(result.getWidth()).isEqualTo(orientation == 6 || orientation == 8 ? 20 : 40);
            assertThat(result.getHeight()).isEqualTo(orientation == 6 || orientation == 8 ? 40 : 20);
        }
        var metadata = new Metadata();
        assertThat((Integer) ReflectionTestUtils.invokeMethod(MediaService.class, "orientation", metadata))
                .isEqualTo(1);
        var directory = new ExifIFD0Directory();
        metadata.addDirectory(directory);
        assertThat((Integer) ReflectionTestUtils.invokeMethod(MediaService.class, "orientation", metadata))
                .isEqualTo(1);
        directory.setInt(ExifIFD0Directory.TAG_ORIENTATION, 8);
        assertThat((Integer) ReflectionTestUtils.invokeMethod(MediaService.class, "orientation", metadata))
                .isEqualTo(8);
        var exif = new ExifSubIFDDirectory();
        exif.setInt(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT, 200);
        metadata.addDirectory(exif);
        String json = ReflectionTestUtils.invokeMethod(new MediaService(manager, root.toString()), "allowedExif",
                metadata);
        assertThat(json).contains("\"iso\":\"200\"").doesNotContain("orientation", "capturedAt");
    }

    private static MediaEntity entity(String id, String original, String copy) {
        return new MediaEntity(id, "owner", "hash", 20, 10, original, copy, "{}");
    }
}
