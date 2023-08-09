package com.example.miniproject.member.controller;

import com.example.miniproject.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.example.miniproject.member.dto.MemberRequestDto.CreateMember;
import static com.example.miniproject.member.dto.MemberRequestDto.LoginMember;
import static com.example.miniproject.member.dto.MemberResponseDto.ResponseAccessToken;
import static com.example.miniproject.member.dto.MemberResponseDto.ResponseSuccess;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/register")
    public ResponseEntity<Object> register(
            @RequestBody @Valid CreateMember memberRequestDto
    ) {

        memberService.register(memberRequestDto);

        return ResponseEntity.ok()
                .body(ResponseSuccess.builder()
                        .status(HttpStatus.OK.value())
                        .message("회원가입에 성공하였습니다.")
                        .build());
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(
            HttpServletRequest request,
            @RequestBody @Valid LoginMember memberRequestDto
    ) {

        Map<String, String> response = memberService.login(request, memberRequestDto);

        // Cookie 에 refreshToken 을 저장함 이때 key 값은 UUID.randomUUID() !!
        ResponseCookie responseCookie = ResponseCookie.from("refreshToken", response.get("refreshTokenId"))
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(60 * 60 * 24)
                .sameSite("None")
                .domain("localhost")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(ResponseAccessToken.builder()
                        .accessToken(response.get("accessToken"))
                        .role(response.get("role"))
                        .build());
    }

    @PostMapping("/logout")
    public ResponseEntity<Object> logout(
            @CookieValue("refreshToken") String refreshTokenId,
            HttpServletRequest request, Authentication authentication
    ) {

        memberService.logout(request, refreshTokenId, authentication);

        return ResponseEntity.ok()
                .body(ResponseSuccess.builder()
                        .status(HttpStatus.OK.value())
                        .message("로그아웃에 성공하였습니다.")
                        .build());
    }

}
