package com.ecommerce.service;

import com.ecommerce.entity.MediaFile;
import com.ecommerce.exception.InvalidFileException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.MediaFileRepository;
import com.ecommerce.repository.UserRepository;
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
    private final UserRepository userRepository;

    public record FileDownload(byte[] data, String mimeType, String originalFilename) {}

    /**
     * Загрузить файл. Текущий пользователь определяется по email из JWT.
     */
    public MediaFile uploadFile(MultipartFile file, String uploaderEmail) throws IOException {
        log.info("Uploading file: {} by {}", file.getOriginalFilename(), uploaderEmail);

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
        userRepository.findByEmail(uploaderEmail).ifPresent(mediaFile::setUploadedBy);

        MediaFile saved = mediaFileRepository.save(mediaFile);
        log.info("File uploaded successfully: {} (id={})", filename, saved.getId());

        return saved;
    }

    /**
     * Скачать файл по ID — возвращает байты вместе с метаданными для заголовков ответа.
     */
    @Transactional(readOnly = true)
    public FileDownload downloadFile(UUID id) throws IOException {
        MediaFile mediaFile = findById(id);
        byte[] data = Files.readAllBytes(Paths.get(mediaFile.getFilePath()));
        return new FileDownload(data, mediaFile.getMimeType(), mediaFile.getOriginalFilename());
    }

    /**
     * Получить метаданные файла без чтения с диска.
     */
    @Transactional(readOnly = true)
    public MediaFile findById(UUID id) {
        return mediaFileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("File not found with id: " + id));
    }

    /**
     * Удалить файл с диска и из БД.
     */
    public void deleteFile(UUID id) throws IOException {
        MediaFile mediaFile = findById(id);
        Files.deleteIfExists(Paths.get(mediaFile.getFilePath()));
        mediaFileRepository.delete(mediaFile);
        log.info("File deleted: {}", mediaFile.getFilename());
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidFileException("File is empty");
        }
        if (file.getSize() > Constants.Validation.MAX_FILE_SIZE) {
            throw new InvalidFileException("File size exceeds maximum allowed: 10MB");
        }
        String ext = getFileExtension(file.getOriginalFilename()).replace(".", "").toLowerCase();
        if (!Constants.File.ALLOWED_EXTENSIONS.contains(ext)) {
            throw new InvalidFileException("File type not allowed: " + ext +
                    ". Allowed: " + Constants.File.ALLOWED_EXTENSIONS);
        }
    }

    private String getFileExtension(String filename) {
        return filename != null && filename.contains(".")
                ? filename.substring(filename.lastIndexOf("."))
                : "";
    }
}
