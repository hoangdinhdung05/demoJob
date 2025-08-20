package com.demoJob.demo.repository;

import com.demoJob.demo.util.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.demoJob.demo.entity.User;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Tìm người dùng theo tên đăng nhập
     * @param username username của người dùng
     * @return Optional<User> nếu tìm thấy, Optional.empty() nếu không tìm thấy
     */
    Optional<User> findByUsername(String username);

    /**
     * Tìm người dùng theo email
     * @param email email của người dùng
     * @return Optional<User> nếu tìm thấy, Optional.empty() nếu không tìm thấy
     */
    Optional<User> findByEmail(String email);

    /**
     * Kiểm tra xem người dùng có tồn tại với email cụ thể hay không
     * @param email email cần kiểm tra
     * @return true nếu tồn tại, false nếu không tồn tại
     */
    boolean existsByEmail(String email);

    /**
     * Kiểm tra xem người dùng có tồn tại với tên đăng nhập cụ thể hay không
     * @param username tên đăng nhập cần kiểm tra
     * @return true nếu tồn tại, false nếu không tồn tại
     */
    boolean existsByUsername(String username);

    /**
     * Tìm người dùng theo ID
     * @param id ID của người dùng
     * @return Optional<User> nếu tìm thấy, Optional.empty() nếu không tìm thấy
     */
    @Query("""
            SELECT u
            FROM User u
            WHERE u.id = :id
            AND u.status <> :status
            """)
    Optional<User> findByIdAndStatusNot(Long id, UserStatus status);

    @Query("""
            SELECT COUNT(u) > 0
            FROM User u
            WHERE u.email = :email
            AND u.id <> :id
            """)
    boolean existsByEmailAndIdNot(String email, Long id);

    @Query("select u from User u " +
            "left join fetch u.userHasRoles r " +
            "where u.id = :id")
    Optional<User> findByIdWithRoles(@Param("id") Long id);
}