package aifu.project.libraryweb.entity;


import aifu.project.common_domain.entity.Librarian;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class SecurityLibrarian extends Librarian implements UserDetails {
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + getRole()));
    }

    @Override
    public String getUsername() {
        return getEmail();
    }

    public Librarian toBase() {
        Librarian base = new Librarian();
        BeanUtils.copyProperties(this, base);
        return base;
    }

}
