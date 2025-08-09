package aifu.project.libraryweb.service.pdf_book_service;

import aifu.project.common_domain.dto.pdf_book_dto.FileUploadResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final CloudinaryService cloudinaryService;

    // ===== Constants =====
    private static final long MAX_IMAGE_SIZE_MB = 50;
    private static final long MAX_PDF_SIZE_MB = 100;
    private static final long MAX_EBOOK_SIZE_MB = 200;

    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Collections.unmodifiableSet(new HashSet<>(Set.of(
            ".jpg", ".jpeg", ".jfif", ".pjpeg", ".pjp",
            ".png", ".gif", ".bmp", ".tif", ".tiff",
            ".webp", ".heic", ".heif",
            ".svg", ".ico"
    )));

    private static final Set<String> ALLOWED_IMAGE_TYPES = Collections.unmodifiableSet(new HashSet<>(Set.of(
            "image/jpeg", "image/pjpeg", "image/jfif", "image/pjp",
            "image/png", "image/gif", "image/bmp", "image/tiff",
            "image/webp", "image/heic", "image/heif",
            "image/svg+xml", "image/x-icon"
    )));

    private static final Set<String> ALLOWED_EBOOK_FORMATS = Collections.unmodifiableSet(new HashSet<>(Set.of(
            "pdf", "epub", "mobi", "azw3", "fb2", "djvu", "txt", "docx", "doc",
            "rtf", "odt", "html", "htm"
    )));

    public String save(MultipartFile file, String subDir) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Fayl bo'sh yoki null bo‘lishi mumkin emas");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = (originalFilename != null && originalFilename.contains("."))
                ? originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase()
                : "";

        String contentType = file.getContentType();

        switch (subDir.toLowerCase()) {
            case "pdf" -> validatePdfOrWord(file, contentType, extension, originalFilename);
            case "image" -> validateImage(file, contentType, extension, originalFilename);
            case "ebook" -> validateEbook(file, extension, originalFilename);
            default -> throw new IllegalArgumentException("Noto‘g‘ri katalog turi: " + subDir);
        }

        return cloudinaryService.uploadFile(file, subDir);
    }

    // PDF va Word formatlarini birga tekshirish
    private void validatePdfOrWord(MultipartFile file, String contentType, String extension, String filename) {
        checkSize(file, MAX_PDF_SIZE_MB, filename);

        boolean isPdf = "application/pdf".equalsIgnoreCase(contentType) || extension.equals(".pdf");
        boolean isDocx = "application/vnd.openxmlformats-officedocument.wordprocessingml.document".equalsIgnoreCase(contentType) || extension.equals(".docx");
        boolean isDoc = "application/msword".equalsIgnoreCase(contentType) || extension.equals(".doc");

        if (!(isPdf || isDocx || isDoc)) {
            throw new IllegalArgumentException(filename + " — faqat PDF yoki Word (doc/docx) fayllar ruxsat etiladi");
        }
    }

    private void validateImage(MultipartFile file, String contentType, String extension, String filename) {
        checkSize(file, MAX_IMAGE_SIZE_MB, filename);

        boolean isValidType = contentType != null && ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase());
        if (!isValidType && "application/octet-stream".equalsIgnoreCase(contentType)) {
            isValidType = ALLOWED_IMAGE_EXTENSIONS.contains(extension);
        }

        if (!isValidType) {
            throw new IllegalArgumentException(filename + " — ruxsat etilgan rasm turlari: " + ALLOWED_IMAGE_TYPES);
        }

        if (!ALLOWED_IMAGE_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException(filename + " — ruxsat etilgan rasm formatlari: " + ALLOWED_IMAGE_EXTENSIONS);
        }
    }

    private void validateEbook(MultipartFile file, String extension, String filename) {
        checkSize(file, MAX_EBOOK_SIZE_MB, filename);

        if (!ALLOWED_EBOOK_FORMATS.contains(extension.replace(".", ""))) {
            throw new IllegalArgumentException(filename + " — ruxsat etilgan elektron kitob formatlari: " + ALLOWED_EBOOK_FORMATS);
        }
    }

    private void checkSize(MultipartFile file, long maxMb, String filename) {
        if (file.getSize() > maxMb * 1024 * 1024) {
            throw new IllegalArgumentException(filename + " hajmi " + maxMb + "MB dan oshmasligi kerak!");
        }
    }

    public FileUploadResponseDTO saveWithSize(MultipartFile file, String subDir) throws IOException {
        String url = save(file, subDir);
        double sizeMb = Math.round((file.getSize() / 1024.0 / 1024.0) * 100.0) / 100.0;
        return new FileUploadResponseDTO(url, sizeMb);
    }
}
