package aifu.project.libraryweb.service;


import aifu.project.common_domain.dto.AccountActivationRequest;
import aifu.project.common_domain.dto.AdminCreateRequest;
import aifu.project.common_domain.dto.AdminResponse;
import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.common_domain.entity.Librarian;
import aifu.project.common_domain.entity.enums.Role;
import aifu.project.common_domain.exceptions.EmailAlreadyExistsException;
import aifu.project.libraryweb.repository.LibrarianRepository;
import aifu.project.libraryweb.utils.Util;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor

public class AdminManagementService {
    private final LibrarianRepository librarianRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public ResponseEntity<ResponseMessage> createAdmin(AdminCreateRequest request) {
        log.info("yangi admin yaratish jarayoni boshlandi: Request={}", request.toString());

        if (librarianRepository.existsByEmail(request.getEmail())) {
            log.warn("Admin yaratish jarayoni bekor qilindi: bu email bilan admin allaqachon mavjud: email={}", request.getEmail());
            throw new EmailAlreadyExistsException("Email already exists: " + request.getEmail());
        }

        Librarian savedAdmin = AdminCreateRequest.toEntity(request, passwordEncoder.encode(request.getPassword()));
        savedAdmin = librarianRepository.save(savedAdmin);

        log.info("Yangi admin bazaga saqlandi (Inactive): ID={}, Email={}", savedAdmin.getId(), savedAdmin.getEmail());

        emailService.sendSimpleMessage(request.getEmail());
        log.info("Faollashtirish kodi muvaffaqiyatli yuborildi: email={}", request.getEmail());

        AdminResponse response = AdminResponse.toDto(savedAdmin);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new ResponseMessage(true,
                                "Yangi admin muvaffaqiyatli yaratildi va faollashtirish kodi yuborildi!",
                                response)
                );
    }

    @Transactional
    public void activateAccount(AccountActivationRequest request) {
        String email = request.getEmail();
        String code = request.getCode();
        log.info("Akkauntni faollashtirishga urinish: email={}", email);

        Map.Entry<String, Long> entry = emailService.getCacheEntry(email);

        if (entry == null || !entry.getKey().equals(code) || System.currentTimeMillis() - entry.getValue() > EmailService.getCODE_EXPIRY_TIME_MS()) {
            emailService.removeCacheEntry(email);
            log.warn("Faollashtirishda xatolik: Kod notug'ri yoki muddati o'tgan. email={}", email);
            throw new IllegalArgumentException("Faollashtirish kodi notug'ri yoki muddati o'tgan!");
        }

        Librarian user = librarianRepository.findByEmailAndIsActiveFalse(email)
                .orElseThrow(() -> {
                    log.error("Faollashtirishda xatolik: Foydalanuvchi topilmadi yoki allaqachon faol. email={}", email);
                    return new IllegalArgumentException("Foydalanuvchi topilmadi yoki allaqachon faollashtirilgan.");
                });


        user.setActive(true);
        librarianRepository.save(user);
        emailService.removeCacheEntry(email);
        log.info("Akkaunt muvaffaqiyatli faollashtirildi: email={}", email);
    }

    public ResponseEntity<ResponseMessage> getAll(Integer page, Integer size, String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(--page, size, Sort.by(direction, "id"));
        Page<AdminResponse> librarianPage = librarianRepository.findAdmins(pageable);

        List<AdminResponse> content = librarianPage.getContent();
        log.info("Adminlar ro'yxati. Ro'yxat: {}, Hajmi: {}", content, librarianPage.getTotalElements());


        Map<String, Object> data = Util.getPageInfo(librarianPage);
        data.put("data", content);

        return ResponseEntity.ok(new ResponseMessage(true, "Adminlar ro'yxati", data));
    }

    public ResponseEntity<ResponseMessage> deleteAdmin(Long adminId) {
        Librarian adminToDelete = librarianRepository
                .findByID(adminId)
                .orElseThrow(() -> {
                    log.warn("O'chirish uchun Admin topilmadi yoki bu ID SuperAdminga tegishli: id={}", adminId);
                    return new IllegalArgumentException("Berilgan ID bilan Admin topilmadi.");
                });

        adminToDelete.setActive(false);
        adminToDelete.setDeleted(true);

        librarianRepository.save(adminToDelete);

        log.info("Admin muvaffsaqiyatli o'chirildi: id={}, email={}", adminToDelete.getId(), adminToDelete.getEmail());
        return ResponseEntity.ok(
                new ResponseMessage(true, "Admin muvaffiqaytli o'chirildi!", adminId)
        );

    }
}
