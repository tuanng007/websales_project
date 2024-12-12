package com.websales.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.websales.common.entity.Country;
import com.websales.common.entity.ShippingRate;
@Repository
public interface ShippingRateRepository extends CrudRepository<ShippingRate, Integer> {

	public ShippingRate findByCountryAndState(Country country, String state);
	
}