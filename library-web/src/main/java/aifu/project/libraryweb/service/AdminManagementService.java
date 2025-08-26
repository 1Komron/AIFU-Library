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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;


@Slf4j
@Service
@RequiredArgsConstructor

public class AdminManagementService {


    private final LibrarianRepository librarianRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private static final Map<String, Map.Entry<String, Long>> activationCodeCache = new ConcurrentHashMap<>();
    private static final long CODE_EXPIRY_TIME_MS = 5 * 60 * 1000;

    public ResponseEntity<ResponseMessage> createAdmin(AdminCreateRequest request) {
        log.info("yangi admin yaratish jarayoni boshlandi: email={}", request.getEmail());

        if (librarianRepository.existsByEmail(request.getEmail())) {
            log.warn("Admin yaratish jarayoni bekor qilindi: bu email bilan admin allaqachon mavjud: email={}", request.getEmail());
            throw new EmailAlreadyExistsException("Email already exists: " + request.getEmail());
        }

        Librarian newAdmin = new Librarian();
        newAdmin.setName(request.getName());
        newAdmin.setSurname(request.getSurname());
        newAdmin.setEmail(request.getEmail());
        newAdmin.setPassword(passwordEncoder.encode(request.getPassword()));
        newAdmin.setRole(Role.ADMIN);
        newAdmin.setDeleted(false);
        newAdmin.setActive(false);

        Librarian savedAdmin = librarianRepository.save(newAdmin);
        log.info("Yangi admin (noactive) bazaga saqlandi: id={}, email={}", savedAdmin.getId(), savedAdmin.getEmail());


        String activationCode = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
        activationCodeCache.put(request.getEmail(), Map.entry(activationCode, System.currentTimeMillis()));


        String subject = "Kutubxona Tizimida Accountdi faollashtirish codi";
        String text = "Salom! Sizning yangi admin akkauntingizni faollashtirish uchun tasdiqlash kodi: " + activationCode;
        emailService.sendSimpleMessage(request.getEmail(), subject, text);
        log.info("Faollashtirish kodi muvaffaqiyatli yuborildi: email={}", request.getEmail());

        AdminResponse response = AdminResponse.builder()
                .id(savedAdmin.getId())
                .name(savedAdmin.getName())
                .surname(savedAdmin.getSurname())
                .email(savedAdmin.getEmail())
                .role(savedAdmin.getRole().name())
                .isActive(savedAdmin.isActive())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new ResponseMessage
                                (true,
                                        "Yangi admin muvaffaqiyatli yaratildi va faollashtirish kodi yuborildi!",
                                        response)
                );

    }


    @Transactional
    public void activateAccount(AccountActivationRequest request) {
        String email = request.getEmail();
        String code = request.getCode();
        log.info("Akkauntni faollashtirishga urinish: email={}", email);

        Map.Entry<String, Long> entry = activationCodeCache.get(email);

        if (entry == null || !entry.getKey().equals(code) || System.currentTimeMillis() - entry.getValue() > CODE_EXPIRY_TIME_MS) {
            activationCodeCache.remove(email);
            log.warn("Faollashtirishda xatolik: cod notug'ri yoki muddati o'tgan. email={}", email);
            throw new IllegalArgumentException("Faollashtirish kodi notug'ri yoki muddati o'tgan!");
        }


        Librarian user = librarianRepository.findByEmailAndIsActiveFalse(email)
                .orElseThrow(() -> {
                    log.error("Faollashtirishda xatolik: Foydalanuvchi topilmadi yoki allaqachon faol. email={}", email);
                    return new IllegalArgumentException("Foydalanuvchi topilmadi yoki allaqachon faollashtirilgan.");
                });


        user.setActive(true);
        librarianRepository.save(user);
        activationCodeCache.remove(email);
        log.info("Akkaunt muvaffaqiyatli faollashtirildi: email={}", email);

    }


    public ResponseEntity<ResponseMessage> getAll(Integer page, Integer size, String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(--page, size, Sort.by(direction, "id"));
        Page<Librarian> librarianPage = librarianRepository.findAll(pageable);

        List<Librarian> content = librarianPage.getContent();
        log.info("Adminlar ro'yxati. Ro'yxat: {}, Hajmi: {}", content, librarianPage.getTotalElements());

        List<AdminResponse> list = content.stream()
                .filter(admin -> admin.getRole() == Role.ADMIN)
                .map(admin -> AdminResponse.builder()
                        .id(admin.getId())
                        .name(admin.getName())
                        .surname(admin.getSurname())
                        .email(admin.getEmail())
                        .role(admin.getRole().name())
                        .isActive(admin.isActive())
                        .build())
                .toList();

        Map<String, Object> data = Util.getPageInfo(librarianPage);
        data.put("data", list);

        return ResponseEntity.ok(new ResponseMessage(true, "Adminlar ro'yxati", data));
    }

    public ResponseEntity<ResponseMessage> deleteAdmin(Long adminId) {
        Librarian adminToDelete = librarianRepository
                .findByIdAndRoleAndIsDeletedFalse(adminId, Role.ADMIN)
                .orElseThrow(() -> {
                    log.warn("O'chirish uchun Admin topilmadi yoki bu ID SuperAdminga tegishli: id={}", adminId);
                    return new RuntimeException("Berilgan ID bilan Admin topilmadi.");
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
