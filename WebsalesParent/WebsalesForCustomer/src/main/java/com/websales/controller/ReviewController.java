package com.websales.controller;

import java.util.List;

 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

 
import com.websales.common.entity.Customer;
import com.websales.common.entity.Product;
import com.websales.common.entity.Review;
import com.websales.common.exception.CustomerNotFoundException;
import com.websales.common.exception.ProductNotFoundException;
import com.websales.common.exception.ReviewNotFoundException;
import com.websales.service.CustomerService;
import com.websales.service.ProductService;
import com.websales.service.ReviewService;
import com.websales.service.ReviewVoteService;
import com.websales.util.AuthenticationControllerHelperUtil;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ReviewController {

 	
	private String defaultRedirectURL = "redirect:/reviews/page/1?sortField=reviewTime&sortDir=desc";

	private ReviewService reviewService;
	
	private ProductService productService;
	
	private AuthenticationControllerHelperUtil authenticationControllerHelperUtil;
	
	private ReviewVoteService voteService;

	@Autowired
	public ReviewController(ReviewService reviewService, 
			                CustomerService customerService, 
			                ProductService productService,
			                AuthenticationControllerHelperUtil authenticationControllerHelperUtil,
			                ReviewVoteService voteService) {
		super();
		this.reviewService = reviewService;
		this.productService = productService;
		this.authenticationControllerHelperUtil = authenticationControllerHelperUtil;
		this.voteService = voteService;
	}
	
	@GetMapping("/reviews")
	public String listFirstPage(Model model) {
		
 		
		return defaultRedirectURL;
	}
	
	@GetMapping("/reviews/page/{pageNum}") 
	public String listReviewsByCustomerByPage(Model model, HttpServletRequest request,
							@PathVariable(name = "pageNum") int pageNum,
							String keyword, String sortField, String sortDir) throws CustomerNotFoundException {
		
 		
		Customer customer = authenticationControllerHelperUtil.getAuthenticatedCustomer(request);
		
 		
		Page<Review> page = reviewService.listByCustomerByPage(customer, keyword, pageNum, sortField, sortDir);		
		List<Review> listReviews = page.getContent();
		
	 
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		model.addAttribute("moduleURL", "/reviews");

		model.addAttribute("listReviews", listReviews);

		long startCount = (pageNum - 1) * ReviewService.REVIEWS_PER_PAGE + 1;
		
 		
		model.addAttribute("startCount", startCount);

		long endCount = startCount + ReviewService.REVIEWS_PER_PAGE - 1;
		 
		
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}

		model.addAttribute("endCount", endCount);
		
 
		return "reviews/reviews_customer";
	}
	
	@GetMapping("/reviews/detail/{id}")
	public String viewReview(@PathVariable("id") Integer id, Model model, 
			RedirectAttributes ra, HttpServletRequest request) throws CustomerNotFoundException {
		
 		
		Customer customer = authenticationControllerHelperUtil.getAuthenticatedCustomer(request);
		
 
		
		try {
			
			Review review = reviewService.getByCustomerAndId(customer, id);
			
	 
			
			model.addAttribute("review", review);

			return "reviews/review_detail_modal";
			
		} catch (ReviewNotFoundException ex) {
			
	 
			
			ra.addFlashAttribute("messageError", ex.getMessage());
			
			return defaultRedirectURL;		
		}
	}
	
	@GetMapping("/ratings/{productAlias}")
	public String listByProductFirstPage(@PathVariable(name = "productAlias") String productAlias, Model model,
			HttpServletRequest request) {
		
 
		return listByProductByPage(model, productAlias, 1, "reviewTime", "desc", request);
	}
	
	@GetMapping("/ratings/{productAlias}/page/{pageNum}") 
	public String listByProductByPage(Model model,
				@PathVariable(name = "productAlias") String productAlias,
				@PathVariable(name = "pageNum") int pageNum,
				String sortField, String sortDir, HttpServletRequest request) {
 
		Product product = null;

		try {
			product = productService.getProduct(productAlias);
		} catch (ProductNotFoundException ex) {
 			return "error/404";
		}

		Page<Review> page = reviewService.listByProduct(product, pageNum, sortField, sortDir);
		List<Review> listReviews = page.getContent();
		
		Customer customer = authenticationControllerHelperUtil.getAuthenticatedCustomer(request);
		
 		
		if (customer != null) {
 			voteService.markReviewsVotedForProductByCustomer(listReviews, product.getId(), customer.getId());
		}
	 

		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
 
		model.addAttribute("listReviews", listReviews);
		model.addAttribute("product", product);

		long startCount = (pageNum - 1) * ReviewService.REVIEWS_PER_PAGE + 1;
	 
		
		model.addAttribute("startCount", startCount);

		long endCount = startCount + ReviewService.REVIEWS_PER_PAGE - 1;
	 
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
 
		model.addAttribute("endCount", endCount);
		
 		
		model.addAttribute("pageTitle", "Reviews for " + product.getShortName());

		return "reviews/reviews_product";
	}
	
	@GetMapping("/write_review/product/{productId}")
	public String showViewForm(@PathVariable("productId") Integer productId, Model model,
			HttpServletRequest request) throws CustomerNotFoundException {
 
		Review review = new Review();

		Product product = null;

		try {
			product = productService.getProduct(productId);
 		} catch (ProductNotFoundException ex) {
 			return "error/404";
		}

		Customer customer = authenticationControllerHelperUtil.getAuthenticatedCustomer(request);
		
 		
		boolean customerReviewed = reviewService.didCustomerReviewProduct(customer, product.getId());
		
 
		if (customerReviewed) {
			model.addAttribute("customerReviewed", customerReviewed);
 		} else {
			boolean customerCanReview = reviewService.canCustomerReviewProduct(customer, product.getId());
			
 
			if (customerCanReview) {
				model.addAttribute("customerCanReview", customerCanReview);
 			} else {
				model.addAttribute("NoReviewPermission", true);
 			}
		}		
		
 
		model.addAttribute("product", product);
		model.addAttribute("review", review);
		
		return "reviews/review_form";
	}
	
	@PostMapping("/post_review")
	public String saveReview(Model model, Review review, Integer productId, HttpServletRequest request) throws CustomerNotFoundException {
 
		Customer customer = authenticationControllerHelperUtil.getAuthenticatedCustomer(request);

		Product product = null;

		try {
			product = productService.getProduct(productId);
 		} catch (ProductNotFoundException ex) {
 			return "error/404";
		}

		review.setProduct(product);
		review.setCustomer(customer);

		Review savedReview = reviewService.save(review);
		
 
		model.addAttribute("review", savedReview);

		return "reviews/review_done";
	}
	
}
