package aifu.project.librarybot.service;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;


@Slf4j
@Service
public class PassportHasher {
    @Value("${security.hashing.passport-salt}")
    private String salt;

    public String hash(String passportCode) {
        if (passportCode == null || passportCode.isBlank()) {
            return "";
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest((salt + passportCode).getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (Exception e) {
            log.error("Passport kodini xeshlashda xatolik: {}", passportCode, e);
            throw new RuntimeException("Passport kodini xeshlashda ichki xatolik", e);
        }
    }
}

