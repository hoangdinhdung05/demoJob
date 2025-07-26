package base_webSocket_demo.repository;

import base_webSocket_demo.entity.RoleHasPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RoleHasPermissionRepository extends JpaRepository<RoleHasPermission, Integer> {
    void deleteAllByRoleId(Integer roleId);
    List<RoleHasPermission> findAllByRoleId(Integer roleId);
}
