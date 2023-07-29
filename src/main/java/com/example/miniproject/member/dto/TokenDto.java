package com.example.miniproject.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class TokenDto {

    @Getter
    @AllArgsConstructor
    public static class RequestAccessToken {

        private String refreshToken;

    }

    @Getter
    @AllArgsConstructor
    public static class ResponseAccessToken {

        private String accessToken;

    }

}
