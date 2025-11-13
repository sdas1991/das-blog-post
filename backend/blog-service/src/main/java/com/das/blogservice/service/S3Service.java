package com.das.blogservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region:us-east-1}")
    private String region;

    /**
     * Upload a file to S3 and return the public URL
     */
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        try {
            // Generate unique file name
            String fileName = folder + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

            // Create put request
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            // Upload file
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            // Return the public URL
            String fileUrl = String.format("https://%s.s3.%s.amazonaws.com/%s",
                    bucketName, region, fileName);

            log.info("File uploaded successfully: {}", fileUrl);
            return fileUrl;

        } catch (S3Exception e) {
            log.error("Error uploading file to S3: {}", e.getMessage());
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }

    /**
     * Delete a file from S3
     */
    public void deleteFile(String fileUrl) {
        try {
            // Extract the file key from URL
            String key = extractKeyFromUrl(fileUrl);

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            log.info("File deleted successfully: {}", fileUrl);

        } catch (S3Exception e) {
            log.error("Error deleting file from S3: {}", e.getMessage());
            throw new RuntimeException("Failed to delete file from S3", e);
        }
    }

    /**
     * Extract S3 key from full URL
     */
    private String extractKeyFromUrl(String fileUrl) {
        // Extract key from URL like: https://bucket.s3.region.amazonaws.com/key
        String[] parts = fileUrl.split(bucketName + ".s3." + region + ".amazonaws.com/");
        if (parts.length > 1) {
            return parts[1];
        }
        throw new IllegalArgumentException("Invalid S3 URL format");
    }
}
