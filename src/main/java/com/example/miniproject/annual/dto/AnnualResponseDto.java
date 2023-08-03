package com.example.miniproject.annual.dto;

import java.time.format.DateTimeFormatter;
import java.util.List;

import com.example.miniproject.annual.domain.Annual;
import com.example.miniproject.member.domain.Member;
import com.example.miniproject.util.AESUtil;
import com.example.miniproject.util.DateUtil;

import lombok.Getter;
import lombok.Setter;

public class AnnualResponseDto {
	@Getter
	@Setter
	public static class MainDto {
		private Long id;
		private String email;
		private String name;
		private String category;
		private String startDate;
		private String endDate;
		private String reason;

		public MainDto(Annual annual) {
			this.id = annual.getId();
			this.email = AESUtil.decrypt(annual.getMember().getEmail());
			this.name = AESUtil.decrypt(annual.getMember().getName());
			this.category = annual.getCategory().getName();
			this.startDate = annual.getStartedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			this.endDate = annual.getLastedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			this.reason = annual.getReason() == null ? null : annual.getReason().getName();
		}
	}

	@Getter
	@Setter
	public static class MyPageDto {
		private String name;
		private int annualBalance;
		private int annualUsed;
		private int annualRemain;
		private List<AnnualHistroy> annualHistroies;
		private List<DutyHistory> dutyHistories;

		public MyPageDto(Member member, List<AnnualHistroy> annualHistroies, List<DutyHistory> dutyHistories) {
			this.name = AESUtil.decrypt(member.getName());
			this.annualBalance = member.getTotalAnnual().getAnnualAmount();
			this.annualUsed = member.getAnnualUsed();
			this.annualRemain = member.getAnnualRemain();
			this.annualHistroies = annualHistroies;
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
