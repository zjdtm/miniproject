package com.example.miniproject.jwt.service;

import com.example.miniproject.member.domain.Member;
import com.example.miniproject.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.example.miniproject.jwt.dto.TokenDto.ResponseToken;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final JwtService jwtService;

    private final RedisTemplate redisTemplate;

    private final PrincipalDetailService principalDetailService;
    private final MemberService memberService;

    public ResponseToken createNewTokens(String refreshTokenId) {

        // redis 에 저장된 refreshToken 을 찾는다.
        String refreshToken = redisTemplate.opsForValue().get(refreshTokenId).toString();

        // refreshToken 에서 회원의 이메일을 추출한다.
        String extractUsername = jwtService.extractUsername(refreshToken);

        // 추출한 회원의 이메일로 memberService 에서 회원의 이메일을 찾는다.
        Member findMember = memberService.findByEmail(extractUsername);

        // 새로운 accessToken 생성
        String newAccessToken = jwtService.generateToken(Duration.ofMinutes(1), findMember.getEmail());

        // 새로운 refreshToken 생성
        String newRefreshTokenId = UUID.randomUUID().toString();
        String newRefreshToken = jwtService.generateToken(Duration.ofDays(1), findMember.getEmail());

        // 기존 refreshToken 을 삭제 후 새로운 refreshToken 을 redis 에 저장
        redisTemplate.delete(refreshTokenId);

        redisTemplate.opsForValue().set(
                newRefreshTokenId, newRefreshToken,
                jwtService.extractExpiration(newRefreshToken).getTime() - new Date().getTime(),
                TimeUnit.MILLISECONDS
        );

        return ResponseToken.builder()
                .accessToken(newAccessToken)
                .refreshTokenId(newRefreshTokenId)
                .build();

    }

}
