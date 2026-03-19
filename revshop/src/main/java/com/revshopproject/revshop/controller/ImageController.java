package com.revshopproject.revshop.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ImageController {

    @GetMapping({
        "/uploads/product-images/{productId}/{filename}",
        "/product-images/{productId}/{filename}"
    })
    public ResponseEntity<Resource> serveImage(
            @PathVariable String productId,
            @PathVariable String filename) {
        try {
            // Decode URL-encoded characters (e.g. spaces: %20 or +)
            String decodedFilename  = URLDecoder.decode(filename,  StandardCharsets.UTF_8);
            String decodedProductId = URLDecoder.decode(productId, StandardCharsets.UTF_8);

            Path filePath = Paths.get("product-images", decodedProductId, decodedFilename);

            if (!Files.exists(filePath)) {
                // Try replacing spaces with underscores as fallback
                String underscored = decodedFilename.replace(" ", "_");
                filePath = Paths.get("product-images", decodedProductId, underscored);
            }

            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            // Detect content type
            String contentType = detectContentType(filePath, decodedFilename);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CACHE_CONTROL, "max-age=86400")
                    .body(resource);

        } catch (MalformedURLException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private String detectContentType(Path filePath, String filename) {
        // 1. Try probing the actual file bytes
        try {
            String ct = Files.probeContentType(filePath);
            if (ct != null) return ct;
        } catch (IOException ignored) {}

        // 2. Guess from extension
        String lower = filename.toLowerCase();
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".png"))  return "image/png";
        if (lower.endsWith(".gif"))  return "image/gif";
        if (lower.endsWith(".webp")) return "image/webp";
        if (lower.endsWith(".svg"))  return "image/svg+xml";

        // 3. Read magic bytes to detect image type for files with no extension
        try {
            byte[] header = new byte[12];
            int read = Files.newInputStream(filePath).read(header);
            if (read >= 3) {
                // JPEG: FF D8 FF
                if ((header[0] & 0xFF) == 0xFF && (header[1] & 0xFF) == 0xD8) return "image/jpeg";
                // PNG: 89 50 4E 47
                if ((header[0] & 0xFF) == 0x89 && header[1] == 'P' && header[2] == 'N') return "image/png";
                // GIF: 47 49 46
                if (header[0] == 'G' && header[1] == 'I' && header[2] == 'F') return "image/gif";
                // WebP: 52 49 46 46 ... 57 45 42 50
                if (header[0] == 'R' && header[1] == 'I' && header[2] == 'F') return "image/webp";
            }
        } catch (IOException ignored) {}

        // 4. Default
        return "image/jpeg";
    }
}