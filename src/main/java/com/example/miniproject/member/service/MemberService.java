package com.example.miniproject.member.service;

import com.example.miniproject.constant.ErrorCode;
import com.example.miniproject.exception.MemberException;
import com.example.miniproject.exception.TokenException;
import com.example.miniproject.jwt.dto.TokenDto.ResponseToken;
import com.example.miniproject.jwt.service.JwtService;
import com.example.miniproject.jwt.service.PrincipalDetailService;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
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

        // accessToken 유효기간 10분으로 설정 테스트용도
        String accessToken = jwtService.generateToken(Duration.ofMinutes(10), member);

        // refreshToken 유효기간 1일 설정
        String refreshToken = jwtService.generateToken(Duration.ofDays(1), member);

        // redis 에 refreshToken randomKey 저장
        String randomKey = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(
                randomKey, refreshToken,
                jwtService.extractAllClaim(refreshToken).getExpiresAt().getTime(),
                TimeUnit.MILLISECONDS
        );

        // loginLog 저장
        loginLogService.save(LoginLogDto.CreateLoginLog.builder()
                .member(member)
                .userAgent(request.getHeader("User-Agent"))
                .clientIp(request.getRemoteAddr())  // TODO : ip6 형식을 ip4 형식으로 설정을 해줘야 함
                .successLoginDate(LocalDateTime.now())
                .build());

        // accessToken, refreshToken 은 return
        return ResponseToken.builder()
                .accessToken(accessToken)
                .refreshToken(randomKey)
                .build();

    }

    // 로그아웃
    public void logout(HttpServletRequest request, Authentication authentication) {

        // 로그아웃 하려는 토큰이 유효한 지 검증
        String jwt = request.getHeader("Authorization").substring(7);

        if (!jwtService.validToken(jwt, principalDetailService.loadUserByUsername(authentication.getName()))) {
            throw new TokenException(ErrorCode.TOKEN_NOT_MATCH);
        }

        // 토큰에서 회원에 email 을 추출한 후 회원을 찾는다
        Member member = memberRepository.findByEmail(jwtService.extractUsername(jwt))
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        // redis 에 회원의 이메일을 key 로 가진 refreshToken 이 존재하는 경우에는 삭제한다.
        if (redisTemplate.opsForValue().get("RT:" + member.getEmail()) != null) {
            redisTemplate.delete("RT:" + member.getEmail());
        }

        // redis 에 BlackList 로 저장
        redisTemplate.opsForValue().set(
                jwt, "logout",
                jwtService.extractExpiration(jwt).getTime(),
                TimeUnit.MILLISECONDS);

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

    private TotalAnnual findTotalAnnual(LocalDateTime joinedAt) {
        int years = LocalDateTime.now().getYear() - joinedAt.getYear() + 1;

        TotalAnnual totalAnnual = totalAnnualRepository.findAnnualAmountByYears(years)
                .orElseThrow(() -> new MemberException(ErrorCode.ANNUAL_TOTAL_NOT_FOUND));

        return totalAnnual;
    }
}
