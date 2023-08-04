package com.example.miniproject.jwt.service;

import com.example.miniproject.constant.ErrorCode;
import com.example.miniproject.exception.TokenException;
import com.example.miniproject.member.domain.Member;
import com.example.miniproject.member.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.example.miniproject.jwt.dto.TokenDto.*;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final JwtService jwtService;

    private final RedisTemplate redisTemplate;

    private final PrincipalDetailService principalDetailService;
    private final MemberService memberService;

    public ResponseToken createNewTokens(HttpServletRequest request, Authentication authentication) {

        String refreshToken = null;

        // 쿠키에 refreshToken 값이 존재하는지 확인
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if(cookie.getName().equals("refreshToken")) {
                refreshToken = cookie.getValue();
            }
        }

        // redis 에서 회원 정보를 이용해서 jwt 가 존재하는지 확인
        String findRefreshToken = (String) redisTemplate.opsForValue().get(refreshToken);
        if(Objects.isNull(findRefreshToken))
            throw new TokenException(ErrorCode.TOKEN_NOT_FOUND);

        // refreshToken 유효성 검증
        if (!jwtService.validToken(findRefreshToken, principalDetailService.loadUserByUsername(authentication.getName()))
        ) {
            throw new TokenException(ErrorCode.TOKEN_NOT_MATCH);
        }

        String email = authentication.getName();
        Member member = memberService.findByEmail(email);

        // 새로운 accessToken, refreshToken 생성
        String newAccessToken = jwtService.generateToken(Duration.ofHours(1), member);
        String newRefreshToken = jwtService.generateToken(Duration.ofDays(1), member);

        // 새로운 refreshToken 을 redis 에 저장
        Long now = new Date().getTime();
        redisTemplate.opsForValue().set(
                member.getEmail(), newRefreshToken,
                jwtService.extractAllClaim(newRefreshToken).getExpiresAt().getTime() - now,
                TimeUnit.MILLISECONDS
        );

        return ResponseToken.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();

    }

}
