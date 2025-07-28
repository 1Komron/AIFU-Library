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

@Service
@RequiredArgsConstructor
public class AdminManagementService {

    private final LibrarianRepository librarianRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminResponse createAdmin(AdminCreateRequest request) {

        if(librarianRepository.existsByEmail(request.getEmail())){
            throw new EmailAlreadyExistsException("Email already exists: " + request.getEmail());
        }

        Librarian admin = new Librarian();
        admin.setName(request.getName());
        admin.setSurname(request.getSurname());
        admin.setEmail(request.getEmail());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setRole(Role.ADMIN);
        admin.setDeleted(false);

        Librarian savedAdmin = librarianRepository.save(admin);
        return AdminResponse.builder()
                .id(savedAdmin.getId())
                .name(savedAdmin.getName())
                .surname(savedAdmin.getSurname())
                .email(savedAdmin.getEmail())
                .role(savedAdmin.getRole().name())
                .build();


    }

}
