package aifu.project.libraryweb.entity;


import org.springframework.beans.BeanUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class SecurityLibrarian extends aifu.project.common_domain.entity.Librarian implements UserDetails {
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + getRole()));
    }

    @Override
    public String getUsername() {
        return getEmail();
    }

    public aifu.project.common_domain.entity.Librarian toBase() {
        aifu.project.common_domain.entity.Librarian base = new aifu.project.common_domain.entity.Librarian();
        BeanUtils.copyProperties(this, base);
        return base;
    }

}
