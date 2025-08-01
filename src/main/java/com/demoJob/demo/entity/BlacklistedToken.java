package com.demoJob.demo.entity;

import com.demoJob.demo.util.TokenBlacklistReason;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "tbl_token_blacklist")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlacklistedToken extends AbstractEntity<Long> {

    private String token;

    private Instant expiryDate;

    @Enumerated(EnumType.STRING)
    private TokenBlacklistReason reason;

}
