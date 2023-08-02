package com.example.miniproject.member.controller;

import com.example.miniproject.jwt.service.TokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TokenApiController {

    private final TokenService tokenService;

    @PostMapping("/token")
    public ResponseEntity<String> createNewAccessToken(
            HttpServletRequest request, Authentication authentication
    ) {

        Cookie[] cookies = request.getCookies();

        String refreshTokenId = null;
        for (Cookie cookie : cookies) {
            refreshTokenId = cookie.getName();
        }

        String newAccessToken = tokenService.createNewAccessToken(refreshTokenId, authentication);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(newAccessToken);
    }

}
