package com.websales.service;

import java.util.Date;

 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
 
import com.websales.common.entity.Customer;
import com.websales.common.entity.OrderStatus;
import com.websales.common.entity.Product;
import com.websales.common.entity.Review;
import com.websales.common.exception.ReviewNotFoundException;
import com.websales.repository.OrderDetailRepository;
import com.websales.repository.ProductRepository;
import com.websales.repository.ReviewRepository;
import com.websales.service.impl.IReviewService;

import jakarta.transaction.Transactional;

@Service
@Transactional 
public class ReviewService implements IReviewService {

	public static final int REVIEWS_PER_PAGE = 5;

	private ReviewRepository reviewRepo;
	private OrderDetailRepository orderDetailRepo;
	private ProductRepository productRepo;
	


	@Override
	public Page<Review> listByCustomerByPage(Customer customer, String keyword, int pageNum, String sortField,
			String sortDir) {
		
		// TODO Auto-generated method stub
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

		Pageable pageable = PageRequest.of(pageNum - 1, REVIEWS_PER_PAGE, sort);

		if (keyword != null) {
			return reviewRepo.findByCustomer(customer.getId(), keyword, pageable);
		}

		return reviewRepo.findByCustomer(customer.getId(), pageable);
	}

	@Override
	public Review getByCustomerAndId(Customer customer, Integer reviewId) throws ReviewNotFoundException {
		// TODO Auto-generated method stub
		Review review = reviewRepo.findByCustomerAndId(customer.getId(), reviewId);
		if (review == null) 
			throw new ReviewNotFoundException("Customer doesn not have any reviews with ID " + reviewId);

		return review;
	}
	
	public Page<Review> list3MostVotedReviewsByProduct(Product product) {
		Sort sort = Sort.by("votes").descending();
		Pageable pageable = PageRequest.of(0, 3, sort);

		return reviewRepo.findByProduct(product, pageable);		
	}
	
	public Page<Review> listByProduct(Product product, int pageNum, String sortField, String sortDir) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending(); 
		Pageable pageable = PageRequest.of(pageNum - 1, REVIEWS_PER_PAGE, sort);

		return reviewRepo.findByProduct(product, pageable);
	}

	public boolean didCustomerReviewProduct(Customer customer, Integer productId) {
		Long count = reviewRepo.countByCustomerAndProduct(customer.getId(), productId);
		return count == 1;
		
	}
	
	public boolean canCustomerReviewProduct(Customer customer, Integer productId) {
		Long count = orderDetailRepo.countByProductAndCustomerAndOrderStatus(productId, customer.getId(), OrderStatus.DELIVERED);
		return count > 0;
		
	}
	
	public Review save(Review review) {
		review.setReviewTime(new Date());
		Review savedReview = reviewRepo.save(review);

		Integer productId = savedReview.getProduct().getId();		
		productRepo.updateReviewCountAndAverageRating(productId);

		return savedReview;
	}
}
