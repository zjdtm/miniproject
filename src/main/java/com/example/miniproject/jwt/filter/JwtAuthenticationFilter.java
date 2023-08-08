package com.example.miniproject.jwt.filter;

import com.example.miniproject.constant.ErrorCode;
import com.example.miniproject.exception.TokenException;
import com.example.miniproject.jwt.service.JwtService;
import com.example.miniproject.jwt.service.PrincipalDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final RedisTemplate redisTemplate;
    private final PrincipalDetailService principalDetailService;
    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String REFRESH_TOKEN_COOKIE = "refreshToken";
    private final static String TOKEN_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {

        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);

        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(REFRESH_TOKEN_COOKIE)) {
                refreshToken = cookie.getValue();
            }
        }

        // Header 값에 토큰이 존재하는지 검증
        if (authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Header 에 accessToken 만 추출하기
        String accessToken = authorizationHeader.substring(7);

        // accessToken 에서 회원의 이메일 값만 추출하기
        String userEmail = jwtService.extractUsername(accessToken);

        // accessToken 이 만료되었고 cookie 에 저장된 refreshToken 이 있다면
        if(jwtService.extractExpiredCheck(accessToken) && refreshToken != null
                && request.getRequestURI().equals("/api/token")) {
            filterChain.doFilter(request, response);
            return;
        }

        // SecurityContextHolder 에 저장된 회원과 email 이 같은지 검증
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = principalDetailService.loadUserByUsername(userEmail);
            if (jwtService.validToken(accessToken, userDetails.getUsername())) {

                // redis 에 accessToken logout 여부 확인
                String isLogout = (String) redisTemplate.opsForValue().get(accessToken);

                // isLogout 이 비어 있는 경우에는 정상적으로 처리하기
                if (ObjectUtils.isEmpty(isLogout)) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            userDetails.getPassword(),
                            userDetails.getAuthorities()
                    );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                } else if (isLogout.equals("logout")) {
                    throw new TokenException(ErrorCode.MEMBER_IS_LOGOUT);
                }
            }
        }else {
            throw new TokenException(ErrorCode.TOKEN_NOT_FOUND);
        }
        filterChain.doFilter(request, response);
    }
}
