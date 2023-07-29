package com.example.miniproject.jwt.service;

import com.example.miniproject.member.domain.Member;
import com.example.miniproject.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final PrincipalDetailService principalDetailService;
    private final MemberService memberService;

    public String createNewAccessToken(String refreshToken, Authentication authentication) {

        if (!jwtService.validToken(refreshToken, principalDetailService.loadUserByUsername(authentication.getName()))) {
            throw new IllegalArgumentException("토큰 유효성 검증에 실패하였습니다.");
        }

        String email = refreshTokenService.findByRefreshToken(refreshToken).getEmail();
        Member member = memberService.findByEmail(email);

        return jwtService.generateToken(Duration.ofHours(2), member);

    }

}
