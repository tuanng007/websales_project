package com.websales.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.websales.common.entity.OrderDetail;
import com.websales.common.entity.OrderStatus;


public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

	@Query("SELECT COUNT(d) FROM OrderDetail d JOIN OrderTrack t ON d.order.id = t.order.id"
			+ " WHERE d.product.id = ?1 AND d.order.customer.id = ?2 AND"
			+ " t.status = ?3")
	public Long countByProductAndCustomerAndOrderStatus(
			Integer productId, Integer customerId, OrderStatus status);
	
}
