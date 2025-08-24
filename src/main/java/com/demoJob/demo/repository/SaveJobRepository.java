package com.demoJob.demo.repository;

import com.demoJob.demo.entity.SaveJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface SaveJobRepository extends JpaRepository<SaveJob, Long> {

    boolean existsByUserIdAndJobId(Long userId, Long jobId);

    Optional<SaveJob> findByUserIdAndJobId(Long userId, Long jobId);

    /**
     * Lấy ra list job mà User đã lưu (Đã active)
     */
    @Query("""
            SELECT sj
            FROM SaveJob sj
            JOIN sj.job j
            WHERE j.status = 'ACTIVE' AND sj.user.id = :userId
            """)
    Page<SaveJob> findAllByUserId(Pageable pageable, Long userId);

    void deleteByUserIdAndJobId(Long userId, Long jobId);

}
