package com.websales.controller;

import java.util.List;

 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

 
import com.websales.common.entity.Address;
import com.websales.common.entity.CartItem;
import com.websales.common.entity.Customer;
import com.websales.common.entity.ShippingRate;
import com.websales.common.exception.CustomerNotFoundException;
import com.websales.service.AddressService;
import com.websales.service.CustomerService;
import com.websales.service.ShippingRateService;
import com.websales.service.ShoppingCartService;
import com.websales.util.AuthenticationControllerHelperUtil;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ShoppingCartController {
	
 	
	private ShoppingCartService cartService;
	
	private AddressService addressService;
	
	private ShippingRateService shipService;
	
	private AuthenticationControllerHelperUtil authenticationControllerHelperUtil;
	
	@Autowired
	public ShoppingCartController(CustomerService customerService, ShoppingCartService cartService,
			AddressService addressService, ShippingRateService shipService,
			AuthenticationControllerHelperUtil authenticationControllerHelperUtil) {
		
		super();
		this.cartService = cartService;
		this.addressService = addressService;
		this.shipService = shipService;
		this.authenticationControllerHelperUtil = authenticationControllerHelperUtil;
	}

	@GetMapping("/cart")
	public String viewCart(Model model, HttpServletRequest request) throws CustomerNotFoundException {
		
 		
		Customer customer = authenticationControllerHelperUtil.getAuthenticatedCustomer(request);
		List<CartItem> cartItems = cartService.listCartItems(customer);
		
	 

		float estimatedTotal = 0.0F;

		for (CartItem item : cartItems) {
 			estimatedTotal += item.getSubtotal();
		}
		
		Address defaultAddress = addressService.getDefaultAddress(customer);
		
		ShippingRate shippingRate = null;
		boolean usePrimaryAddressAsDefault = false;

		if (defaultAddress != null) {
			shippingRate = shipService.getShippingRateForAddress(defaultAddress);
		} else {
			usePrimaryAddressAsDefault = true;
			shippingRate = shipService.getShippingRateForCustomer(customer);
		}
 
		
		model.addAttribute("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);
		model.addAttribute("shippingSupported", shippingRate != null);

		model.addAttribute("cartItems", cartItems);
		model.addAttribute("estimatedTotal", estimatedTotal);
		
 
		return "cart/shopping_cart";
	}
	

}