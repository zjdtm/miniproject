package com.example.miniproject.member.service;

import com.example.miniproject.jwt.service.JwtService;
import com.example.miniproject.loginLog.dto.LoginLogDto;
import com.example.miniproject.loginLog.service.LoginLogService;
import com.example.miniproject.member.domain.Member;
import com.example.miniproject.member.domain.RefreshToken;
import com.example.miniproject.member.repository.MemberRepository;
import com.example.miniproject.member.repository.RefreshTokenRepository;
import com.example.miniproject.util.AESUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static com.example.miniproject.member.dto.MemberRequestDto.CreateMember;
import static com.example.miniproject.member.dto.MemberRequestDto.LoginMember;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtService jwtService;

    private final LoginLogService loginLogService;

    private final RefreshTokenRepository refreshTokenRepository;
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
    public Map<String, String> login(HttpServletRequest request, LoginMember memberRequestDto) {

        // 회원이 존재하는지 검증
        Member member = memberRepository.findByEmail(AESUtil.encrypt(memberRequestDto.getEmail()))
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));

        // 비밀번호가 일치하는지 검증
        if (!passwordEncoder.matches(memberRequestDto.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("패스워드를 잘못 입력하였습니다.");
        }

        // accessToken TODO : Duration.ofHours
        String accessToken = jwtService.generateToken(Duration.ofHours(1), member);

        // refreshToken TODO : Duration.ofHours
        String refreshToken = jwtService.generateToken(Duration.ofDays(1), member);

        // save refreshToken
        refreshTokenRepository.save(RefreshToken.builder()
                .email(member.getEmail())
                .refreshToken(refreshToken)
                .build());

        // loginLog save
        loginLogService.save(LoginLogDto.CreateLoginLog.builder()
                .userId(member.getId())
                .userAgent(request.getHeader("User-Agent"))
                .clientIp(request.getRemoteAddr())
                .createdAt(member.getCreatedAt())
                .build());

        Map<String, String> map = new HashMap<>();
        map.put("accessToken", accessToken);
        map.put("refreshToken", refreshToken);

        return map;

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
