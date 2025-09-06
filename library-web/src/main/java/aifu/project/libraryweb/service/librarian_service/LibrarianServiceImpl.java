package aifu.project.libraryweb.service.librarian_service;

import aifu.project.common_domain.dto.AdminResponse;
import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.common_domain.entity.Librarian;
import aifu.project.common_domain.exceptions.LoginBadCredentialsException;
import aifu.project.libraryweb.entity.SecurityLibrarian;
import aifu.project.libraryweb.repository.LibrarianRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LibrarianServiceImpl implements LibrarianService {
    private final LibrarianRepository librarianRepository;

    @Override
    public ResponseEntity<ResponseMessage> profile() {
        Librarian librarian = getLibrarian();

        if (librarian == null) {
            throw new LoginBadCredentialsException("No user is logged in");
        }

        AdminResponse response = AdminResponse.builder()
                .id(librarian.getId())
                .name(librarian.getName())
                .surname(librarian.getSurname())
                .email(librarian.getEmail())
                .role(librarian.getRole().name())
                .isActive(librarian.isActive())
                .build();

        return ResponseEntity.ok(new ResponseMessage(true, "Data", response));
    }

    @Override
    public ResponseEntity<ResponseMessage> update(Map<String, Object> updates) {
        Librarian librarian = updateLibrarian(updates);

        librarian = librarianRepository.save(librarian);

        log.info("Libararian update qilindi. Librarian: {}. Update qilingan fieldlar: {}", librarian, updates.keySet());

        AdminResponse response = AdminResponse.builder()
                .id(librarian.getId())
                .name(librarian.getName())
                .surname(librarian.getSurname())
                .email(librarian.getEmail())
                .role(librarian.getRole().name())
                .isActive(librarian.isActive())
                .build();

        return ResponseEntity.ok(new ResponseMessage(true, "Muvaffaqiyatli update qilindi", response));
    }

    private static Librarian updateLibrarian(Map<String, Object> updates) {
        log.info("Kutubxonachi profile tahrirlash jarayoni...");
        log.info("Update qilinayotgan fieldlar: {}", updates.keySet());

        Librarian librarian = getLibrarian();

        log.info("Update qilinayotga kutubxonachi: {}", librarian);

        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (key == null) {
                throw new IllegalArgumentException("Key Null");
            }

            if (value == null) {
                throw new IllegalArgumentException("Value Null");
            }

            switch (key) {
                case "name" -> librarian.setName((String) value);
                case "surname" -> librarian.setSurname((String) value);
                case "image" -> librarian.setImageUrl((String) value);
                default -> throw new IllegalArgumentException("Noma'lum field: " + key);
            }
        }
        return librarian;
    }

    private static Librarian getLibrarian() {
        SecurityLibrarian securityLibrarian = (SecurityLibrarian) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return securityLibrarian.toBase();
    }
}
