package com.websales.admin.country;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.websales.common.entity.Country;

@Repository
public interface CountryRepository extends CrudRepository<Country, Integer>{
	
	public List<Country> findAllByOrderByNameAsc();
	
//	@Query("SELECT c FROM Country c WHERE c.name = :name")
//	public Country findByName(String name);
}
