package com.example.miniproject.member.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.example.miniproject.constant.Role;
import com.example.miniproject.member.domain.Member;
import com.example.miniproject.member.domain.TotalAnnual;
import com.example.miniproject.util.AESUtil;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

public class MemberRequestDto {

	@Getter
	@Builder
	public static class CreateMember {

		@NotNull(message = "이름이 비어있을 수 없습니다.")
		private String name;

		@NotNull(message = "이메일은 비어있을 수 없습니다.")
		@Email(message = "이메일 형식이 맞지 않습니다.")
		private String email;

		@NotNull(message = "패스워드는 비어있을 수 없습니다.")
		@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,16}$"
			, message = "비밀번호는 8글자 ~ 16글자 로 대소문자 1개, 숫자 1개, 특수문자 1개 이상으로 조합해야 합니다.")
		private String password;

		@NotNull(message = "입사 날짜는 비어있을 수 없습니다.")
		@DateTimeFormat(pattern = "yyyy-MM-dd")
		private LocalDate join;

		public Member toEntity(String password, TotalAnnual totalAnnual) {

			return Member.builder()
				.name(AESUtil.encrypt(name))
				.email(AESUtil.encrypt(email))
				.password(password)
				.role(Role.USER)
				.joinedAt(join.atStartOfDay())
				.totalAnnual(totalAnnual)
				.annualUsed(0)
				.annualRemain(totalAnnual.getAnnualAmount())
				.position(totalAnnual.getPosition())
				.build();
		}

	}

	@Getter
	@Builder
	public static class LoginMember {

		@NotNull
		@Email(message = "이메일 형식이 맞지 않습니다.")
		private String email;

		@NotNull
		@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,16}$"
			, message = "비밀번호는 8글자 ~ 16글자 로 대소문자 1개, 숫자 1개, 특수문자 1개 이상으로 조합해야 합니다.")
		private String password;

	}

}
