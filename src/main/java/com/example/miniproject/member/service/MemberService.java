package com.example.miniproject.member.service;

import com.example.miniproject.jwt.service.JwtService;
import com.example.miniproject.member.domain.Member;
import com.example.miniproject.member.repository.MemberRepository;
import com.example.miniproject.util.AESUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

import static com.example.miniproject.member.dto.MemberRequestDto.*;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;

    // 회원가입
    public void register(CreateMember memberRequestDto) {

        duplicateMemberCheck(memberRequestDto.getEmail());

        Member member = memberRequestDto.toEntity(
                passwordEncoder.encode(memberRequestDto.getPassword())
        );

        memberRepository.save(member);
    }

    // 로그인
    public String login(LoginMember memberRequestDto) {

        // 회원이 존재하는지 검증
        Member member = memberRepository.findByEmail(AESUtil.encrypt(memberRequestDto.getEmail()))
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));

        // 비밀번호가 일치하는지 검증
        if(!passwordEncoder.matches(memberRequestDto.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("패스워드를 잘못 입력하였습니다.");
        }

        String token = jwtService.generateToken(Duration.ofHours(1), member);

        return token;

    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));
    }

    // 회원 이메일 중복 체크
    private void duplicateMemberCheck(String email) {
        if (memberRepository.existsByEmail(email))
            throw new IllegalArgumentException("중복된 회원입니다.");
    }



}
