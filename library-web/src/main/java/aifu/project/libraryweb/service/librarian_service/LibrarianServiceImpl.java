package aifu.project.libraryweb.service.librarian_service;

import aifu.project.common_domain.dto.AdminResponse;
import aifu.project.common_domain.dto.AdminUpdateDTO;
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

import static aifu.project.libraryweb.utils.UpdateUtils.updateIfChanged;

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
                .imageUrl(librarian.getImageUrl())
                .isActive(librarian.isActive())
                .build();

        return ResponseEntity.ok(new ResponseMessage(true, "Data", response));
    }

    @Override
    public ResponseEntity<ResponseMessage> update(AdminUpdateDTO updates) {
        Librarian librarian = updateLibrarian(updates);

        librarian = librarianRepository.save(librarian);

        log.info("Libararian update qilindi. Librarian: {}.", librarian);

        AdminResponse response = AdminResponse.builder()
                .id(librarian.getId())
                .name(librarian.getName())
                .surname(librarian.getSurname())
                .email(librarian.getEmail())
                .role(librarian.getRole().name())
                .imageUrl(librarian.getImageUrl())
                .isActive(librarian.isActive())
                .build();

        return ResponseEntity.ok(new ResponseMessage(true, "Muvaffaqiyatli update qilindi", response));
    }

    private Librarian updateLibrarian(AdminUpdateDTO updates) {
        log.info("Kutubxonachi profile tahrirlash jarayoni...");
        log.info("Update qilinayotgan fieldlar: {}", updates);

        Librarian librarian = getLibrarian();

        log.info("Update qilinayotga kutubxonachi: {}", librarian);

        updateFields(updates, librarian);

        return librarian;
    }

    private void updateFields(AdminUpdateDTO updates, Librarian librarian) {
        updateIfChanged(updates.name(), librarian::getName, librarian::setName);
        updateIfChanged(updates.surname(), librarian::getSurname, librarian::setSurname);
        updateIfChanged(updates.imageUrl(), librarian::getImageUrl, librarian::setImageUrl);
    }

    private static Librarian getLibrarian() {
        SecurityLibrarian securityLibrarian = (SecurityLibrarian) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return securityLibrarian.toBase();
    }
}
