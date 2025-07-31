/*package aifu.project.libraryweb.service;

import aifu.project.common_domain.dto.PasswordChangeRequest;
import aifu.project.common_domain.entity.Librarian;
import aifu.project.libraryweb.repository.LibrarianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class PasswordManagementService {

    private final LibrarianRepository librarianRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // --- WARNING: This is a temporary in-memory cache. ---
    // In a production environment with multiple server instances, this will NOT work.
    // Replace this with a distributed cache like Redis.
    private static final Map<String, String> confirmationCodeCache = new ConcurrentHashMap<>();
    private static final Map<String, String> newPasswordCache = new ConcurrentHashMap<>();

    /**
     * Step 1 of password change: Verifies the old password and sends a confirmation code.
     *
     * @param userEmail The email of the currently logged-in user.
     * @param request   Contains the old and new password.
     *//*
    public void initiatePasswordChange(String userEmail, PasswordChangeRequest request) {
       Librarian admin = librarianRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));

        // Verify that the provided old password matches the stored hash.
        if (!passwordEncoder.matches(request.getOldPassword(), admin.getPassword())) {
            throw new IllegalArgumentException("The old password you entered is incorrect.");
        }

        String code = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));

        // Temporarily store the code and the new password, associated with the user's email.
        // In a real app, you would add an expiration time (e.g., 10 minutes).
        confirmationCodeCache.put(userEmail, code);
        newPasswordCache.put(userEmail, request.getNewPassword());

        // Send the code to the user's registered email.
        emailService.sendConfirmationCode(userEmail, code);
    }

    /**
     * Step 2 of password change: Confirms the code and updates the password.
     *
     * @param userEmail        The email of the currently logged-in user.
     * @param confirmationCode The 6-digit code sent to the user's email.
     *//*
    @Transactional
    public void confirmPasswordChange(String userEmail, String confirmationCode) {
        String correctCode = confirmationCodeCache.get(userEmail);
        String newPassword = newPasswordCache.get(userEmail);

        // Verify the confirmation code.
        if (correctCode == null || newPassword == null || !correctCode.equals(confirmationCode)) {
            throw new IllegalArgumentException("The confirmation code is invalid or has expired.");
        }

        Librarian admin = librarianRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));

        // Hash the new password and save it to the database.
        admin.setPassword(passwordEncoder.encode(newPassword));
        librarianRepository.save(admin);

        // Clean up the cache after successful update.
        confirmationCodeCache.remove(userEmail);
        newPasswordCache.remove(userEmail);
    }
}*/