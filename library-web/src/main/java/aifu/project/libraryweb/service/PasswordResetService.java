package aifu.project.libraryweb.service;

import aifu.project.common_domain.dto.PasswordResetConfirmRequest;
import aifu.project.common_domain.entity.Librarian;
import aifu.project.libraryweb.repository.LibrarianRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final LibrarianRepository librarianRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    //Vaqtinchalik in-memry kesh, cod yaratrilgan vaqtini belgilaydi
    private static final Map<String, Map.Entry<String, Long>> resetCodeCache = new ConcurrentHashMap<>();
    private static final long CODE_EXPIRY_TIME = 5 * 60 * 1000; //5 Daqiqa saqlaydi


    /**
     * Parolni tiklash jarayonini boshlaydi: emaildi tekshiradi va tasdiqlash kod yuboradi!!
     */
       public void initiatePasswordReset(String email) {
           if (!librarianRepository.existsByEmail(email)) {
           log.warn("Parolni tiklash uchun email topilmadi: {}", email);
           return;
           }

           // Kod generatsiya qilamiz va keshga vaqt bilan saqlaymiz
           String code = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
           resetCodeCache.put(email, Map.entry(code, System.currentTimeMillis()));

           try {
               emailService.sendConfirmationCode(email, code);
               log.info("Tasdiqlash codi {} emailga yuborildi ", email);
           }catch (Exception e){
               log.error("Email yuborishda xato: {}", email, e);
               throw new RuntimeException("Tasdiqlash kodi yuborishda xatolik yuz berdi");
           }

       }


    /**
     * Parolni tiklashni tasdiqlaydi: kodni tekshiradi va yangi parolni o‘rnatadi.
     */
    @Transactional
    public void confirmPasswordReset(PasswordResetConfirmRequest request) {
        String email = request.getEmail();
        String code = request.getCode();
        String newPassword = request.getNewPassword();

        // 1. Keshdagi kod va vaqtni tekshiramiz
        Map.Entry<String, Long> entry = resetCodeCache.get(email);
        if (entry == null || !entry.getKey().equals(code) ||
                System.currentTimeMillis() - entry.getValue() > CODE_EXPIRY_TIME) {
            resetCodeCache.remove(email); // Muddat o‘tgan kodni o‘chiramiz
            throw new IllegalArgumentException("Tasdiqlash kodi noto‘g‘ri yoki muddati o‘tgan.");
        }

        // 2. Foydalanuvchini topib, yangi parolni xeshlab saqlaymiz
        Librarian user = librarianRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Foydalanuvchi topilmadi: " + email));
        user.setPassword(passwordEncoder.encode(newPassword));
        librarianRepository.save(user);

        // 3. Ishlatilgan kodni keshdan o‘chiramiz
        resetCodeCache.remove(email);
        log.info("Parol {} emaili uchun muvaffaqiyatli yangilandi", email);
    }

       }

