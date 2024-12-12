package com.websales.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
 
import com.websales.common.entity.Brand;
import com.websales.common.entity.Product;
import com.websales.repository.BrandRepository;
import com.websales.service.ProductService;

@Controller
public class ProductsByBrandController {
	
 
	private BrandRepository brandRepo;
	
	private ProductService productService;
	
	
	@Autowired
	public ProductsByBrandController(BrandRepository brandRepo, ProductService productService) {
		super();
		this.brandRepo = brandRepo;
		this.productService = productService;
	}

	@GetMapping("/brand/{brand_id}")
	public String listProductsByBrand(@PathVariable(name = "brand_id") Integer brandId, Model model) {
		
 		
		return listProductsByBrandByPage(brandId, 1, model);
	}

	@GetMapping("/brand/{brand_id}/page/{pageNum}")
	public String listProductsByBrandByPage(@PathVariable(name = "brand_id") Integer brandId,
			@PathVariable(name = "pageNum") int pageNum,
			Model model) {
		
		 
		
		Optional<Brand> brandById = brandRepo.findById(brandId);
		
		if (!brandById.isPresent()) {
			
 			return "error/404";
		}

		
		Brand brand = brandById.get();
		
 
		Page<Product> pageProducts = productService.listByBrand(pageNum, brand.getId());
		List<Product> listProducts = pageProducts.getContent();
		
	 
		model.addAttribute("sortField", "name");
		model.addAttribute("sortDir", "asc");
		model.addAttribute("totalPages", pageProducts.getTotalPages());
		model.addAttribute("totalItems", pageProducts.getTotalElements());
		model.addAttribute("currentPage", pageNum);	
		

		long startCount = (pageNum - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
		
 		model.addAttribute("startCount", startCount);

		long endCount = startCount + ProductService.PRODUCTS_PER_PAGE - 1;
 		
		if (endCount > pageProducts.getTotalElements()) {
 			endCount = pageProducts.getTotalElements();
			
		}

		model.addAttribute("endCount", endCount);
 
	 
		model.addAttribute("brand", brand);
		model.addAttribute("products", listProducts);
		model.addAttribute("pageTitle", "Products by " + brand.getName());		

		return "product/products_by_brand";
	}	
}
