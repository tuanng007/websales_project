package com.websales.util;

import java.util.Iterator;

 
import com.websales.common.entity.Customer;
import com.websales.common.entity.Order;
import com.websales.common.entity.OrderDetail;
import com.websales.common.entity.Product;
import com.websales.service.ReviewService;

public class ReviewStatusUtil {

	public static void setProductReviewableStatus(Customer customer, Order order, ReviewService reviewService) {
		Iterator<OrderDetail> iterator = order.getOrderDetails().iterator();

		while(iterator.hasNext()) {
			OrderDetail orderDetail = iterator.next();
			Product product = orderDetail.getProduct();
			Integer productId = product.getId();

			boolean didCustomerReviewProduct = reviewService.didCustomerReviewProduct(customer, productId);
			product.setReviewedByCustomer(didCustomerReviewProduct);

			if (!didCustomerReviewProduct) {
				boolean canCustomerReviewProduct = reviewService.canCustomerReviewProduct(customer, productId);
				product.setCustomerCanReview(canCustomerReviewProduct);
			}

		}
	}
}
