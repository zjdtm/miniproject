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
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final JwtService jwtService;

    private final RedisTemplate redisTemplate;

    private final PrincipalDetailService principalDetailService;
    private final MemberService memberService;

    public String createNewAccessToken(String refreshTokenId, Authentication authentication) {

        // redis 에서 회원 정보를 이용해서 jwt 가 존재하는지 확인
        String findRefreshToken = redisTemplate.opsForValue().get(refreshTokenId).toString();
        if(Objects.isNull(findRefreshToken))
            throw new TokenException(ErrorCode.TOKEN_NOT_FOUND);

        // refreshToken 유효성 검증
        if (!jwtService.validToken(findRefreshToken, principalDetailService.loadUserByUsername(authentication.getName()))
        ) {
            throw new TokenException(ErrorCode.TOKEN_NOT_MATCH);
        }

        String email = authentication.getName();
        Member member = memberService.findByEmail(email);

        redisTemplate.opsForValue().set(
                refreshTokenId, refreshTokenId,
                jwtService.extractAllClaim(refreshTokenId).getExpiresAt().getTime(),
                TimeUnit.MILLISECONDS
        );

        return jwtService.generateToken(Duration.ofHours(1), member);

    }

}
