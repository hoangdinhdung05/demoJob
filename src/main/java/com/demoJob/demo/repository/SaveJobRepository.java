package com.demoJob.demo.repository;

import com.demoJob.demo.entity.SaveJob;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface SaveJobRepository extends JpaRepository<SaveJob, Long> {

    /**
     * Check exists trước khi lưu Job tránh trùng
     */
    boolean existsByUserIdAndJobId(Long userId, Long jobId);

    /**
     * User xóa Job khỏi danh sách yêu thích
     */
    @Modifying
    @Transactional
    @Query("""
            UPDATE SaveJob sj
            SET sj.status = 'DELETE'
            WHERE sj.user.id = :userId AND sj.job.id = :jobId
            """)
    int softDeleteByUserIdAndJobId(@Param("userId") Long userId,
                                   @Param("jobId") Long jobId);

    /**
     * Query danh sách job đã lưu theo UserId
     * @param userId
     * @return
     */
    List<SaveJob> findAllByUserId(Long userId);

    /**
     * Query job theo userId và jobId
     */
    Optional<SaveJob> findByUserIdAndJobId(Long userId, Long jobId);
}
