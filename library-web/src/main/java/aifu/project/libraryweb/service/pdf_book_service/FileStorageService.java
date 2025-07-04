package aifu.project.libraryweb.service.pdf_book_service;

import aifu.project.common_domain.dto.pdf_book_dto.FileUploadResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final CloudinaryService cloudinaryService;

    private static final Set<String> ALLOWED_IMAGE_TYPES = new HashSet<>(Arrays.asList(
            "image/jpeg", "image/pjpeg", "image/jfif", "image/pjp",
            "image/png", "image/gif", "image/bmp", "image/tiff",
            "image/webp", "image/heic", "image/heif",
            "image/svg+xml", "image/x-icon"
    ));

    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = new HashSet<>(Arrays.asList(
            ".jpg", ".jpeg", ".jfif", ".pjpeg", ".pjp",
            ".png", ".gif", ".bmp", ".tif", ".tiff",
            ".webp", ".heic", ".heif",
            ".svg", ".ico"
    ));

    public String save(MultipartFile file, String subDir) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Fayl bo'sh yoki null");
        }

        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase()
                : "";

        if (subDir.equals("pdf")) {
            if (!"application/pdf".equals(contentType)) {
                throw new IllegalArgumentException("Faqat PDF fayllar ruxsat etiladi");
            }
            if (file.getSize() > 100 * 1024 * 1024) {
                throw new IllegalArgumentException("PDF fayl hajmi 100MB dan katta bo‘lmasligi kerak!");
            }
        }

        if (subDir.equals("image")) {
            if (file.getSize() > 50 * 1024 * 1024) {
                throw new IllegalArgumentException("Rasm fayl hajmi 50MB dan katta bo‘lmasligi kerak!");
            }

            boolean isValidType = contentType != null && ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase());
            if (!isValidType && contentType != null && contentType.equalsIgnoreCase("application/octet-stream")) {
                isValidType = ALLOWED_IMAGE_EXTENSIONS.contains(extension);
            }
            if (!isValidType) {
                throw new IllegalArgumentException("Faqat rasm fayllar ruxsat etiladi: JPEG, PNG, GIF, BMP, TIFF, WEBP, HEIC, SVG, ICO");
            }

            if (!ALLOWED_IMAGE_EXTENSIONS.contains(extension.toLowerCase())) {
                throw new IllegalArgumentException("Faqat quyidagi rasm formatlari ruxsat etiladi: " +
                        ".jpg, .jpeg, .jfif, .pjpeg, .pjp, .png, .gif, .bmp, .tif, .tiff, .webp, .heic, .heif, .svg, .ico");
            }
        }

        // Faylni Cloudinary'ga yuklaymiz
        return cloudinaryService.uploadFile(file, subDir);
    }

    public FileUploadResponseDTO saveWithSize(MultipartFile file, String subDir) throws IOException {
        String url = save(file, subDir);
        double sizeMb = file.getSize() / 1024.0 / 1024.0;
        double result = Math.round(sizeMb * 100.0) / 100.0;

        return new FileUploadResponseDTO(url, result);
    }
}