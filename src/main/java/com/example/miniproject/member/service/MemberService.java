package com.example.miniproject.member.service;

import com.example.miniproject.constant.ErrorCode;
import com.example.miniproject.exception.MemberException;
import com.example.miniproject.jwt.dto.TokenDto.ResponseToken;
import com.example.miniproject.jwt.service.JwtService;
import com.example.miniproject.loginLog.dto.LoginLogDto;
import com.example.miniproject.loginLog.service.LoginLogService;
import com.example.miniproject.member.domain.Member;
import com.example.miniproject.member.repository.MemberRepository;
import com.example.miniproject.util.AESUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static com.example.miniproject.member.dto.MemberRequestDto.CreateMember;
import static com.example.miniproject.member.dto.MemberRequestDto.LoginMember;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtService jwtService;

    private final LoginLogService loginLogService;

    private final RedisTemplate<String, String> redisTemplate;
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
    public ResponseToken login(HttpServletRequest request, LoginMember memberRequestDto) {

        // 회원이 존재하는지 검증
        Member member = memberRepository.findByEmail(AESUtil.encrypt(memberRequestDto.getEmail()))
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        // 비밀번호가 일치하는지 검증
        if (!passwordEncoder.matches(memberRequestDto.getPassword(), member.getPassword())) {
            throw new MemberException(ErrorCode.MEMBER_PASSWORD_NOT_MATCH);
        }

        // accessToken 유효기간 1분으로 설정 테스트용도
        String accessToken = jwtService.generateToken(Duration.ofMinutes(1), member);

        // refreshToken 유효기간 1일 설정
        String refreshToken = jwtService.generateToken(Duration.ofDays(1), member);

        // redis 에 refreshToken 저장
        String randomKey = redisTemplate.randomKey();
        redisTemplate.opsForValue().set(
                randomKey, refreshToken,
                jwtService.extractAllClaim(refreshToken).getExpiresAt().getTime(),
                TimeUnit.MILLISECONDS
        );

        // loginLog 저장
        loginLogService.save(LoginLogDto.CreateLoginLog.builder()
                .userId(member.getId())
                .member(member)
                .userAgent(request.getHeader("User-Agent"))
                .clientIp(request.getRemoteAddr())  // TODO : ip6 형식을 ip4 형식으로 설정을 해줘야 함
                .successLoginDate(LocalDateTime.now())
                .build());

        // accessToken 은 jwt 형식, refreshToken 은 randomKey 값을 보냄
        return ResponseToken.builder()
                .accessToken(accessToken)
                .refreshToken(randomKey)
                .build();

    }

    // 회원 이메일 찾기
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    }

    // 회원 이메일 중복 체크
    private void duplicateMemberCheck(String email) {
        if (memberRepository.existsByEmail(email))
            throw new MemberException(ErrorCode.MEMBER_EMAIL_DUPLICATED);
    }


}
