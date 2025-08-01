package com.demoJob.demo.dto;

import com.demoJob.demo.util.OtpType;
import lombok.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OtpRedisData {

    private long userId;
    private String code;
    private OtpType type;
    private LocalDateTime expiryTime;

}
