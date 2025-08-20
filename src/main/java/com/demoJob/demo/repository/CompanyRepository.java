package com.demoJob.demo.repository;

import com.demoJob.demo.entity.Company;
import com.demoJob.demo.util.enums.CompanyStatus;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    boolean existsByName(String companyName);

    boolean existsByEmail(@NotBlank(message = "Company email not null") String email);

    Page<Company> findByStatus(CompanyStatus status, Pageable pageable);
}
