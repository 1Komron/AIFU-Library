package aifu.project.libraryweb.config;

import aifu.project.common_domain.entity.Librarian;
import aifu.project.common_domain.entity.enums.Role;
import aifu.project.libraryweb.repository.LibrarianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SuperAdminInitializer implements CommandLineRunner {

  private final LibrarianRepository librarianRepository;
  private final PasswordEncoder passwordEncoder;


  @Value("${superadmin.email}")
  private String email;

  @Value("${superadmin.initial-password}")
  private String initialPassword;

  @Value("${superadmin.name}")
  private String name;

  @Value("${superadmin.surname}")
  private String surname;
  



    @Override
    public void run(String... args) throws Exception {

      if(!librarianRepository.existsByEmail(email)){
        Librarian superAdmin = new Librarian();
        superAdmin.setName(name);
        superAdmin.setSurname(surname);
        superAdmin.setEmail(email);
        superAdmin.setPassword(passwordEncoder.encode(initialPassword));
        superAdmin.setRole(Role.SUPER_ADMIN);
        superAdmin.setDeleted(false);

        librarianRepository.save(superAdmin);

        System.out.println(">>> SUPERADMIN (SUPER_ADMIN) CREATED SUCCESSFULLY! <<<");

      }

    }
}
