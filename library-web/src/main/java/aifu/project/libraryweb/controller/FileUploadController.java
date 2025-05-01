package aifu.project.libraryweb.controller;

import aifu.project.libraryweb.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileStorageService fileStorageService;

    @PostMapping("/pdf")
    public ResponseEntity<Map<String, String>> uploadPdf(@RequestParam("file") MultipartFile file)
            throws IOException {
        String url = fileStorageService.save(file, "pdf");
        return ResponseEntity.ok(Map.of("url", url));
    }

    @PostMapping("/image")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file)
            throws IOException {
        String url = fileStorageService.save(file, "image");
        return ResponseEntity.ok(Map.of("url", url));
    }
}
