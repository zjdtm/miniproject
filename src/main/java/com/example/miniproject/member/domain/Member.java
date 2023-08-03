package com.example.miniproject.member.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.miniproject.constant.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
	@Column(unique = true)
	private String email;

	@NotBlank
	private String password;

	@Enumerated(EnumType.STRING)
	private Role role;

	@NotNull
	@PastOrPresent
	private LocalDateTime joinedAt;

	@CreationTimestamp
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime modifiedAt;

	@OneToOne()
	@JoinColumn(name = "annual_amount_id")
	private TotalAnnual totalAnnual;

	private int annualUsed;
	private int annualRemain;

	public void changeName(String newName) {
		this.name = newName;
	}

	public void changePassword(String newPassword) {
		this.password = newPassword;
	}

	public boolean isNotEqualsEmail(String email) {
		return !this.email.equals(email);
	}

	public void sumAnnualUsed(int value) {
		this.annualUsed += value;
	}

	public void sumAnnualRemain(int value) {
		this.annualRemain += value;
	}
}
