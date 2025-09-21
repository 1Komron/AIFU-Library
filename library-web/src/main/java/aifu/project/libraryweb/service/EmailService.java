package aifu.project.libraryweb.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private static final Map<String, Map.Entry<String, Long>> activationCodeCache = new ConcurrentHashMap<>();
    @Getter
    private static final long CODE_EXPIRY_TIME_MS = 300000;

    public void sendConfirmationCode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Your Password Change Confirmation Code");
        message.setText("Hello,\n\nYour confirmation code to change your password is: " + code +
                "\n\nThis code will expire in 10 minutes." +
                "\n\nIf you did not request this, please ignore this email.");
        mailSender.send(message);
    }

    @Async
    public void sendSimpleMessage(String toEmail) {
        String activationCode = createCode();
        String subject = "Kutubxona Tizimida Accountdi faollashtirish codi";
        String text = "Salom! Sizning yangi admin akkauntingizni faollashtirish uchun tasdiqlash kodi: " + activationCode;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
        cacheActivationCode(toEmail, activationCode);
    }

    private String createCode() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
    }

    private void cacheActivationCode(String email, String code) {
        activationCodeCache.put(email, Map.entry(code, System.currentTimeMillis()));
    }

    public Map.Entry<String, Long> getCacheEntry(String email) {
        return activationCodeCache.get(email);
    }

    public void removeCacheEntry(String email) {
        activationCodeCache.remove(email);
    }


}
