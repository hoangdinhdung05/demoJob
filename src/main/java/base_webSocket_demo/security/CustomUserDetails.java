package base_webSocket_demo.security;

import base_webSocket_demo.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Set;

@Getter
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final User user;
    private final Set<? extends GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword() != null ? user.getPassword() : "";
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // hoặc user.isExpired() == false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // hoặc user.isLocked() == false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // hoặc user.isCredentialExpired() == false;
    }

    @Override
    public boolean isEnabled() {
        return true; // hoặc user.isActive();
    }

    public Long getId() {
        return user.getId();
    }
}
