package com.websales.controller;

import java.util.List;

 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

 
import com.websales.common.entity.Category;
import com.websales.common.entity.Customer;
import com.websales.common.entity.Product;
import com.websales.common.entity.Review;
import com.websales.common.exception.CategoryNotFoundException;
import com.websales.common.exception.CustomerNotFoundException;
import com.websales.common.exception.ProductNotFoundException;
import com.websales.service.CategoryService;
import com.websales.service.ProductService;
import com.websales.service.ReviewService;
import com.websales.service.ReviewVoteService;
import com.websales.util.AuthenticationControllerHelperUtil;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ProductController {

 	
	private CategoryService categoryService;
	
	private ProductService productService;
	
	private ReviewService reviewService;
	
	private AuthenticationControllerHelperUtil authenticationControllerHelperUtil;
	
	private ReviewVoteService reviewVoteService;

	@Autowired
	public ProductController(CategoryService categoryService, ProductService productService,
			ReviewService reviewService, ReviewVoteService reviewVoteService,
			AuthenticationControllerHelperUtil authenticationControllerHelperUtil
			 ) {
		super();
		this.categoryService = categoryService;
		this.productService = productService;
		this.reviewService = reviewService;
		this.reviewVoteService = reviewVoteService;
 		this.authenticationControllerHelperUtil = authenticationControllerHelperUtil;
 	}

	@GetMapping("/c/{category_alias}")
	public String viewCategoryFirstPage(@PathVariable("category_alias") String alias,
			Model model) {
		
 		
		return viewCategoryByPage(alias, 1, model);
	}
	
	@GetMapping("/c/{category_alias}/page/{pageNum}")
	public String viewCategoryByPage(@PathVariable("category_alias") String alias,
			@PathVariable("pageNum") int pageNum,
			Model model) {
		
		try {
			
 			
			Category category = categoryService.getCategory(alias);
			
 			

			List<Category> listCategoryParents = categoryService.getCategoryParents(category);
			
 
			Page<Product> pageProducts = productService.listByCategory(pageNum, category.getId());
			
			List<Product> listProducts = pageProducts.getContent();
			
 
			long startCount = (pageNum - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
			long endCount = startCount + ProductService.PRODUCTS_PER_PAGE - 1;
			
 			
 			if (endCount > pageProducts.getTotalElements()) {
			 
				endCount = pageProducts.getTotalElements();
			}

		 
			model.addAttribute("currentPage", pageNum);
			model.addAttribute("totalPages", pageProducts.getTotalPages());
			model.addAttribute("startCount", startCount);
			model.addAttribute("endCount", endCount);
			model.addAttribute("totalItems", pageProducts.getTotalElements());
			model.addAttribute("pageTitle", category.getName());
			model.addAttribute("listCategoryParents", listCategoryParents);
			model.addAttribute("listProducts", listProducts);
			model.addAttribute("category", category);

			return "product/products_by_category";
			
			
		} catch (CategoryNotFoundException ex) {
			return "error/404";
		}
		
	}
	
	@GetMapping("/p/{product_alias}")
	public String viewProductDetail(@PathVariable("product_alias") String alias, Model model,HttpServletRequest request) throws CustomerNotFoundException {
		
 		
		try {
			Product product = productService.getProduct(alias);
			
			List<Category> listCategoryParents = categoryService.getCategoryParents(product.getCategory());
			Page<Review> listReviews = reviewService.list3MostVotedReviewsByProduct(product);
 		 
			
			Customer customer = authenticationControllerHelperUtil.getAuthenticatedCustomer(request);
					
			if (customer != null) {
				boolean customerReviewed = reviewService.didCustomerReviewProduct(customer, product.getId());
 				
				reviewVoteService.markReviewsVotedForProductByCustomer(listReviews.getContent(), 
						                                         product.getId(), 
						                                         customer.getId());
				
 				
				if (customerReviewed) {
					model.addAttribute("customerReviewed", customerReviewed);
 				} else {
					boolean customerCanReview = reviewService.canCustomerReviewProduct(customer, product.getId());
 					model.addAttribute("customerCanReview", customerCanReview);
				}
			}
			
 			
			
		 
			model.addAttribute("listCategoryParents", listCategoryParents);
			model.addAttribute("product", product);
			model.addAttribute("listReviews", listReviews);
			model.addAttribute("pageTitle", product.getShortName());

			return "product/product_detail";
		} catch (ProductNotFoundException e) {
			return "error/404";
		}
	}
	
	@GetMapping("/search")
	public String searchFirstPage(@RequestParam("keyword") String keyword, Model model) {
		
 		
		return searchByPage(keyword, 1, model);
	}

	@GetMapping("/search/page/{pageNum}")
	public String searchByPage(@RequestParam("keyword") String keyword,
			@PathVariable("pageNum") int pageNum,
			Model model) {
		
 		
		Page<Product> pageProducts = productService.search(keyword, pageNum);
		List<Product> listResult = pageProducts.getContent();
 
		long startCount = (pageNum - 1) * ProductService.SEARCH_RESULTS_PER_PAGE + 1;
		long endCount = startCount + ProductService.SEARCH_RESULTS_PER_PAGE - 1;
		
		
	 
		if (endCount > pageProducts.getTotalElements()) {
	 
			
			endCount = pageProducts.getTotalElements();
		}
		
	 

		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", pageProducts.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", pageProducts.getTotalElements());
		model.addAttribute("pageTitle", keyword + " - Search Result");

	 
		model.addAttribute("keyword", keyword);
		model.addAttribute("searchKeyword", keyword);
		model.addAttribute("listResult", listResult);

		return "product/search_result";
	}
}
