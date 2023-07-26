package com.example.miniproject.member.dto;

import com.example.miniproject.member.domain.Member;
import com.example.miniproject.util.AESUtil;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
public class MemberRequestDto {

    @NotNull
    private String name;

    @NotNull
    private String email;

    @NotNull
    private String password;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime join;

    public Member toEntity(String password) {

        return Member.builder()
                .name(AESUtil.encrypt(name))
                .email(AESUtil.encrypt(email))
                .password(password)
                .joinedAt(join)
                .build();
    }
}
