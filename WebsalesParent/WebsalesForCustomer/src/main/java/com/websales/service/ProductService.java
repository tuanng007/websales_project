package com.websales.service;

import java.util.NoSuchElementException;

 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import com.websales.common.entity.Product;
import com.websales.common.exception.ProductNotFoundException;
import com.websales.repository.ProductRepository;
import com.websales.service.impl.IProductService;

import jakarta.transaction.Transactional;
 

@Service
@Transactional
public class ProductService implements IProductService{
	
	public static final int PRODUCTS_PER_PAGE = 10;
	public static final int SEARCH_RESULTS_PER_PAGE = 10;

	@Autowired 
	private ProductRepository repo;
	
	@Override
	public Page<Product> listByCategory(int pageNum, Integer categoryId) {
		// TODO Auto-generated method stub
		
		
		String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
		Pageable pageable = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE);
		
	
		return repo.listByCategory(categoryId, categoryIdMatch, pageable);
	}

	@Override
	public Product getProduct(String alias) throws ProductNotFoundException {
		// TODO Auto-generated method stub
		Product product = repo.findByAlias(alias);
		if (product == null) {
			throw new ProductNotFoundException("Could not find any product with alias " + alias);
		}

		return product;
	}

	@Override
	public Page<Product> search(String keyword, int pageNum) {
		// TODO Auto-generated method stub
		Pageable pageable = PageRequest.of(pageNum - 1, SEARCH_RESULTS_PER_PAGE);
		return repo.search(keyword, pageable);
	}
	
	public Product getProduct(Integer id) throws ProductNotFoundException {
		try {
			Product product = repo.findById(id).get();
			return product;
		} catch (NoSuchElementException ex) {
			throw new ProductNotFoundException("Could not find any product with ID " + id);
		}
	}
	
	public Page<Product> listByBrand(int pageNum, Integer brandId) {
		Pageable pageable = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE);

		return repo.listByBrand(brandId, pageable);
	}	

}
