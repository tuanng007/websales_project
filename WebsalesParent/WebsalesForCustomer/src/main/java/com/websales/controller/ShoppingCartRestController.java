package com.websales.controller;

 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
 
import com.websales.common.entity.Customer;
import com.websales.common.exception.ShoppingCartException;
import com.websales.service.CustomerService;
import com.websales.service.ShoppingCartService;
import com.websales.util.AuthenticationControllerHelperUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class ShoppingCartRestController {
	
 
	private ShoppingCartService cartService;
	
	private AuthenticationControllerHelperUtil authenticationControllerHelperUtil;
	
	@Autowired
	public ShoppingCartRestController(ShoppingCartService cartService,
			CustomerService customerService,
			AuthenticationControllerHelperUtil authenticationControllerHelperUtil) {
		super();
		this.cartService = cartService;
		this.authenticationControllerHelperUtil = authenticationControllerHelperUtil;
	}


	@PostMapping("/cart/add/{productId}/{quantity}")
	public String addProductToCart(@PathVariable("productId") Integer productId,
			@PathVariable("quantity") Integer quantity, HttpServletRequest request) {
		
		 
		
		Customer customer = authenticationControllerHelperUtil.getAuthenticatedCustomer(request);
		
		if(customer != null) {
 			
			Integer updatedQuantity;
			try {
				updatedQuantity = cartService.addProduct(productId, quantity, customer);
			} catch (ShoppingCartException ex) {
				// TODO Auto-generated catch block
				return ex.getMessage();
			}
			
 			
			return updatedQuantity + " item(s) of this product were added to your shopping cart.";
		}else {
			return "You must login to add this product to cart.";
		}
		
	}

	
	@DeleteMapping("/cart/remove/{productId}")
	public String removeProduct(@PathVariable("productId") Integer productId,
			HttpServletRequest request) {
		
 		
		Customer customer = authenticationControllerHelperUtil.getAuthenticatedCustomer(request);
		
		if(customer != null) {
			
 			
			cartService.removeProduct(productId, customer);
			
			return "The product has been removed from your shopping cart.";
		}else {
			return "You must login to remove product.";
		}
		
	}
	
	@PostMapping("/cart/update/{productId}/{quantity}")
	public String updateQuantity(@PathVariable("productId") Integer productId,
			@PathVariable("quantity") Integer quantity, HttpServletRequest request) {
		
 		
		Customer customer = authenticationControllerHelperUtil.getAuthenticatedCustomer(request);
		
		if(customer != null) {
 			
			float subtotal = cartService.updateQuantity(productId, quantity, customer);
			
 
			return String.valueOf(subtotal);
		}else {
			return "You must login to change quantity of product.";
		}
		
	}
}
