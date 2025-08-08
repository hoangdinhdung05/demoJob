package com.demoJob.demo.repository;

import com.demoJob.demo.util.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    @Query("""
            SELECT u FROM User u
            WHERE u.id = :id
            AND u.status <> :status
            """)
    Optional<User> findByIdAndStatusNot(Long id, UserStatus status);
}