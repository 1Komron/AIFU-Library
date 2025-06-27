package aifu.project.libraryweb.service.pdf_book_service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

//@Service
//@RequiredArgsConstructor
//public class CloudinaryService {
//
//    private final Cloudinary cloudinary;
//
//    public String uploadFile(MultipartFile file, String folder) throws IOException {
//        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
//                ObjectUtils.asMap(
//                        "folder", folder,
//                        "resource_type", "auto"
//                ));
//        return uploadResult.get("secure_url").toString();
//    }
//}
@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String uploadFile(MultipartFile file, String folder) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", folder,
                        "resource_type", "raw", // <-- bu muhim
                        "use_filename", true,
                        "unique_filename", true
                )
        );
        return uploadResult.get("secure_url").toString();
    }
}
