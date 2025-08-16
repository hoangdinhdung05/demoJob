package com.demoJob.demo.util;

import com.demoJob.demo.entity.Role;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.entity.UserHasRole;
import com.demoJob.demo.exception.InvalidDataException;
import com.demoJob.demo.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserRoleUtil {

    private final RoleRepository roleRepository;

    /**
     * Lấy danh sách Role theo roleNames
     */
    public Set<Role> getRoles(Set<String> roleNames) {
        Set<Role> roles = roleRepository.findByNameIn(roleNames)
                .orElseThrow(() -> new InvalidDataException("Invalid role names: " + roleNames));
        if (roles.isEmpty()) {
            throw new InvalidDataException("No valid roles found: " + roleNames);
        }
        return roles;
    }

    /**
     * Gán roles mặc định cho User
     */
    public void assignRolesToUser(User user, Set<Role> roles) {
        Set<UserHasRole> userHasRoles = roles.stream()
                .map(role -> UserHasRole.builder().user(user).role(role).build())
                .collect(Collectors.toSet());
        user.setUserHasRoles(userHasRoles);
    }
}
