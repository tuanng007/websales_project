package com.websales.admin.category;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.websales.common.entity.Category;

@Repository
public interface CategoryRepository extends PagingAndSortingRepository<Category, Integer>, CrudRepository<Category, Integer> {
	
	@Query("SELECT cat FROM Category cat WHERE cat.parent.id is NULL")
	public List<Category> findRootCategories(Sort sort);
	
	@Query("SELECT cat FROM Category cat WHERE cat.parent.id is NULL")
	public Page<Category> findRootCategories(Pageable pageable);
	
	@Query("SELECT cat FROM Category cat WHERE cat.name LIKE %?1%")
	public Page<Category> search(String keyword, Pageable pageable);
	
	public Long countById(Integer id);
	
	public Category findByName(String name);
	
	public Category findByAlias(String alias);
	
	
	@Query("UPDATE Category cat SET cat.enabled = ?2 WHERE cat.id = ?1")
	@Modifying
	public void updateEnabledStatus(Integer id, boolean enabled);
}
