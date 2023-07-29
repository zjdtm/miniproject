package com.example.miniproject.member.controller;

import com.example.miniproject.jwt.service.TokenService;
import com.example.miniproject.member.dto.TokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class TokenApiController {

    private final TokenService tokenService;

    @PostMapping("/api/token")
    public ResponseEntity<TokenDto.ResponseAccessToken> createNewAccessToken(
            @RequestBody TokenDto.RequestAccessToken request,
            Authentication authentication) {
        String newAccessToken = tokenService.createNewAccessToken(request.getRefreshToken(), authentication);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new TokenDto.ResponseAccessToken(newAccessToken));
    }

}
