package com.demoJob.demo.entity;

import com.demoJob.demo.util.OtpType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_otp_code")
@Getter
@Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OtpCode extends AbstractEntity<Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String code; // 6 số

    @Enumerated(EnumType.STRING)
    private OtpType type;

    private LocalDateTime expiryTime;

    private boolean used;
}

