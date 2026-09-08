package com.frameforward.media.service;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.util.*;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frameforward.auth.gateway.AccountDataCleanup;
import com.frameforward.media.gateway.WorkMediaCleanup;
import com.frameforward.media.manager.MediaManager;
import com.frameforward.media.model.dto.MediaResponse;
import com.frameforward.media.model.dto.UploadProgress;
import com.frameforward.media.model.entity.MediaEntity;

@Service
@Primary
public class MediaService implements WorkMediaCleanup, AccountDataCleanup {
    static final long MAX_BYTES = 50L * 1024 * 1024;
    private final MediaManager media;
    private final Path root;
    private final ObjectMapper json = new ObjectMapper();
    public MediaService(MediaManager media, @Value("${frame-forward.media.storage-root:./var/media}") String root) {
        this.media = media;
        this.root = Paths.get(root).toAbsolutePath().normalize();
    }
    @Transactional
    public MediaResponse ingest(String ownerId, MultipartFile upload) {
        validateUpload(upload);
        Path temporary = null;
        try {
            temporary = copyToTemporaryFile(upload);
            String hash = sha256(temporary);
            MediaEntity existing = findExisting(ownerId, hash);
            if (existing != null)
                return response(existing);
            MediaEntity saved = storeDecodedMedia(ownerId, hash, temporary);
            return response(saved);
        } catch (TooLargeException | InvalidMediaException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidMediaException("JPEG could not be processed");
        } finally {
            if (temporary != null)
                try {
                    Files.deleteIfExists(temporary);
                } catch (IOException ignored) {
                }
        }
    }
    private static void validateUpload(MultipartFile upload) {
        if (upload == null || upload.isEmpty())
            throw new InvalidMediaException("A JPEG file is required");
        if (upload.getSize() > MAX_BYTES)
            throw new TooLargeException();
    }
    private Path copyToTemporaryFile(MultipartFile upload) throws IOException {
        Path temporaryDirectory = root.resolve("tmp");
        Files.createDirectories(temporaryDirectory);
        Path temporary = Files.createTempFile(temporaryDirectory, "upload-", ".jpg");
        try (InputStream in = upload.getInputStream()) {
            copyBounded(in, temporary);
        }
        return temporary;
    }
    private MediaEntity findExisting(String ownerId, String hash) {
        return media.findExisting(ownerId, hash);
    }
    private MediaEntity storeDecodedMedia(String ownerId, String hash, Path temporary) throws Exception {
        BufferedImage decoded = readJpeg(temporary);
        Metadata metadata = ImageMetadataReader.readMetadata(temporary.toFile());
        BufferedImage corrected = orient(decoded, orientation(metadata));
        String id = UUID.randomUUID().toString();
        Path original = moveOriginal(temporary, id);
        Path sanitized = writeSanitizedCopy(corrected, id);
        MediaEntity saved = new MediaEntity(id, ownerId, hash, corrected.getWidth(), corrected.getHeight(),
                original.toString(), sanitized.toString(), allowedExif(metadata));
        media.save(saved);
        return saved;
    }
    private static BufferedImage readJpeg(Path temporary) throws IOException {
        byte[] signature = Files.readAllBytes(temporary);
        if (!hasJpegSignature(signature))
            throw new InvalidMediaException("File is not a JPEG");
        BufferedImage decoded = ImageIO.read(temporary.toFile());
        if (decoded == null)
            throw new InvalidMediaException("JPEG cannot be decoded");
        return decoded;
    }
    private static boolean hasJpegSignature(byte[] signature) {
        return signature.length >= 3 && (signature[0] & 255) == 255 && (signature[1] & 255) == 216
                && (signature[2] & 255) == 255;
    }
    private Path moveOriginal(Path temporary, String id) throws IOException {
        Path originals = root.resolve("original");
        Files.createDirectories(originals);
        Path original = originals.resolve(id + ".jpg");
        Files.move(temporary, original, StandardCopyOption.ATOMIC_MOVE);
        return original;
    }
    private Path writeSanitizedCopy(BufferedImage corrected, String id) throws IOException {
        Path ai = root.resolve("ai");
        Files.createDirectories(ai);
        Path sanitized = ai.resolve(id + ".jpg");
        if (!ImageIO.write(corrected, "jpeg", sanitized.toFile()))
            throw new InvalidMediaException("JPEG encoder unavailable");
        return sanitized;
    }
    public MediaResponse get(String ownerId, String id) {
        var item = media.findOwnedEntity(ownerId, id);
        if (item == null)
            throw new NotFoundException();
        return response(item);
    }
    @Transactional
    public MediaResponse registerGenerated(String ownerId, String imageUrl, int width, int height) {
        String id = UUID.randomUUID().toString();
        String hash = UUID.nameUUIDFromBytes(imageUrl.getBytes(java.nio.charset.StandardCharsets.UTF_8)).toString()
                .replace("-", "");
        MediaEntity generated = new MediaEntity(id, ownerId, hash, width, height, imageUrl, imageUrl, "{}");
        media.save(generated);
        return response(generated);
    }
    public UploadProgress progress(String ownerId, String id) {
        get(ownerId, id);
        return new UploadProgress(id, "COMPLETED", 100);
    }
    @Override
    public void deleteForWork(String ownerId, String id) {
        var item = media.findOwnedEntity(ownerId, id);
        if (item == null)
            return;
        try {
            deleteStoredFile(item.originalPath);
            deleteStoredFile(item.aiCopyPath);
            media.delete(item.id);
        } catch (IOException exception) {
            throw new CleanupFailedException(exception);
        }
    }
    @Override
    public void deleteForAccount(String accountId) {
        media.listOwned(accountId).forEach(item -> deleteForWork(accountId, item.id));
    }
    private void deleteStoredFile(String value) throws IOException {
        if (value == null || value.isBlank() || value.contains("://"))
            return;
        Path file = Paths.get(value).toAbsolutePath().normalize();
        if (file.startsWith(root))
            Files.deleteIfExists(file);
    }
    private MediaResponse response(MediaEntity e) {
        return new MediaResponse(e.id, e.width, e.height, e.contentHash, "COMPLETED", e.exifJson);
    }
    private static void copyBounded(InputStream in, Path target) throws IOException {
        long total = 0;
        byte[] buffer = new byte[8192];
        try (OutputStream out = Files.newOutputStream(target)) {
            int n;
            while ((n = in.read(buffer)) != -1) {
                total += n;
                if (total > MAX_BYTES)
                    throw new TooLargeException();
                out.write(buffer, 0, n);
            }
        }
    }
    private static String sha256(Path file) throws Exception {
        MessageDigest d = MessageDigest.getInstance("SHA-256");
        try (InputStream in = Files.newInputStream(file)) {
            in.transferTo(new DigestOutputStream(OutputStream.nullOutputStream(), d));
        }
        return HexFormat.of().formatHex(d.digest());
    }
    private String allowedExif(Metadata m) {
        Map<String, String> out = new LinkedHashMap<>();
        ExifSubIFDDirectory exif = m.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
        if (exif != null) {
            copy(exif, out, ExifSubIFDDirectory.TAG_EXPOSURE_TIME, "exposureTime");
            copy(exif, out, ExifSubIFDDirectory.TAG_FNUMBER, "fNumber");
            copy(exif, out, ExifSubIFDDirectory.TAG_ISO_EQUIVALENT, "iso");
            copy(exif, out, ExifSubIFDDirectory.TAG_FOCAL_LENGTH, "focalLength");
            copy(exif, out, ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL, "capturedAt");
        }
        try {
            return json.writeValueAsString(out);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }
    private static void copy(ExifSubIFDDirectory d, Map<String, String> out, int tag, String name) {
        if (d.containsTag(tag))
            out.put(name, d.getDescription(tag));
    }
    public static class CleanupFailedException extends RuntimeException {
        public CleanupFailedException(Throwable cause) {
            super("媒体文件清理失败", cause);
        }
    }
    private static int orientation(Metadata m) {
        ExifIFD0Directory d = m.getFirstDirectoryOfType(ExifIFD0Directory.class);
        return d == null
                ? 1
                : d.getInteger(ExifIFD0Directory.TAG_ORIENTATION) == null
                        ? 1
                        : d.getInteger(ExifIFD0Directory.TAG_ORIENTATION);
    }
    private static BufferedImage orient(BufferedImage src, int o) {
        if (o == 1)
            return src;
        int w = src.getWidth(), h = src.getHeight();
        boolean swap = o >= 5 && o <= 8;
        BufferedImage dst = new BufferedImage(swap ? h : w, swap ? w : h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = dst.createGraphics();
        AffineTransform t = new AffineTransform();
        switch (o) {
            case 3 :
                t.translate(w, h);
                t.rotate(Math.PI);
                break;
            case 6 :
                t.translate(h, 0);
                t.rotate(Math.PI / 2);
                break;
            case 8 :
                t.translate(0, w);
                t.rotate(-Math.PI / 2);
                break;
            default :
                return src;
        }
        g.drawImage(src, t, null);
        g.dispose();
        return dst;
    }
    public static class InvalidMediaException extends RuntimeException {
        InvalidMediaException(String message) {
            super(message);
        }
    }
    public static class TooLargeException extends RuntimeException {
    }
    public static class NotFoundException extends RuntimeException {
    }
}
