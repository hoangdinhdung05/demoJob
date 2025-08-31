package com.demoJob.demo.repository;

import com.demoJob.demo.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {
    /**
     * Query kiểm tra resume exists thông qua userId và jobId
     */
    boolean existsByUserIdAndJobId(Long userId, Long jobId);
}
