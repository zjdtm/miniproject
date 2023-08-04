package com.example.miniproject.member.controller;

import com.example.miniproject.jwt.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.miniproject.jwt.dto.TokenDto.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TokenApiController {

    private final TokenService tokenService;

    @PostMapping("/token")
    public ResponseEntity<String> createNewAccessToken(
            HttpServletRequest request, Authentication authentication
    ) {

        ResponseToken responseToken = tokenService.createNewTokens(request, authentication);

        ResponseCookie responseCookie = ResponseCookie.from("refreshToken", responseToken.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(60 * 60 * 24 * 30)     // TODO : 기본으로 60초로 설정
                .sameSite("None")            // TODO : why use sameSite ?
                .domain("localhost")    // TODO : front-end domain
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(responseToken.getAccessToken());
    }

}
