package com.websales.admin.brand;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.websales.common.entity.Brand;

@Repository
public interface BrandRepository extends CrudRepository<Brand, Integer>, PagingAndSortingRepository<Brand, Integer>{
	
	@Query("SELECT bra FROM Brand bra WHERE bra.name LIKE %?1%")
	public Page<Brand> findAll(String keyword, Pageable pageable);
	
	public Long countById(Integer id);
	
	public Brand findByName(String name);
	
	@Query("SELECT NEW Brand(bra.id, bra.name) FROM Brand bra ORDER BY bra.name ASC")
	public List<Brand> findAll();
}
