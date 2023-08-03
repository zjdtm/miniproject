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

		@NotNull
		private String name;

		@NotNull
		@Email
		private String email;

		@NotNull
		@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,16}$")
		private String password;

		@NotNull
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
				.build();
		}

	}

	@Getter
	@Builder
	public static class LoginMember {

		@NotNull
		@Email
		private String email;

		@NotNull
		@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,16}$")
		private String password;

	}

}
