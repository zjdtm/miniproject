package com.example.miniproject.annual.repository;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.miniproject.annual.domain.Annual;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AnnualRepositoryTest {
	@Autowired
	private AnnualRepository annualRepository;

	@Test
	public void findAllTest() {
		//given

		//when
		List<Annual> annuals = annualRepository.findAll();

		//then
		Assertions.assertEquals(6, annuals.size());
	}
}
