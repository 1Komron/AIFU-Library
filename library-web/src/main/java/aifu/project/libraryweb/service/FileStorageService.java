package aifu.project.libraryweb.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * Saves the given file under uploadDir/subDir and returns the public URL.
     */
    public String save(MultipartFile file, String subDir) throws IOException {
        // build directories
        Path dirPath = Paths.get(uploadDir, subDir);
        Files.createDirectories(dirPath);

        // unique filename
        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = dirPath.resolve(filename);

        // save to disk
        file.transferTo(filePath.toFile());

        // return URL that your frontend can GET
        return "/uploads/" + subDir + "/" + filename;
    }
}
