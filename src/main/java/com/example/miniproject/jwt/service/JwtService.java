package com.example.miniproject.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.miniproject.constant.ErrorCode;
import com.example.miniproject.exception.TokenException;
import com.example.miniproject.member.domain.Member;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.secret_key}")
    private String secretKey;

    // JWT 생성
    public String generateToken(Duration expiredAt, Member member) {
        Date now = new Date();
        return makeToken(new Date(now.getTime() + expiredAt.toMillis()), member);
    }

    private String makeToken(Date expiry, Member member) {
        Map<String, Object> header = new HashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");
        Date now = new Date();

        return JWT.create()
                .withHeader(header)
                .withIssuer(issuer)
                .withIssuedAt(now)
                .withExpiresAt(expiry)
                .withSubject(member.getEmail())
                .sign(Algorithm.HMAC256(secretKey));
    }

    // JWT 에서 유저네임 추출
    public String extractUsername(String token) {
        return extractAllClaim(token).getSubject();
    }

    // JWT 유효성 검증
    public boolean validToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !checkTokenExpiration(token));
    }

    // JWT 만료일자 체크
    public boolean checkTokenExpiration(String token) {
        return extractExpiration(token).before(new Date());
    }

    // JWT 에서 만료일자 추출
    public Date extractExpiration(String token) {
        return extractAllClaim(token).getExpiresAt();
    }

    // JWT 에서 모든 Claim 추출
    public DecodedJWT extractAllClaim(String token) {
        try {
            return JWT.require(Algorithm.HMAC256(secretKey))
                    .build()
                    .verify(token);
        } catch (TokenExpiredException e) {
            throw new TokenException(ErrorCode.TOKEN_NOT_FOUND);
        }
    }

}
