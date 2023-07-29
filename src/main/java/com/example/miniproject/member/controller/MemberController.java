package com.example.miniproject.member.controller;

import com.example.miniproject.member.dto.MemberRequestDto;
import com.example.miniproject.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid MemberRequestDto.CreateMember memberRequestDto) {

        memberService.register(memberRequestDto);

        return ResponseEntity.ok().body("회원가입에 성공하였습니다.");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid MemberRequestDto.LoginMember memberRequestDto) {

        String token = memberService.login(memberRequestDto);

        return ResponseEntity.ok().body(token);
    }


}
