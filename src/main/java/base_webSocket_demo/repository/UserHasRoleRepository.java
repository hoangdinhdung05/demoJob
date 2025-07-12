package base_webSocket_demo.repository;

import base_webSocket_demo.entity.Role;
import base_webSocket_demo.entity.User;
import base_webSocket_demo.entity.UserHasRole;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserHasRoleRepository extends JpaRepository<UserHasRole, Long> {
    List<UserHasRole> findByUser(User user);
    List<UserHasRole> findByRole(Role role);
    Optional<UserHasRole> findByUserAndRole(User user, Role role);
}