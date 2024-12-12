package com.websales.admin.product;

import java.util.List;

import com.websales.admin.paging.PagingAndSortingHelper;
import com.websales.common.entity.Product;

public interface IProductService {

	public List<Product> listAll();
	
	public Product save(Product product);
	
	public String checkUnique(Integer id, String name);
	
	public void updateProductEnabledStatus(Integer id, boolean enabled);
	
	public void delete(Integer id) throws ProductNotFound;
	
	public Product get(Integer id) throws ProductNotFound;
	
	public void listByPage(int pageNum, PagingAndSortingHelper helper, Integer categoryId);
	
	public void saveProductPrice(Product productInForm);
	
}
