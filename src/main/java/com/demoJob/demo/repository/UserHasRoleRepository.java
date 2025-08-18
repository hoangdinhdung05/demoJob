package com.demoJob.demo.repository;

import com.demoJob.demo.entity.UserHasRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserHasRoleRepository extends JpaRepository<UserHasRole, Long> {
}