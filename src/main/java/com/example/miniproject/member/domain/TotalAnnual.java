package com.example.miniproject.member.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "annual")
public class TotalAnnual {
	@Id
	private Long id;
	private int years;
	private int annualAmount;
	private String histYear;
}
