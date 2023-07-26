package com.example.miniproject.member.service;

import com.example.miniproject.member.domain.Member;
import com.example.miniproject.member.dto.MemberRequestDto;
import com.example.miniproject.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public void save(MemberRequestDto memberRequestDto) {

        duplicateMemberCheck(memberRequestDto.getEmail());

        Member member = memberRequestDto.toEntity(passwordEncoder.encode(memberRequestDto.getPassword()));

        memberRepository.save(member);
    }

    private void duplicateMemberCheck(String email) {
        if (memberRepository.existsByEmail(email))
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
    }

}
