package com.ecommerce.service;

import com.ecommerce.entity.MediaFile;
import com.ecommerce.exception.InvalidFileException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.MediaFileRepository;
import com.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// @ExtendWith(MockitoExtension.class) — подключаем Mockito к JUnit 5.
// Это позволяет использовать @Mock и @InjectMocks без запуска Spring-контекста.
// Тесты запускаются за миллисекунды, потому что нет ни БД, ни HTTP-сервера.
@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    // @Mock создаёт «фиктивный» объект репозитория.
    // Все его методы по умолчанию возвращают null / пустые Optional / 0 —
    // настоящей БД нет, и это именно то, что нам нужно для изоляции теста.
    @Mock
    private MediaFileRepository mediaFileRepository;

    @Mock
    private UserRepository userRepository;

    // @InjectMocks создаёт настоящий экземпляр FileService и автоматически
    // внедряет в него все поля, помеченные @Mock выше.
    @InjectMocks
    private FileService fileService;

    // @TempDir — JUnit 5 создаёт уникальную временную папку перед каждым тестом
    // и удаляет её после. Мы работаем с реальными файлами, но не засоряем систему.
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        // @Value("${app.file.upload-dir}") не срабатывает без Spring-контекста,
        // поэтому вручную записываем значение в приватное поле через рефлексию.
        ReflectionTestUtils.setField(fileService, "uploadDir", tempDir.toString());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // uploadFile
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("uploadFile: валидный PNG-файл сохраняется на диск и в БД")
    void uploadFile_validPng_savesFileToDiskAndDatabase() throws IOException {
        // Arrange — MockMultipartFile имитирует файл из HTTP-запроса
        MockMultipartFile file = new MockMultipartFile(
                "file",            // имя поля формы
                "photo.png",       // оригинальное имя файла
                "image/png",       // MIME-тип
                new byte[]{1, 2, 3} // содержимое
        );

        // Настраиваем мок: когда сервис вызовет save(), вернём объект с ID
        MediaFile savedMediaFile = new MediaFile();
        savedMediaFile.setId(UUID.randomUUID());
        savedMediaFile.setFilename("generated-name.png");
        when(mediaFileRepository.save(any(MediaFile.class))).thenReturn(savedMediaFile);

        // Пользователь по email не найден — это нормально, поле uploadedBy останется null
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

        // Act
        MediaFile result = fileService.uploadFile(file, "user@example.com");

        // Assert — проверяем, что вернулся объект с нужным ID
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(savedMediaFile.getId());

        // Проверяем, что save() был вызван ровно один раз
        verify(mediaFileRepository, times(1)).save(any(MediaFile.class));
    }

    @Test
    @DisplayName("uploadFile: пустой файл → InvalidFileException")
    void uploadFile_emptyFile_throwsInvalidFileException() {
        // Файл с нулевым содержимым — isEmpty() вернёт true
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file", "empty.png", "image/png", new byte[0]
        );

        // assertThatThrownBy — читаемый способ проверить, что выброшено нужное исключение
        assertThatThrownBy(() -> fileService.uploadFile(emptyFile, "user@example.com"))
                .isInstanceOf(InvalidFileException.class)
                .hasMessageContaining("empty");

        // Если валидация провалилась — до БД не должны доходить
        verifyNoInteractions(mediaFileRepository);
    }

    @Test
    @DisplayName("uploadFile: файл > 10MB → InvalidFileException")
    void uploadFile_fileTooLarge_throwsInvalidFileException() {
        // Создаём массив байт на 1 байт больше лимита в 10MB
        byte[] bigContent = new byte[10 * 1024 * 1024 + 1];
        MockMultipartFile bigFile = new MockMultipartFile(
                "file", "heavy.png", "image/png", bigContent
        );

        assertThatThrownBy(() -> fileService.uploadFile(bigFile, "user@example.com"))
                .isInstanceOf(InvalidFileException.class)
                .hasMessageContaining("exceeds");

        verifyNoInteractions(mediaFileRepository);
    }

    @Test
    @DisplayName("uploadFile: недопустимое расширение (.exe) → InvalidFileException")
    void uploadFile_invalidExtension_throwsInvalidFileException() {
        MockMultipartFile exeFile = new MockMultipartFile(
                "file", "virus.exe", "application/octet-stream", new byte[]{1, 2, 3}
        );

        assertThatThrownBy(() -> fileService.uploadFile(exeFile, "user@example.com"))
                .isInstanceOf(InvalidFileException.class)
                .hasMessageContaining("not allowed");

        verifyNoInteractions(mediaFileRepository);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // downloadFile
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("downloadFile: существующий файл → возвращает байты и метаданные")
    void downloadFile_existingFile_returnsFileDownloadRecord() throws IOException {
        // Создаём реальный файл во временной директории
        byte[] fileContent = "hello world".getBytes();
        Path filePath = tempDir.resolve("test.png");
        Files.write(filePath, fileContent);

        // Настраиваем сущность, которую вернёт репозиторий
        MediaFile mediaFile = new MediaFile();
        mediaFile.setId(UUID.randomUUID());
        mediaFile.setFilePath(filePath.toString());  // указываем реальный путь
        mediaFile.setMimeType("image/png");
        mediaFile.setOriginalFilename("test.png");

        when(mediaFileRepository.findById(mediaFile.getId())).thenReturn(Optional.of(mediaFile));

        // Act
        FileService.FileDownload download = fileService.downloadFile(mediaFile.getId());

        // Assert — record содержит байты и корректные метаданные для HTTP-заголовков
        assertThat(download.data()).isEqualTo(fileContent);
        assertThat(download.mimeType()).isEqualTo("image/png");
        assertThat(download.originalFilename()).isEqualTo("test.png");
    }

    @Test
    @DisplayName("downloadFile: файл не найден в БД → ResourceNotFoundException")
    void downloadFile_notFound_throwsResourceNotFoundException() {
        UUID unknownId = UUID.randomUUID();
        when(mediaFileRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fileService.downloadFile(unknownId))
                .isInstanceOf(ResourceNotFoundException.class)
                // Сообщение должно содержать ID, чтобы клиент понял, чего не нашли
                .hasMessageContaining(unknownId.toString());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // deleteFile
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("deleteFile: удаляет файл с диска и вызывает delete() в репозитории")
    void deleteFile_existingFile_deletesFromDiskAndDatabase() throws IOException {
        // Создаём реальный файл, который должен исчезнуть после удаления
        Path filePath = tempDir.resolve("to-delete.png");
        Files.write(filePath, new byte[]{1, 2, 3});
        assertThat(filePath).exists(); // убеждаемся, что файл создан

        MediaFile mediaFile = new MediaFile();
        mediaFile.setId(UUID.randomUUID());
        mediaFile.setFilePath(filePath.toString());
        mediaFile.setFilename("to-delete.png");

        when(mediaFileRepository.findById(mediaFile.getId())).thenReturn(Optional.of(mediaFile));

        // Act
        fileService.deleteFile(mediaFile.getId());

        // Файл физически удалён с диска
        assertThat(filePath).doesNotExist();

        // delete() вызван с правильным объектом
        verify(mediaFileRepository).delete(mediaFile);
    }

    @Test
    @DisplayName("deleteFile: файл не найден в БД → ResourceNotFoundException, delete() не вызывается")
    void deleteFile_notFound_throwsResourceNotFoundException() {
        UUID unknownId = UUID.randomUUID();
        when(mediaFileRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fileService.deleteFile(unknownId))
                .isInstanceOf(ResourceNotFoundException.class);

        // Важно: delete() не должен вызываться, если запись не нашли
        verify(mediaFileRepository, never()).delete(any());
    }
}
