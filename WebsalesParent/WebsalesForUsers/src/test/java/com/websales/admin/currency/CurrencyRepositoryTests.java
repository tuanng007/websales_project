package com.websales.admin.currency;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.websales.admin.setting.CurrencyRepository;
import com.websales.common.entity.Currency;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false) // keep data committed into the database  (Committed transaction for test -> LOG)
public class CurrencyRepositoryTests {

	@Autowired
	private CurrencyRepository repo;

	@Test
	public void testCreateCurrencies() {
		List<Currency> listCurrencies = Arrays.asList(
			new Currency("United States Dollar", "$", "USD"),
			new Currency("Japanese Yen", "¥", "JPY"),
			new Currency("Euro", "€", "EUR"),
			new Currency("Chinese Yuan", "¥", "CNY"),
			new Currency("Vietnamese đồng ", "₫", "VND")
		);

		repo.saveAll(listCurrencies);

		Iterable<Currency> iterable = repo.findAll();

		assertThat(iterable).size().isEqualTo(13);
	}
	
	@Test
	public void testListAllOrderByNameAsc() {
		List<Currency> currencies = repo.findAllByOrderByNameAsc();

		currencies.forEach(System.out::println);

		assertThat(currencies.size()).isGreaterThan(0);
	}
}
