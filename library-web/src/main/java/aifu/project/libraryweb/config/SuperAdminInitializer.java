package aifu.project.libraryweb.config;

import aifu.project.common_domain.entity.Librarian;
import aifu.project.common_domain.entity.enums.Role;
import aifu.project.libraryweb.repository.LibrarianRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
@Slf4j
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
    if (!librarianRepository.existsByRole(Role.SUPER_ADMIN)) {

      Librarian superAdmin = new Librarian();
      superAdmin.setName(name);
      superAdmin.setSurname(surname);
      superAdmin.setEmail(email);
      superAdmin.setPassword(passwordEncoder.encode(initialPassword));
      superAdmin.setRole(Role.SUPER_ADMIN);
      superAdmin.setDeleted(false);
      librarianRepository.save(superAdmin);
      log.info("SuperAdmin email bilan muvaffaqiyatli yaratildi {} " , email);
    } else {
      log.info("Tizimda allaqachon SuperAdmin bor, yangi superAdmin yaratilmadi !!");

    }

  }
}

