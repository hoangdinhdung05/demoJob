package com.demoJob.demo.repository;

import com.demoJob.demo.entity.RoleHasPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RoleHasPermissionRepository extends JpaRepository<RoleHasPermission, Integer> {
    void deleteAllByRoleId(Integer roleId);
    List<RoleHasPermission> findAllByRoleId(Integer roleId);
}
