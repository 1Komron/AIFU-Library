package aifu.project.libraryweb.service;

import aifu.project.common_domain.entity.Librarian;
import aifu.project.libraryweb.repository.LibrarianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final LibrarianRepository librarianRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Librarian librarian = librarianRepository.findByEmailAndIsDeletedFalse(username)
                .orElseThrow(() -> new UsernameNotFoundException("Librarian not found with email: " + username));

        return new User(
                librarian.getEmail(),
                librarian.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + librarian.getRole()))
        );
    }
}
