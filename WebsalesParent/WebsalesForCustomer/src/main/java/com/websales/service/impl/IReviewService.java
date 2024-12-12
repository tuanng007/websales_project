package com.websales.service.impl;

import org.springframework.data.domain.Page;

import com.websales.common.entity.Customer;
import com.websales.common.entity.Review;
import com.websales.common.exception.ReviewNotFoundException;

public interface IReviewService {

	public Page<Review> listByCustomerByPage(Customer customer, String keyword, int pageNum, 
			String sortField, String sortDir);
	
	public Review getByCustomerAndId(Customer customer, Integer reviewId) throws ReviewNotFoundException;
}
