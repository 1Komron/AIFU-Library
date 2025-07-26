package aifu.project.libraryweb.service;

import aifu.project.common_domain.entity.Librarian;
import aifu.project.libraryweb.entity.SecurityLibrarian;
import aifu.project.libraryweb.repository.LibrarianRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.BeanUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final LibrarianRepository librarianRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Librarian baseLibrarian = librarianRepository
                .findByEmailAndIsDeletedFalse(username)
                .orElseThrow(() -> new UsernameNotFoundException("SecurityLibrarian not found with email: " + username));

        SecurityLibrarian securityLibrarian = new SecurityLibrarian();
        BeanUtils.copyProperties(baseLibrarian, securityLibrarian);

        return securityLibrarian;
    }

}
