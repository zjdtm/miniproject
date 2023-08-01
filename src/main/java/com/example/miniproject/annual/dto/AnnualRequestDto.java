package com.example.miniproject.annual.dto;

import java.time.LocalDate;

import com.example.miniproject.annual.domain.Annual;
import com.example.miniproject.constant.Category;
import com.example.miniproject.constant.Reason;
import com.example.miniproject.constant.Status;
import com.example.miniproject.member.domain.Member;

import lombok.Getter;
import lombok.Setter;

public class AnnualRequestDto {
	@Getter
	@Setter
	public static class SaveDto {
		private String title;
		private String category;
		private String startDate;
		private String endDate;
		private String reason;

		public Annual toEntity(Member member) {
			return Annual.builder()
				.title(this.title)
				.category(Category.findByName(this.category))
				.startedAt(LocalDate.parse(this.startDate))
				.lastedAt(LocalDate.parse(this.endDate))
				.reason(Reason.findByName(this.reason))
				.member(member)
				.status(Status.READY)
				.build();
		}
	}
}
