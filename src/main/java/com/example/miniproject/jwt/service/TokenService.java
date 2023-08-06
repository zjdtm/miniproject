package com.example.miniproject.jwt.service;

import com.example.miniproject.constant.ErrorCode;
import com.example.miniproject.exception.TokenException;
import com.example.miniproject.member.domain.Member;
import com.example.miniproject.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;
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

    public ResponseToken createNewTokens(String refreshTokenId, Authentication authentication) {

        // refreshTokenId 끝에 회원의 이메일이 포함되어 있는지 확인
        if (refreshTokenId.endsWith(authentication.getName())) {
            // redis 에서 회원 정보를 이용해서 jwt 가 존재하는지 확인
            String findRefreshToken = (String) redisTemplate.opsForValue().get(refreshTokenId);
            if (Objects.isNull(findRefreshToken))
                throw new TokenException(ErrorCode.TOKEN_NOT_FOUND);
        }

        String email = authentication.getName();
        Member member = memberService.findByEmail(email);

        // 새로운 accessToken, refreshToken 생성
        String newAccessToken = jwtService.generateToken(Duration.ofMinutes(5), member);
        String newRefreshToken = jwtService.generateToken(Duration.ofDays(1), member);

        // 기존 refreshToken 을 삭제 후 새로운 refreshToken 을 redis 에 저장
        redisTemplate.delete(refreshTokenId);
        String newRefreshTokenId = UUID.randomUUID() + member.getEmail();
        redisTemplate.opsForValue().set(
                newRefreshTokenId, newRefreshToken,
                jwtService.extractAllClaim(newRefreshToken).getExpiresAt().getTime(),
                TimeUnit.MILLISECONDS
        );

        return ResponseToken.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshTokenId)
                .build();

    }

}
