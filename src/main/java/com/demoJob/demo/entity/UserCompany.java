package com.demoJob.demo.entity;

import com.demoJob.demo.util.enums.UserCompanyStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "tbl_user_company")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCompany extends AbstractEntity<Long> {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    private String position;

    private boolean isOwner;

    @Enumerated(EnumType.STRING)
    private UserCompanyStatus status;

    private LocalDate joinedAt;

    private LocalDate endDate;
    private LocalDate startDate;
}
