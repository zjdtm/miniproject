package com.example.miniproject.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class MemberResponseDto {

    @Getter
    @Setter
    @Builder
    public static class ResponseAccessToken {
        private String accessToken;
    }

    @Getter
    @Setter
    @Builder
    public static class ResponseSuccess {

        private int status;
        private String message;
    }

}
