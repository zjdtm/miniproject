package com.example.miniproject.member.controller;

import com.example.miniproject.member.dto.MemberRequestDto;
import com.example.miniproject.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid MemberRequestDto memberRequestDto) {

        memberService.save(memberRequestDto);

        return new ResponseEntity(HttpStatus.OK);
    }


}
