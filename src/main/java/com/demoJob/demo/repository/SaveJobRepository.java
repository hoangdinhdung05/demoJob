package com.demoJob.demo.repository;

import com.demoJob.demo.entity.SaveJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface SaveJobRepository extends JpaRepository<SaveJob, Long> {

    boolean existsByUserIdAndJobId(Long userId, Long jobId);

    Optional<SaveJob> findByUserIdAndJobId(Long userId, Long jobId);

    List<SaveJob> findAllByUserId(Long userId);

    void deleteByUserIdAndJobId(Long userId, Long jobId);

}
