package com.websales.admin.product;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.websales.admin.paging.PagingAndSortingHelper;
import com.websales.common.entity.Product;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ProductService implements IProductService{
	
	public static final int PRODUCTS_PER_PAGE = 5;

	@Autowired 
	private ProductRepository repo;
	
	@Override
	public List<Product> listAll() {
		return (List<Product>) repo.findAll();
	}

	@Override
	public Product save(Product product) {
		if (product.getId() == null) {
			product.setCreatedTime(new Date());
		}

		if (product.getAlias() == null || product.getAlias().isEmpty()) {
			String defaultAlias = product.getName().replaceAll(" ", "-");
			product.setAlias(defaultAlias);
		} else {
			product.setAlias(product.getAlias().replaceAll(" ", "-"));
		}

		product.setUpdatedTime(new Date());

		Product updatedProduct = repo.save(product);
//		repo.updateReviewCountAndAverageRating(updatedProduct.getId());

		return updatedProduct;
	}
	
	@Override
	public String checkUnique(Integer id, String name) {
		boolean isCreatingNew = (id == null || id == 0);
		Product productByName = repo.findByName(name);

		if (isCreatingNew) {
			if (productByName != null) return "Duplicate";
		} else {
			if (productByName != null && productByName.getId() != id) {
				return "Duplicate";
			}
		}

		return "OK";
	}
	
	@Override
	public void updateProductEnabledStatus(Integer id, boolean enabled) {
		repo.updateEnabledStatus(id, enabled);
	}
	
	@Override
	public void delete(Integer id) throws ProductNotFound {
		Long countById = repo.countById(id);

		if (countById == null || countById == 0) {
			throw new ProductNotFound("Could not find any product with ID " + id);			
		}

		repo.deleteById(id);
	}
	
	@Override
	public Product get(Integer id) throws ProductNotFound{
		try {
			return repo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new ProductNotFound("Could not find any product with ID " + id);
		}
	}

	@Override
	public void listByPage(int pageNum, PagingAndSortingHelper helper, Integer categoryId) {
		
		Pageable pageable = helper.createPageable(PRODUCTS_PER_PAGE, pageNum);
		String keyword = helper.getKeyword();
		Page<Product> page = null;

		if (keyword != null && !keyword.isEmpty()) {
			
			if (categoryId != null && categoryId > 0) {
				String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
				page = repo.searchInCategory(categoryId, categoryIdMatch, keyword, pageable);
			} else {
				page = repo.findAll(keyword, pageable);
			}
		} else {
			if (categoryId != null && categoryId > 0) {
				String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
				page = repo.findAllInCategory(categoryId, categoryIdMatch, pageable);
			} else {		
				page = repo.findAll(pageable);
			}
		}
		
		helper.updateModelAttribute(pageNum, page);
	}
	
	@Override
	public void saveProductPrice(Product productInForm) {
		Product productInDB = repo.findById(productInForm.getId()).get();
		productInDB.setCost(productInForm.getCost());
		productInDB.setPrice(productInForm.getPrice());
		productInDB.setDiscountPercent(productInForm.getDiscountPercent());

		repo.save(productInDB);
	}
	
	public void searchProducts(int pageNum, PagingAndSortingHelper helper) {
		Pageable pageable = helper.createPageable(PRODUCTS_PER_PAGE, pageNum);
		String keyword = helper.getKeyword();		
		Page<Product> page = repo.searchProductsByName(keyword, pageable);		
		helper.updateModelAttribute(pageNum, page);
	}
}

