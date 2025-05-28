package aifu.project.libraryweb.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    @Value("${file.upload-dir:E:/files}")
    private String uploadDir;

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
        String filename = UUID.randomUUID() + extension;

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

        Path dirPath = Paths.get(uploadDir, subDir);
        Files.createDirectories(dirPath);

        Path filePath = dirPath.resolve(filename);
        file.transferTo(filePath.toFile());

        String url = "/uploads/" + subDir + "/" + filename;
        return url;
    }
}