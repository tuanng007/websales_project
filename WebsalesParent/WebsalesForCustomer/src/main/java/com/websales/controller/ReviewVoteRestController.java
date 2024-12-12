package com.websales.controller;

 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
 
import com.websales.common.entity.Customer;
import com.websales.common.entity.VoteResultDTO;
import com.websales.common.entity.VoteType;
import com.websales.common.exception.CustomerNotFoundException;
import com.websales.service.ReviewVoteService;
import com.websales.util.AuthenticationControllerHelperUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class ReviewVoteRestController {

 	
	private ReviewVoteService service;
	
	private AuthenticationControllerHelperUtil helper;
	
	@Autowired
	public ReviewVoteRestController(ReviewVoteService service, AuthenticationControllerHelperUtil helper) {
		super();
		this.service = service;
		this.helper = helper;
	}


	@PostMapping("/vote_review/{id}/{type}")
	public VoteResultDTO voteReview(@PathVariable(name = "id") Integer reviewId,
			@PathVariable(name = "type") String type,
			HttpServletRequest request) throws CustomerNotFoundException {
 

		Customer customer = helper.getAuthenticatedCustomer(request);
		
		if (customer == null) {
 			return VoteResultDTO.fail("You must login to vote the review.");
		}
		
		VoteType voteType = VoteType.valueOf(type.toUpperCase());
		
 		
		return service.doVote(reviewId, customer, voteType);
	}
}
