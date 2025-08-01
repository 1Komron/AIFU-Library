package aifu.project.libraryweb.service;


import aifu.project.common_domain.dto.AccountActivationRequest;
import aifu.project.common_domain.dto.AdminCreateRequest;
import aifu.project.common_domain.dto.AdminResponse;
import aifu.project.common_domain.entity.Librarian;
import aifu.project.common_domain.entity.enums.Role;
import aifu.project.common_domain.exceptions.EmailAlreadyExistsException;
import aifu.project.libraryweb.repository.LibrarianRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;


/**
    * Bu service SuperAdmin  (Librarian)  tomonidan oddiy adminlardi boshqarish uchun
    * muljallangan barcha beznis operatsiyalarni o'z ichiga oladi.
    * U yangi admin yaratish va ulardi accountlarini boshqarish uchun ma'suldir.
    * @author Alisher
    */

@Slf4j
@Service
@RequiredArgsConstructor

public class AdminManagementService {

    /**
     * Ma'lumotlar bazasidagi "librarians" jadvali bilan ishlash uchun.
     */
    private final LibrarianRepository librarianRepository;

    /**
     * Parollarni xavfsizl  BCrypt algoretimi bilan xeshlash (shefralash) uchun;
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Tashqi dunyoga elektron xatlar yuborish uchun;
     */
    private final EmailService emailService;


    /**
     * Faollashtirish  codalarni vaqtinchalik saqlash uchun keshlash mexanezimi
     * Kalit (key) bu foydalanuvchi emaili
     * Qiymat (value) esa bu Map.Entry bulib uning kalit codining o'zi, qiymat esa kod yaratilgan vaqt
     */
    private static final Map<String, Map.Entry<String, Long>> activationCodeCache = new ConcurrentHashMap<>();

    private static final long CODE_EXPIRY_TIME_MS = 5 * 60 * 1000;

    public AdminResponse createAdmin(AdminCreateRequest request) {
        log.info("yangi admin yaratish jarayoni boshlandi: email={}", request.getEmail());

        if (librarianRepository.existsByEmail(request.getEmail())) {
            log.warn("Admin yaratish jarayoni bekor qilindi: bu email bilan admin allaqachon mavjud: email={}", request.getEmail());
            throw new EmailAlreadyExistsException("Email already exists: " + request.getEmail());
        }

        // 2-YANGI OBYEKT YARATISH: Yangi Librarian (Admin roli bilan) obyektini yaratamiz.
        Librarian newAdmin = new Librarian();
        newAdmin.setName(request.getName());
        newAdmin.setSurname(request.getSurname());
        newAdmin.setEmail(request.getEmail());
        newAdmin.setPassword(passwordEncoder.encode(request.getPassword()));
        newAdmin.setRole(Role.ADMIN);
        newAdmin.setDeleted(false);
        newAdmin.setActive(false);

        // 3-BAZAGA SAQLASH: Tayyor obyektni bazaga saqlaymiz.
        Librarian savedAdmin = librarianRepository.save(newAdmin);
        log.info("Yangi admin (noactive) bazaga saqlandi: id={}, email={}", savedAdmin.getId(), savedAdmin.getEmail());

        // 4-KOD GENERATSIYA QILISH VA YUBORISH
        // 6 xonali tasodifiy raqamni generatsiya qilamiz.
        String activationCode = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));

        // Kodni va u yaratilgan aniq vaqtni keshga joylashtiramiz.
        activationCodeCache.put(request.getEmail(), Map.entry(activationCode, System.currentTimeMillis()));

        //Unversal Email serves orqali code yuborish

        String subject = "Kutubxona Tizimida Accountdi faollashtirish codi";
        String text = "Salom! Sizning yangi admin akkauntingizni faollashtirish uchun tasdiqlash kodi: " + activationCode;
        emailService.sendSimpleMessage(request.getEmail(), subject, text);
        log.info("Faollashtirish kodi muvaffaqiyatli yuborildi: email={}", request.getEmail());

        // 5-NATIJANI QAYTARISH: Controller'ga xavfsiz DTO'ni qaytaramiz.
        return AdminResponse.builder()
                .id(savedAdmin.getId())
                .name(savedAdmin.getName())
                .surname(savedAdmin.getSurname())
                .email(savedAdmin.getEmail())
                .role(savedAdmin.getRole().name())
                .build();

    }


    /**
     * SuperAdmin tomonidan yuborilgan kod bo'yicha Admin akkauntini faollashtiradi.
     * Bu metod ham @Transactional, chunki u bazadagi yozuvni o'zgartiradi.
     * request Faollashtirilishi kerak bo'lgan adminning emaili va tasdiqlash kodini saqlovchi DTO.
     */
    @Transactional
    public void activateAccount(AccountActivationRequest request) {
        String email = request.getEmail();
        String code = request.getCode();
        log.info("Akkauntni faollashtirishga urinish: email={}", email);

        // Keshdan shu emailga tegishli bo'lgan yozuvni (kod va vaqtni) olamiz.
        Map.Entry<String, Long> entry = activationCodeCache.get(email);

        //Kodni vaqtini tekshirish
            if(entry == null || !entry.getKey().equals(code) || System.currentTimeMillis() - entry.getValue() >CODE_EXPIRY_TIME_MS){
                // Agar kod noto'g'ri yoki muddati o'tgan bo'lsa, uni keshdan o'chiramiz
                // va xatolik "otamiz". Bu foydalanuvchiga qayta urinib ko'rishga majbur qiladi.
                activationCodeCache.remove(email);
                log.warn("Faollashtirishda xatolik: cod notug'ri yoki muddati o'tgan. email={}",email);
                throw new IllegalArgumentException("Faollashtirish kodi notug'ri yoki muddati o'tgan!");
            }

        // BAZADAN FOYDALANUVCHINI TOPISH
        // Bizga aynan nofaol bo'lgan foydalanuvchi kerak.
        Librarian user = librarianRepository.findByEmailAndIsActiveFalse(email)
                .orElseThrow(() -> {
                    log.error("Faollashtirishda xatolik: Foydalanuvchi topilmadi yoki allaqachon faol. email={}", email);
                    return new IllegalArgumentException("Foydalanuvchi topilmadi yoki allaqachon faollashtirilgan.");
                });


        // AKKAUNTNI FAOLLASHTIRISH
        user.setActive(true);
        librarianRepository.save(user);

        // ISHLATILGAN KODNI KESHDAN O'CHIRISH
        // Bu kodni qayta ishlatishning oldini oladi.
        activationCodeCache.remove(email);
        log.info("Akkaunt muvaffaqiyatli faollashtirildi: email={}", email);

    }


}
