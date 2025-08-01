package com.demoJob.demo.helper;

import com.demoJob.demo.dto.request.Admin.RoleRequest;
import com.demoJob.demo.dto.request.RegisterRequest;
import com.demoJob.demo.service.RoleService;
import com.demoJob.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final RoleService roleService;
    private final UserService userService;

    public void run(String... args) {
        seedRoles();
        seedAdminUser();
    }

    private void seedRoles() {
        List<String> roleNames = List.of("USER", "ADMIN");

        for (String name : roleNames) {
            try {
                roleService.createRole(new RoleRequest(name, name + " description"));
                log.info("✅ Seeded role: {}", name);
            } catch (IllegalArgumentException e) {
                log.info("⚠️ Role '{}' already exists, skipping", name);
            }
        }
    }

    private void seedAdminUser() {
        String username = "admin";

        if (userService.findByUsername(username).isEmpty()) {
            RegisterRequest request = RegisterRequest.builder()
                    .username(username)
                    .email("admin@gmail.com")
                    .firstName("Admin")
                    .lastName("System")
                    .password(new BCryptPasswordEncoder().encode("123456789"))
                    .build();

            userService.createUser(request);
            log.info("✅ Seeded admin user successfully");
        } else {
            log.info("⚠️ Admin user already exists, skipping");
        }
    }
}
