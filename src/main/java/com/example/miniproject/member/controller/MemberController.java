package com.example.miniproject.member.controller;

import com.example.miniproject.jwt.service.TokenService;
import com.example.miniproject.member.dto.MemberRequestDto;
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

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;
    private final TokenService tokenService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid MemberRequestDto.CreateMember memberRequestDto) {

        memberService.register(memberRequestDto);

        return ResponseEntity.ok().body("회원가입에 성공하였습니다.");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(
            HttpServletRequest request,
            Authentication authentication,
            @RequestBody @Valid MemberRequestDto.LoginMember memberRequestDto
    ) {

        Map<String, String> tokens = memberService.login(request, memberRequestDto);

        ResponseCookie responseCookie = ResponseCookie.from(tokens.get("refreshToken"))
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(60)
                .sameSite("None")           // TODO : why use sameSite ?
//                .domain("localhost:8080") // TODO : front-end domain
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(tokens.get("refreshToken"));
    }

}
