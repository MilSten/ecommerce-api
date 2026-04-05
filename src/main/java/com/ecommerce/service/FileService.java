package com.ecommerce.service;

import com.ecommerce.entity.MediaFile;
import com.ecommerce.entity.user.User;
import com.ecommerce.exception.InvalidFileException;
import com.ecommerce.repository.MediaFileRepository;
import com.ecommerce.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FileService {

    @Value("${app.file.upload-dir:storage/uploads}")
    private String uploadDir;

    private final MediaFileRepository mediaFileRepository;

    /**
     * Загрузить файл
     */
    public MediaFile uploadFile(MultipartFile file, User uploadedBy) throws IOException {
        log.info("Uploading file: {}", file.getOriginalFilename());

        validateFile(file);

        Path uploadPath = Paths.get(uploadDir);
        Files.createDirectories(uploadPath);

        String filename = UUID.randomUUID() + getFileExtension(file.getOriginalFilename());
        Path filePath = uploadPath.resolve(filename);

        Files.write(filePath, file.getBytes());

        MediaFile mediaFile = new MediaFile();
        mediaFile.setOriginalFilename(file.getOriginalFilename());
        mediaFile.setFilename(filename);
        mediaFile.setFilePath(filePath.toString());
        mediaFile.setFileSize(file.getSize());
        mediaFile.setMimeType(file.getContentType());
        mediaFile.setUploadedBy(uploadedBy);

        MediaFile saved = mediaFileRepository.save(mediaFile);
        log.info("File uploaded successfully: {}", filename);

        return saved;
    }

    /**
     * Получить файл по ID
     */
    public byte[] getFile(UUID id) throws IOException {
        MediaFile mediaFile = mediaFileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));

        return Files.readAllBytes(Paths.get(mediaFile.getFilePath()));
    }

    /**
     * Удалить файл
     */
    public void deleteFile(UUID id) throws IOException {
        MediaFile mediaFile = mediaFileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));

        Files.deleteIfExists(Paths.get(mediaFile.getFilePath()));
        mediaFileRepository.delete(mediaFile);

        log.info("File deleted: {}", mediaFile.getFilename());
    }

    /**
     * Валидация файла
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidFileException("File is empty");
        }

        if (file.getSize() > Constants.Validation.MAX_FILE_SIZE) {
            throw new InvalidFileException("File size exceeds maximum allowed: 10MB");
        }

        String ext = getFileExtension(file.getOriginalFilename()).substring(1).toLowerCase();
        if (!Constants.File.ALLOWED_EXTENSIONS.contains(ext)) {
            throw new InvalidFileException("File type not allowed: " + ext);
        }
    }

    /**
     * Получить расширение файла
     */
    private String getFileExtension(String filename) {
        return filename != null && filename.contains(".")
                ? filename.substring(filename.lastIndexOf("."))
                : "";
    }
}