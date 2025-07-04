package aifu.project.libraryweb.controller;

import aifu.project.common_domain.dto.pdf_book_dto.FileUploadResponseDTO;
import aifu.project.common_domain.payload.ResponseMessage;
import aifu.project.libraryweb.service.pdf_book_service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileStorageService fileStorageService;

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseMessage> uploadImage(@RequestPart("file") MultipartFile file)
            throws IOException, InterruptedException {
        String url = fileStorageService.save(file, "image");
        ResponseMessage body = new ResponseMessage(
                true,
                "Rasm muvaffaqiyatli yuklandi",
                Map.of("url",url)
        );
        return ResponseEntity.ok(body);

    }

    // FileUploadController.java
    @PostMapping(value = "/pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseMessage> uploadPdf(@RequestPart("file") MultipartFile file) throws IOException {
        FileUploadResponseDTO response = fileStorageService.saveWithSize(file, "pdf");
        ResponseMessage body = new ResponseMessage(
                true,
                "Pdf muvaffaqiyatli yuklandi",
                response

        );

        return ResponseEntity.ok(body);
    }

}