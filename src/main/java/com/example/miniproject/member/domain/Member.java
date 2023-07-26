package com.example.miniproject.member.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    @Email
    @Column(unique = true)
    private String email;

    @NotBlank
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,16}$")
    private String password;

    @NotBlank
    @PastOrPresent
    private LocalDateTime joinedAt;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    public void changeName(String newName) {
        this.name = newName;
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }
}
