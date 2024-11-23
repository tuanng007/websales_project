package com.websales.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.websales.common.entity.Category;

@Repository
public interface CategoryRepository extends PagingAndSortingRepository<Category, Integer>, CrudRepository<Category, Integer> {
	
	@Query("SELECT cat FROM Category cat WHERE cat.parent.id is NULL")
	public List<Category> findRootCategories();
	
	public Category findByName(String name);
	
	public Category findByAlias(String alias);
}
