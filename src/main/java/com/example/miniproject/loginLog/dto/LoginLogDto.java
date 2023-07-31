package com.example.miniproject.loginLog.dto;

import com.example.miniproject.loginLog.domain.LoginLog;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class LoginLogDto {

    @Getter
    @Builder
    public static class CreateLoginLog {

        private Long userId;

        private String userAgent;

        private String clientIp;

        private LocalDateTime createdAt;

        public LoginLog toEntity() {
            return LoginLog.builder()
                    .id(userId)
                    .userAgent(userAgent)
                    .clientIp(clientIp)
                    .createdAt(createdAt)
                    .build();
        }

    }

}
