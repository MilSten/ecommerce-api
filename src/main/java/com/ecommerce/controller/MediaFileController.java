package com.ecommerce.controller;

import com.ecommerce.dto.ApiResponse;
import com.ecommerce.dto.MediaFileDto;
import com.ecommerce.entity.MediaFile;
import com.ecommerce.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
@Tag(name = "Media", description = "Endpoints for file upload and management")
public class MediaFileController {

    private final FileService fileService;

    /**
     * POST /api/v1/media
     * Загрузить файл. Возвращает метаданные сохранённого файла.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Upload a file", description = "Upload an image file (jpg, jpeg, png, webp, gif). Max 10MB.")
    public ResponseEntity<ApiResponse<MediaFileDto>> upload(
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal Jwt jwt
    ) throws IOException {
        MediaFile saved = fileService.uploadFile(file, jwt.getSubject());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(toDto(saved), HttpStatus.CREATED.value(), "File uploaded successfully"));
    }

    /**
     * GET /api/v1/media/{id}
     * Скачать файл. Публичный доступ.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Download a file", description = "Retrieve a file by its ID")
    public ResponseEntity<byte[]> download(@PathVariable UUID id) throws IOException {
        FileService.FileDownload result = fileService.downloadFile(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(result.mimeType()));
        headers.setContentDisposition(
                ContentDisposition.inline()
                        .filename(result.originalFilename())
                        .build()
        );

        return ResponseEntity.ok().headers(headers).body(result.data());
    }

    /**
     * DELETE /api/v1/media/{id}
     * Удалить файл с диска и из БД.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a file", description = "Permanently delete a file by its ID (Admin only)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) throws IOException {
        fileService.deleteFile(id);
        return ResponseEntity.ok(new ApiResponse<>(null, HttpStatus.OK.value(), "File deleted successfully"));
    }

    private MediaFileDto toDto(MediaFile mediaFile) {
        MediaFileDto dto = new MediaFileDto();
        dto.setId(mediaFile.getId());
        dto.setCreatedAt(mediaFile.getCreatedAt());
        dto.setUpdatedAt(mediaFile.getUpdatedAt());
        dto.setOriginalFilename(mediaFile.getOriginalFilename());
        dto.setFilename(mediaFile.getFilename());
        dto.setFilePath(mediaFile.getFilePath());
        dto.setFileSize(mediaFile.getFileSize());
        dto.setMimeType(mediaFile.getMimeType());
        return dto;
    }
}
