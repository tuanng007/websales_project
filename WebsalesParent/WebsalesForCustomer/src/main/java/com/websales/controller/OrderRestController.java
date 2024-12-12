package com.websales.controller;

 
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

  
import com.websales.common.entity.Customer;
import com.websales.error.OrderNotFoundException;
import com.websales.service.CustomerService;
import com.websales.service.OrderService;
import com.websales.util.AuthenticationControllerHelperUtil;
import com.websales.util.OrderReturnRequest;
import com.websales.util.OrderReturnResponse;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class OrderRestController {
	
 
	private OrderService orderService;
	private AuthenticationControllerHelperUtil authenticationControllerHelperUtil;
	
	@Autowired 
	public OrderRestController(OrderService orderService, 
			                   CustomerService customerService,
			                   AuthenticationControllerHelperUtil authenticationControllerHelperUtil) {
		super();
		this.orderService = orderService;
		this.authenticationControllerHelperUtil = authenticationControllerHelperUtil;
	}

	@PostMapping("/orders/return")
	public ResponseEntity<?> handleOrderReturnRequest(@RequestBody OrderReturnRequest returnRequest,
			HttpServletRequest servletRequest) {
		
	 

		
		Customer customer = authenticationControllerHelperUtil.getAuthenticatedCustomer(servletRequest);
		
		if(customer != null) {
 		}else {
			
 			
			return new ResponseEntity<>("Authentication required", HttpStatus.BAD_REQUEST);
		}

		
		try {
			
			orderService.setOrderReturnRequested(returnRequest, customer);
			
 			
		} catch (OrderNotFoundException ex) {
			
 			
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
		}
		
 
		return new ResponseEntity<>(new OrderReturnResponse(returnRequest.getOrderId()), HttpStatus.OK);
	}

}
