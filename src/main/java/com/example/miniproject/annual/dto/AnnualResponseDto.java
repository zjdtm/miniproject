package com.example.miniproject.annual.dto;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import com.example.miniproject.annual.domain.Annual;
import com.example.miniproject.member.domain.Member;
import com.example.miniproject.util.AESUtil;
import com.example.miniproject.util.DateUtil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class AnnualResponseDto {
	@Getter
	@Setter
	public static class MainDto {

		private String username;

		private List<AnnualCalendar> annuals;

		public MainDto(String username, List<Annual> annuals) {
			this.username = AESUtil.decrypt(username);
			this.annuals = annuals.stream().map(annual ->
				AnnualCalendar.builder()
					.id(annual.getId())
					.email(AESUtil.decrypt(annual.getMember().getEmail()))
					.name(AESUtil.decrypt(annual.getMember().getName()))
					.category(annual.getCategory().getName())
					.startDate(annual.getStartedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
					.endDate(annual.getLastedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
					.reason(annual.getReason() == null ? null : annual.getReason().getName())
					.build()
			).collect(Collectors.toList());
		}

		@Getter
		@Setter
		@Builder
		@AllArgsConstructor
		private static class AnnualCalendar {
			private Long id;
			private String email;
			private String name;
			private String category;
			private String startDate;
			private String endDate;
			private String reason;
		}
	}

	@Getter
	@Setter
	public static class MyPageDto {
		private String name;
		private String position;
		private int annualBalance;
		private int annualUsed;
		private int annualRemain;
		private List<AnnualHistroy> annualHistories;
		private List<DutyHistory> dutyHistories;

		public MyPageDto(Member member, List<AnnualHistroy> annualHistories, List<DutyHistory> dutyHistories) {
			this.name = AESUtil.decrypt(member.getName());
			this.position = member.getPosition();
			this.annualBalance = member.getTotalAnnual().getAnnualAmount();
			this.annualUsed = member.getAnnualUsed();
			this.annualRemain = member.getAnnualRemain();
			this.annualHistories = annualHistories;
			this.dutyHistories = dutyHistories;
		}
	}

	@Getter
	@Setter
	public static class AnnualHistroy {
		private Long id;
		private String title;
		private String reason;
		private String startDate;
		private String endDate;
		private String status;

		public AnnualHistroy(Annual annual) {
			this.id = annual.getId();
			this.title = annual.getTitle();
			this.reason = annual.getReason().getName();
			this.startDate = DateUtil.toDateFormat(annual.getStartedAt());
			this.endDate = DateUtil.toDateFormat(annual.getLastedAt());
			this.status = annual.getStatus().getName();
		}
	}

	@Getter
	@Setter
	public static class DutyHistory {
		private Long id;
		private String title;
		private String startDate;
		private String endDate;
		private String status;

		public DutyHistory(Annual annual) {
			this.id = annual.getId();
			this.title = annual.getTitle();
			this.startDate = DateUtil.toDateFormat(annual.getStartedAt());
			this.endDate = DateUtil.toDateFormat(annual.getLastedAt());
			this.status = annual.getStatus().getName();
		}
	}
}
