package aifu.project.libraryweb.service.pdf_book_service;

import aifu.project.common_domain.dto.pdf_book_dto.FileUploadResponseDTO;
import aifu.project.common_domain.exceptions.FileUploadException;
import aifu.project.common_domain.exceptions.FileValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Fayllarni (rasm, PDF, elektron kitob) validatsiyadan o'tkazish va tashqi
 * xotiraga (masalan, Cloudinary) yuklash uchun mas'ul bo'lgan servis.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final CloudinaryService cloudinaryService;

    // Fayl turlari va o'lchamlari uchun konstantalar
    private static final long MAX_IMAGE_SIZE_MB = 50;
    private static final long MAX_PDF_SIZE_MB = 100;
    private static final long MAX_EBOOK_SIZE_MB = 200;

    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of(
            ".jpg", ".jpeg", ".jfif", ".pjpeg", ".pjp", ".png", ".gif", ".webp", ".svg", ".ico");
    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg", "image/pjpeg", "image/png", "image/gif", "image/webp", "image/svg+xml");
    private static final Set<String> ALLOWED_EBOOK_FORMATS = Set.of(
            "pdf", "epub", "mobi", "azw3", "fb2", "djvu", "txt", "docx", "doc");

    /**
     * Faylni validatsiyadan o'tkazadi va Cloudinary'ga yuklaydi.
     *
     * @param file   Yuklanadigan fayl {@link MultipartFile}.
     * @param subDir Fayl saqlanadigan papka nomi ("image", "pdf", "ebook").
     * @return Yuklangan faylning URL manzili.
     * @throws FileValidationException Agar fayl validatsiyadan o'tmasa (null, bo'sh, noto'g'ri format, katta hajm).
     * @throws FileUploadException     Agar faylni Cloudinary'ga yuklashda xatolik yuz bersa.
     */
    public String save(MultipartFile file, String subDir) {
        log.info("Attempting to save file '{}' to directory '{}'", file.getOriginalFilename(), subDir);

        validateFile(file, subDir);

        try {
            String url = cloudinaryService.uploadFile(file, subDir);
            log.info("File '{}' successfully uploaded to Cloudinary. URL: {}", file.getOriginalFilename(), url);
            return url;
        } catch (IOException e) {
            log.error("Failed to upload file '{}' to Cloudinary.", file.getOriginalFilename(), e);
            throw new FileUploadException("Could not upload file: " + file.getOriginalFilename(), e);
        }
    }

    /**
     * Faylni yuklaydi va javob sifatida uning URL manzilini hamda hajmini (MB) qaytaradi.
     *
     * @param file   Yuklanadigan fayl {@link MultipartFile}.
     * @param subDir Fayl saqlanadigan papka nomi ("image", "pdf", "ebook").
     * @return Fayl URL'i va hajmini o'z ichiga olgan {@link FileUploadResponseDTO}.
     * @throws FileValidationException Agar fayl validatsiyadan o'tmasa.
     * @throws FileUploadException     Agar faylni Cloudinary'ga yuklashda xatolik yuz bersa.
     */
    public FileUploadResponseDTO saveWithSize(MultipartFile file, String subDir) {
        // 'save' metodi barcha validatsiya va yuklash ishini bajaradi.
        String url = save(file, subDir);
        double sizeMb = Math.round((file.getSize() / 1024.0 / 1024.0) * 100.0) / 100.0;
        return new FileUploadResponseDTO(url, sizeMb);
    }

    private void validateFile(MultipartFile file, String subDir) {
        if (file == null || file.isEmpty()) {
            throw new FileValidationException("File cannot be null or empty.");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new FileValidationException("File name cannot be empty.");
        }

        String extension = (originalFilename.contains("."))
                ? originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase()
                : "";
        String contentType = file.getContentType();

        switch (subDir.toLowerCase()) {
            case "pdf" -> validatePdfOrWord(file, contentType, extension, originalFilename);
            case "image" -> validateImage(file, contentType, extension, originalFilename);
            case "ebook" -> validateEbook(file, extension, originalFilename);
            default -> throw new FileValidationException("Invalid directory type specified: " + subDir);
        }
    }

    private void validatePdfOrWord(MultipartFile file, String contentType, String extension, String filename) {
        checkSize(file, MAX_PDF_SIZE_MB, filename);
        boolean isPdf = ".pdf".equals(extension);
        boolean isDocx = ".docx".equals(extension);
        boolean isDoc = ".doc".equals(extension);

        if (!(isPdf || isDocx || isDoc)) {
            throw new FileValidationException("Only PDF or Word (doc/docx) files are allowed. Invalid file: " + filename);
        }
    }

    private void validateImage(MultipartFile file, String contentType, String extension, String filename) {
        checkSize(file, MAX_IMAGE_SIZE_MB, filename);
        if (!ALLOWED_IMAGE_EXTENSIONS.contains(extension) || (contentType != null && !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase()))) {
            // Agar content type noaniq bo'lsa (octet-stream), kengaytmaga ishonamiz. Agar kengaytma ham noto'g'ri bo'lsa xato beramiz.
            if (!ALLOWED_IMAGE_EXTENSIONS.contains(extension)) {
                throw new FileValidationException("Invalid image format. Allowed formats: " + ALLOWED_IMAGE_EXTENSIONS);
            }
        }
    }

    private void validateEbook(MultipartFile file, String extension, String filename) {
        checkSize(file, MAX_EBOOK_SIZE_MB, filename);
        if (!ALLOWED_EBOOK_FORMATS.contains(extension.replace(".", ""))) {
            throw new FileValidationException("Invalid ebook format. Allowed formats: " + ALLOWED_EBOOK_FORMATS);
        }
    }

    private void checkSize(MultipartFile file, long maxMb, String filename) {
        if (file.getSize() > maxMb * 1024 * 1024) {
            throw new FileValidationException(String.format("File '%s' size cannot exceed %dMB. Current size: %.2fMB",
                    filename, maxMb, (file.getSize() / 1024.0 / 1024.0)));
        }
    }
}