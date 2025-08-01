package com.demoJob.demo.security;

import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.entity.UserHasRole;
import com.demoJob.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;

//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
//
//        if (user.getUsername() == null || user.getUsername().isEmpty()) {
//            throw new IllegalArgumentException("Username cannot be null or empty");
//        }
//
//        String password = (user.getPassword() != null) ? user.getPassword() : "";
//
//        Set<GrantedAuthority> authorities = user.getUserHasRoles() != null ?
//                user.getUserHasRoles().stream()
//                        .map(UserHasRole::getRole)
//                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().toUpperCase()))
//                        .collect(Collectors.toSet()) : Set.of();
//
//        if (authorities.isEmpty()) {
//            // Gán quyền mặc định nếu cần thiết, hoặc throw exception nếu hệ thống yêu cầu
//            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
//        }
//
//        return new org.springframework.security.core.userdetails.User(
//                user.getUsername(),
//                password,
//                authorities
//        );
//    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        Set<GrantedAuthority> authorities = user.getUserHasRoles() != null ?
                user.getUserHasRoles().stream()
                        .map(UserHasRole::getRole)
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().toUpperCase()))
                        .collect(Collectors.toSet()) : Set.of();

        if (authorities.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return new CustomUserDetails(user, authorities);
    }


}
