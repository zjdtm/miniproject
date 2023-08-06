package com.example.miniproject.annual.dto;

import com.example.miniproject.annotation.AfterStartDate;
import com.example.miniproject.annual.domain.Annual;
import com.example.miniproject.constant.Category;
import com.example.miniproject.constant.Reason;
import com.example.miniproject.constant.Status;
import com.example.miniproject.member.domain.Member;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public class AnnualRequestDto {
    @Getter
    @Setter
    @AfterStartDate
    public static class SaveDto {
        @NotNull
        private String title;
        @NotNull
        private String category;
        @NotNull
        private String startDate;
        @NotNull
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

    @Getter
    @Setter
    public static class CancelDto {
        @NotNull
        private Long id;
    }

    @Getter
    @Setter
    @AfterStartDate
    public static class UpdateDto {
        @NotNull
        private Long id;
        @NotNull
        private String title;
        @NotNull
        private String startDate;
        @NotNull
        private String endDate;
        @NotNull
        private String reason;
    }

    @Getter
    @Setter
    public static class UpdatePasswordDto {

        @NotNull
        private String newPassword;

    }
}
