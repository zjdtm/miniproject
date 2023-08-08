package com.example.miniproject.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class TokenDto {

    @Getter
    @AllArgsConstructor
    public static class RequestToken {

        private String refreshToken;

    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ResponseToken {

        private String accessToken;

        private String refreshTokenId;

    }

}
