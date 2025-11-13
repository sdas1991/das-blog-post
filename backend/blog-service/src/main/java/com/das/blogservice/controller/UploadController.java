package com.das.blogservice.controller;

import com.das.blogservice.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class UploadController {

    private final S3Service s3Service;

    @PostMapping("/widget-image")
    public ResponseEntity<Map<String, String>> uploadWidgetImage(
            @RequestParam("file") MultipartFile file) {

        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Please select a file to upload"));
            }

            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Only image files are allowed"));
            }

            // Validate file size (max 5MB)
            long maxSize = 5 * 1024 * 1024; // 5MB
            if (file.getSize() > maxSize) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "File size must be less than 5MB"));
            }

            // Upload to S3
            String imageUrl = s3Service.uploadFile(file, "widgets");

            Map<String, String> response = new HashMap<>();
            response.put("url", imageUrl);
            response.put("message", "Image uploaded successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error uploading image: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload image: " + e.getMessage()));
        }
    }

    @DeleteMapping("/widget-image")
    public ResponseEntity<Map<String, String>> deleteWidgetImage(
            @RequestParam("url") String imageUrl) {

        try {
            s3Service.deleteFile(imageUrl);
            return ResponseEntity.ok(Map.of("message", "Image deleted successfully"));

        } catch (Exception e) {
            log.error("Error deleting image: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete image: " + e.getMessage()));
        }
    }
}
