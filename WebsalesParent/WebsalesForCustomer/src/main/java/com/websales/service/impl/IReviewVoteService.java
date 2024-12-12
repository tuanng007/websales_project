package com.websales.service.impl;

import com.websales.common.entity.Customer;
import com.websales.common.entity.ReviewVote;
import com.websales.common.entity.VoteResultDTO;
import com.websales.common.entity.VoteType;

public interface IReviewVoteService {

	public VoteResultDTO undoVote(ReviewVote vote, Integer reviewId, VoteType voteType);
	public VoteResultDTO doVote(Integer reviewId, Customer customer, VoteType voteType);
	
}
