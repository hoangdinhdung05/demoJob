package com.demoJob.demo.repository;

import com.demoJob.demo.entity.Role;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.entity.UserHasRole;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserHasRoleRepository extends JpaRepository<UserHasRole, Long> {
    List<UserHasRole> findByUser(User user);
    List<UserHasRole> findByRole(Role role);
    Optional<UserHasRole> findByUserAndRole(User user, Role role);
}