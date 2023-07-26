package com.example.miniproject.annual;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.miniproject.constant.Category;
import com.example.miniproject.constant.Status;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@Entity
public class Annual {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotNull
	@Enumerated(EnumType.STRING)
	private Category category;

	@NotBlank
	private String title;
	@NotNull
	private LocalDateTime startedAt;
	@NotNull
	private LocalDateTime lastedAt;
	@NotBlank
	private String content;

	@NotNull
	@Enumerated(EnumType.STRING)
	private Status status;

	//@TODO: 회원 엔티티 조인 필요
	//

	@NotNull
	@CreationTimestamp
	private LocalDateTime createdAt;
	@UpdateTimestamp
	private LocalDateTime modifiedAt;
}
