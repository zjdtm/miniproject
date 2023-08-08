package com.example.miniproject.member.controller;

import com.example.miniproject.jwt.service.TokenService;
import com.example.miniproject.member.dto.MemberResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.miniproject.jwt.dto.TokenDto.ResponseToken;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TokenApiController {

    private final TokenService tokenService;

    @PostMapping("/token")
    public ResponseEntity<Object> createNewAccessToken(
            @CookieValue("refreshToken") String refreshTokenId
    ) {

        ResponseToken responseToken = tokenService.createNewTokens(refreshTokenId);

        ResponseCookie responseCookie = ResponseCookie.from("refreshToken", responseToken.getRefreshTokenId())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(60 * 60 * 24)
                .sameSite("None")
                .domain("localhost")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(MemberResponseDto.ResponseAccessToken.builder()
                        .accessToken(responseToken.getAccessToken())
                        .build());
    }

}
