package com.das.portfolioservice.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*

@RestController
@RequestMapping("/api/portfolio/upload")
@CrossOrigin(origins = ["*"])
class UploadController {

    private val uploadDir = "/uploads/portfolio"
    private val maxFileSize = 10 * 1024 * 1024 // 10MB
    private val allowedExtensions = listOf("jpg", "jpeg", "png", "gif", "webp", "svg")

    init {
        // Create upload directory if it doesn't exist
        File(uploadDir).mkdirs()
    }

    @PostMapping
    fun uploadFile(@RequestParam("file") file: MultipartFile): ResponseEntity<Map<String, String>> {
        try {
            // Validate file
            if (file.isEmpty) {
                return ResponseEntity.badRequest()
                    .body(mapOf("error" to "Please select a file to upload"))
            }

            // Check file size
            if (file.size > maxFileSize) {
                return ResponseEntity.badRequest()
                    .body(mapOf("error" to "File size exceeds maximum limit of 10MB"))
            }

            // Check file extension
            val originalFilename = file.originalFilename ?: "unknown"
            val extension = originalFilename.substringAfterLast('.', "").lowercase()
            if (!allowedExtensions.contains(extension)) {
                return ResponseEntity.badRequest()
                    .body(mapOf("error" to "Invalid file type. Allowed: ${allowedExtensions.joinToString(", ")}"))
            }

            // Generate unique filename
            val uniqueFilename = "${UUID.randomUUID()}_${System.currentTimeMillis()}.$extension"
            val targetPath: Path = Paths.get(uploadDir, uniqueFilename)

            // Save file
            Files.copy(file.inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING)

            // Return file URL
            val fileUrl = "/uploads/portfolio/$uniqueFilename"
            return ResponseEntity.ok(
                mapOf(
                    "message" to "File uploaded successfully",
                    "filename" to uniqueFilename,
                    "url" to fileUrl,
                    "size" to file.size.toString()
                )
            )

        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Failed to upload file: ${e.message}"))
        }
    }

    @DeleteMapping("/{filename}")
    fun deleteFile(@PathVariable filename: String): ResponseEntity<Map<String, String>> {
        try {
            val filePath = Paths.get(uploadDir, filename)
            if (Files.exists(filePath)) {
                Files.delete(filePath)
                return ResponseEntity.ok(mapOf("message" to "File deleted successfully"))
            }
            return ResponseEntity.notFound().build()
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Failed to delete file: ${e.message}"))
        }
    }

    @GetMapping("/list")
    fun listFiles(): ResponseEntity<List<Map<String, String>>> {
        try {
            val directory = File(uploadDir)
            if (!directory.exists()) {
                return ResponseEntity.ok(emptyList())
            }

            val files = directory.listFiles()?.map { file ->
                mapOf(
                    "filename" to file.name,
                    "url" to "/uploads/portfolio/${file.name}",
                    "size" to file.length().toString(),
                    "lastModified" to file.lastModified().toString()
                )
            } ?: emptyList()

            return ResponseEntity.ok(files)
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(emptyList())
        }
    }
}
