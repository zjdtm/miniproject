package com.example.miniproject.member.service;

import com.example.miniproject.constant.ErrorCode;
import com.example.miniproject.exception.MemberException;
import com.example.miniproject.exception.TokenException;
import com.example.miniproject.jwt.dto.TokenDto.ResponseToken;
import com.example.miniproject.jwt.service.JwtService;
import com.example.miniproject.jwt.service.PrincipalDetailService;
import com.example.miniproject.loginLog.domain.LoginLog;
import com.example.miniproject.loginLog.dto.LoginLogDto;
import com.example.miniproject.loginLog.service.LoginLogService;
import com.example.miniproject.member.domain.Member;
import com.example.miniproject.member.domain.TotalAnnual;
import com.example.miniproject.member.repository.MemberRepository;
import com.example.miniproject.member.repository.TotalAnnualRepository;
import com.example.miniproject.util.AESUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
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

    private final TotalAnnualRepository totalAnnualRepository;

    private final PrincipalDetailService principalDetailService;

    // 회원가입
    public void register(CreateMember memberRequestDto) {

        // 회원 중복 체크
        duplicateMemberCheck(memberRequestDto.getEmail());

        TotalAnnual totalAnnual = findTotalAnnual(memberRequestDto.getJoin().atStartOfDay());

        Member member = memberRequestDto.toEntity(
                passwordEncoder.encode(memberRequestDto.getPassword()), totalAnnual
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

        // accessToken 유효기간 5분으로 설정 테스트용도
        String accessToken = jwtService.generateToken(Duration.ofMinutes(5), member.getEmail());

        // refreshToken 유효기간 1일 설정
        String refreshTokenId = UUID.randomUUID().toString();
        String refreshToken = jwtService.generateToken(Duration.ofDays(1), member.getEmail());

        // redis 에 refreshToken 을 저장
        redisTemplate.opsForValue().set(
                refreshTokenId, refreshToken,
                jwtService.extractExpiration(refreshToken).getTime() - new Date().getTime(),
                TimeUnit.MILLISECONDS
        );

        // 로그인 로그 찾기
        LoginLogDto.CreateLoginLog createLoginLog = LoginLogDto.CreateLoginLog.builder()
                .member(member)
                .userAgent(request.getHeader("User-Agent"))
                .clientIp(request.getRemoteAddr())  // TODO : ip6 형식을 ip4 형식으로 설정을 해줘야 함
                .successLoginDate(LocalDateTime.now())
                .build();

        // 로그인 로그 저장장
       loginLogService.save(createLoginLog);

        // accessToken, refreshTokenId 를 return
        return ResponseToken.builder()
                .accessToken(accessToken)
                .refreshTokenId(refreshTokenId)
                .build();
    }

    // 로그아웃
    public void logout(HttpServletRequest request, String refreshTokenId, Authentication authentication) {

        // Header 에서 accessToken 을 찾는다.
        String accessToken = request.getHeader("Authorization").substring(7);

        // UserDetails 에서 회원의 정보를 찾는다.
        UserDetails userDetails = principalDetailService.loadUserByUsername(authentication.getName());

        // 토큰의 유효성을 검증한다.
        if (!jwtService.validToken(accessToken, userDetails.getUsername())) {
            throw new TokenException(ErrorCode.TOKEN_NOT_MATCH);
        }

        // redis 에 BlackList 로 저장
        redisTemplate.delete(refreshTokenId);
        redisTemplate.opsForValue().set(
                accessToken, "logout",
                jwtService.extractExpiration(accessToken).getTime() - new Date().getTime(),
                TimeUnit.MILLISECONDS);
    }

    // 회원 이메일 찾기
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    }

    // 회원 이메일 중복 체크
    private void duplicateMemberCheck(String email) {
        if (memberRepository.existsByEmail(AESUtil.encrypt(email)))
            throw new MemberException(ErrorCode.MEMBER_EMAIL_DUPLICATED);
    }

    private TotalAnnual findTotalAnnual(LocalDateTime joinedAt) {
        int years = LocalDateTime.now().getYear() - joinedAt.getYear() + 1;

        TotalAnnual totalAnnual = totalAnnualRepository.findAnnualAmountByYears(years)
                .orElseThrow(() -> new MemberException(ErrorCode.ANNUAL_TOTAL_NOT_FOUND));

        return totalAnnual;
    }
}
