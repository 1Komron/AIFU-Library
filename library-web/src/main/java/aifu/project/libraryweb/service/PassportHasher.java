package aifu.project.libraryweb.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

@Component
public class PassportHasher {


    private static final Logger log = LoggerFactory.getLogger(PassportHasher.class);

    private final String salt;

    public PassportHasher(@Value("${security.hashing.passport-salt}") String salt) {
        this.salt = salt;
    }

    public String hash(String passportCode) {
        if (passportCode == null || passportCode.isBlank()) {
            return null;

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

    public boolean verify(String plainPassportCode, String storedHash) {
        if (plainPassportCode == null || storedHash == null || plainPassportCode.isBlank() || storedHash.isBlank()) {
            return false;
        }
        try {
            String computedHash = hash(plainPassportCode);
            return storedHash.equals(computedHash);
        } catch (Exception e) {
            log.error("Passport kodini tekshirishda xatolik: {}", plainPassportCode, e);
            return false;
        }
    }


}
