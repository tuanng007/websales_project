package com.websales.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.websales.common.entity.Currency;
@Repository
public interface CurrencyRepository extends CrudRepository<Currency, Integer> {

}
