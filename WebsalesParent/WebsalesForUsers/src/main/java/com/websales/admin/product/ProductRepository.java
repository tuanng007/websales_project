package com.websales.admin.product;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.websales.common.entity.Product;

@Repository
public interface ProductRepository extends CrudRepository<Product, Integer>, PagingAndSortingRepository<Product, Integer>{

	public Product findByName(String name);
}
