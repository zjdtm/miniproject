package com.example.miniproject.member.controller;

import com.example.miniproject.jwt.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.example.miniproject.member.dto.MemberResponseDto.ResponseAccessToken;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TokenApiController {

    private final TokenService tokenService;

    @PostMapping("/token")
    public ResponseEntity<Object> createNewAccessToken(
            @CookieValue("refreshToken") String refreshTokenId
    ) {

        Map<String, String> response = tokenService.createNewTokens(refreshTokenId);

        ResponseCookie responseCookie = ResponseCookie.from("refreshToken", response.get("refreshTokenId"))
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(60 * 60 * 24)
                .sameSite("None")
                .domain("localhost")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(ResponseAccessToken.builder()
                        .accessToken(response.get("accessToken"))
                        .role(response.get("role"))
                        .build());
    }

}
