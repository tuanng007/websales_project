package com.websales.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.websales.common.entity.Customer;
import com.websales.common.entity.Review;
import com.websales.common.entity.ReviewVote;
import com.websales.common.entity.VoteResultDTO;
import com.websales.common.entity.VoteType;
import com.websales.repository.ReviewRepository;
import com.websales.repository.ReviewVoteRepository;
import com.websales.service.impl.IReviewVoteService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ReviewVoteService implements IReviewVoteService{
	
	@Autowired
	private ReviewRepository reviewRepo;
	
	@Autowired
	private ReviewVoteRepository voteRepo;


	@Override
	public VoteResultDTO undoVote(ReviewVote vote, Integer reviewId, VoteType voteType) {

		voteRepo.delete(vote);
		reviewRepo.updateVoteCount(reviewId);
		Integer voteCount = reviewRepo.getVoteCount(reviewId);
		
	

		return VoteResultDTO.success("You have unvoted " + voteType + " that review.", voteCount);
	}

	@Override
	public VoteResultDTO doVote(Integer reviewId, Customer customer, VoteType voteType) {
	
		Review review = null;

		try {
			review = reviewRepo.findById(reviewId).get();
			
		} catch (NoSuchElementException ex) {
			return VoteResultDTO.fail("The review ID " + reviewId + " no longer exists.");
		}
		
		ReviewVote vote = voteRepo.findByReviewAndCustomer(reviewId, customer.getId());
		
		if (vote != null) {
			if (vote.isUpvoted() && voteType.equals(VoteType.UP) || 
					vote.isDownvoted() && voteType.equals(VoteType.DOWN)) {
				return undoVote(vote, reviewId, voteType);
			} else if (vote.isUpvoted() && voteType.equals(VoteType.DOWN)) {
				vote.voteDown();
			} else if (vote.isDownvoted() && voteType.equals(VoteType.UP)) {
				vote.voteUp();
			}
			
		}else {
			
			
			vote = new ReviewVote();
			vote.setCustomer(customer);
			vote.setReview(review);
			

			if (voteType.equals(VoteType.UP)) {
				vote.voteUp();
			} else {
				vote.voteDown();
			}
		}
		
		voteRepo.save(vote);
		reviewRepo.updateVoteCount(reviewId);
		Integer voteCount = reviewRepo.getVoteCount(reviewId);
		
		
		return VoteResultDTO.success("You have successfully voted " + voteType + " that review.", 
				voteCount);
	}
	
	public void markReviewsVotedForProductByCustomer(List<Review> listReviews, Integer productId,
			Integer customerId) {
		
	
		
		
		List<ReviewVote> listVotes = voteRepo.findByProductAndCustomer(productId, customerId);
		

		for (ReviewVote vote : listVotes) {
			Review votedReview = vote.getReview();
			

			
			if (listReviews.contains(votedReview)) {
				int index = listReviews.indexOf(votedReview);
				
				
				Review review = listReviews.get(index);
				

				if (vote.isUpvoted()) {
					review.setUpvotedByCurrentCustomer(true);
				} else if (vote.isDownvoted()) {
					review.setDownvotedByCurrentCustomer(true);
				}
			}
		}
	}

}
