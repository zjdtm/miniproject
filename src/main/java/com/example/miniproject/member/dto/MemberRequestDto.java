package com.example.miniproject.member.dto;

import com.example.miniproject.member.domain.Member;
import com.example.miniproject.util.AESUtil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

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

        public Member toEntity(String password) {

            return Member.builder()
                    .name(AESUtil.encrypt(name))
                    .email(AESUtil.encrypt(email))
                    .password(password)
                    .joinedAt(join.atStartOfDay())
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
