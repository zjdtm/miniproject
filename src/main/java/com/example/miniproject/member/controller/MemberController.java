package com.example.miniproject.member.controller;

import com.example.miniproject.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.miniproject.jwt.dto.TokenDto.ResponseToken;
import static com.example.miniproject.member.dto.MemberRequestDto.CreateMember;
import static com.example.miniproject.member.dto.MemberRequestDto.LoginMember;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody @Valid CreateMember memberRequestDto
    ) {

        memberService.register(memberRequestDto);

        return ResponseEntity.ok().body("회원가입에 성공하였습니다.");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(
            HttpServletRequest request,
            @RequestBody @Valid LoginMember memberRequestDto
    ) {

        ResponseToken responseToken = memberService.login(request, memberRequestDto);

        // Cookie 에 refreshToken 을 저장함 이때 value 값은 UUID.randomUUID() !!
        ResponseCookie responseCookie = ResponseCookie.from("refreshToken", responseToken.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(60 * 60 * 24 * 30)
                .sameSite("None")
                .domain("localhost")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(responseToken.getAccessToken());
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            HttpServletRequest request, Authentication authentication
    ) {

        memberService.logout(request, authentication);

        return ResponseEntity.ok()
                .body("로그아웃에 성공하였습니다.");
    }

}
