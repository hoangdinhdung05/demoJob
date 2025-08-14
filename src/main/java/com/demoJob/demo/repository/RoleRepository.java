package com.demoJob.demo.repository;

import com.demoJob.demo.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    /**
     * Tìm kiếm vai trò theo id
     * @param roleId id của vai trò
     * @return Optional<Role> nếu tìm thấy, Optional.empty() nếu không tìm thấy
     */
    Optional<Role> findById(Integer roleId);

    /**
     * Tìm kiếm vai trò theo tên
     * @param name tên của vai trò
     * @return Optional<Role> nếu tìm thấy, Optional.empty() nếu không tìm thấy
     */
    boolean existsByName(String name);

    /**
     * Tìm kiếm vai trò theo tên trong tập hợp tên vai trò
     * @param roleNames tập hợp tên vai trò
     * @return Optional<Set<Role>> nếu tìm thấy, Optional.empty() nếu không tìm thấy
     */
    Optional<Set<Role>> findByNameIn(Set<String> roleNames);
}