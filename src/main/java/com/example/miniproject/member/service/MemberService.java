package com.example.miniproject.member.service;

import static com.example.miniproject.member.dto.MemberRequestDto.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.miniproject.constant.ErrorCode;
import com.example.miniproject.exception.MemberException;
import com.example.miniproject.jwt.dto.TokenDto.ResponseToken;
import com.example.miniproject.jwt.service.JwtService;
import com.example.miniproject.loginLog.dto.LoginLogDto;
import com.example.miniproject.loginLog.service.LoginLogService;
import com.example.miniproject.member.domain.Member;
import com.example.miniproject.member.domain.TotalAnnual;
import com.example.miniproject.member.repository.MemberRepository;
import com.example.miniproject.member.repository.TotalAnnualRepository;
import com.example.miniproject.util.AESUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

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

		// accessToken 유효기간 1분으로 설정 테스트용도
		String accessToken = jwtService.generateToken(Duration.ofMinutes(1), member);

		// refreshToken 유효기간 1일 설정
		String refreshToken = jwtService.generateToken(Duration.ofDays(1), member);

		// redis 에 refreshToken 저장
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

	private TotalAnnual findTotalAnnual(LocalDateTime joinedAt) {
		int years = LocalDateTime.now().getYear() - joinedAt.getYear() + 1;

		TotalAnnual totalAnnual = totalAnnualRepository.findAnnualAmountByYears(years)
			.orElseThrow(() -> new MemberException(ErrorCode.ANNUAL_TOTAL_NOT_FOUND));

		return totalAnnual;
	}
}
