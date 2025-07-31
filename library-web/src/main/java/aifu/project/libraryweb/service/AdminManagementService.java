package aifu.project.libraryweb.service;

import aifu.project.common_domain.dto.AdminCreateRequest;
import aifu.project.common_domain.dto.AdminResponse;
import aifu.project.common_domain.entity.Librarian;
import aifu.project.common_domain.entity.enums.Role;
import aifu.project.common_domain.exceptions.EmailAlreadyExistsException;
import aifu.project.libraryweb.repository.LibrarianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor

public class AdminManagementService {

    private final LibrarianRepository librarianRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public AdminResponse createAdmin(AdminCreateRequest request) {

        if(librarianRepository.existsByEmail(request.getEmail())){
            throw new EmailAlreadyExistsException("Email already exists: " + request.getEmail());
        }

        Librarian newAdmin = new Librarian();
        newAdmin.setName(request.getName());
        newAdmin.setSurname(request.getSurname());
        newAdmin.setEmail(request.getEmail());
        newAdmin.setPassword(passwordEncoder.encode(request.getPassword()));
        newAdmin.setRole(Role.ADMIN);
        newAdmin.setDeleted(false);
      //  newAdmin.setActive(false);

        // Noyob, taxmin qilish qiyin bo'lgan faollashtirish kodi yaratamiz
        String code = UUID.randomUUID().toString();
     //   newAdmin.setActivationCode(code);

        //Codning amal qilish muddati 90 soniya
       // newAdmin.setActivationCodeExpiresAt(LocalDateTime.now().plusSeconds(90));
        Librarian savedAdmin = librarianRepository.save(newAdmin);

        //yangi Adminga faollashtirish kodi yuboriladi
        //emailService.sendActivationEmail(savedAdmin.getEmail(), code);
        return AdminResponse.builder()
                .id(savedAdmin.getId())
                .name(savedAdmin.getName())
                .surname(savedAdmin.getSurname())
                .email(savedAdmin.getEmail())
                .role(savedAdmin.getRole().name())
                .build();



    }

}
