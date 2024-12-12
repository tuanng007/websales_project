package com.websales.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;

 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.websales.checkout.CheckoutInfo;
import com.websales.common.entity.Address;
import com.websales.common.entity.CartItem;
import com.websales.common.entity.Customer;
import com.websales.common.entity.Order;
import com.websales.common.entity.PaymentMethod;
import com.websales.common.entity.ShippingRate;
import com.websales.common.exception.CustomerNotFoundException;
import com.websales.common.exception.PayPalApiException;
import com.websales.service.AddressService;
import com.websales.service.CheckoutService;
import com.websales.service.OrderService;
import com.websales.service.PayPalService;
import com.websales.service.SettingService;
import com.websales.service.ShippingRateService;
import com.websales.service.ShoppingCartService;
import com.websales.setting.PaymentSettingBag;
import com.websales.util.AuthenticationControllerHelperUtil;
import com.websales.util.OrderUtil;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CheckoutController {

	@Autowired
	private CheckoutService checkoutService;
	@Autowired
	private AddressService addressService;
	@Autowired
	private ShippingRateService shipService;
	@Autowired
	private ShoppingCartService cartService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private SettingService settingService;
	@Autowired
	private PayPalService paypalService;
	@Autowired
	private AuthenticationControllerHelperUtil authenticationControllerHelperUtil;
	
	

	@GetMapping("/checkout")
	public String showCheckoutPage(Model model, HttpServletRequest request) throws CustomerNotFoundException {
		
 		
		Customer customer = authenticationControllerHelperUtil.getAuthenticatedCustomer(request);
		
 
		Address defaultAddress = addressService.getDefaultAddress(customer);
		
		ShippingRate shippingRate = null;

		if (defaultAddress != null) {
			model.addAttribute("shippingAddress", defaultAddress.toString());
			shippingRate = shipService.getShippingRateForAddress(defaultAddress);
			
 			
		} else {
			model.addAttribute("shippingAddress", customer.toString());
			shippingRate = shipService.getShippingRateForCustomer(customer);
			
 		}
		
 
		if (shippingRate == null) {
 			return "redirect:/cart";
		}

		List<CartItem> cartItems = cartService.listCartItems(customer);
		CheckoutInfo checkoutInfo = checkoutService.prepareCheckout(cartItems, shippingRate);
		
		// Paypal
		String currencyCode = settingService.getCurrencyCode();
		PaymentSettingBag paymentSettings = settingService.getPaymentSettings();
		String paypalClientId = paymentSettings.getClientID();
		
	  

		model.addAttribute("paypalClientId", paypalClientId);
		model.addAttribute("currencyCode", currencyCode);
		model.addAttribute("customer", customer);
		
		 

		model.addAttribute("checkoutInfo", checkoutInfo);
		model.addAttribute("cartItems", cartItems);

		return "checkout/checkout";
	}
	
	@PostMapping("/place_order")
	public String placeOrder(HttpServletRequest request) throws CustomerNotFoundException {
		
 
		String paymentType = request.getParameter("paymentMethod");
	 
		PaymentMethod paymentMethod = PaymentMethod.valueOf(paymentType);
		
 	 
		Customer customer = authenticationControllerHelperUtil.getAuthenticatedCustomer(request);
		
 
		Address defaultAddress = addressService.getDefaultAddress(customer);
		ShippingRate shippingRate = null;
		
 
		if (defaultAddress != null) {
			shippingRate = shipService.getShippingRateForAddress(defaultAddress);
			
 			
		} else {
			shippingRate = shipService.getShippingRateForCustomer(customer);
			
 		}
		
		List<CartItem> cartItems = cartService.listCartItems(customer);
		CheckoutInfo checkoutInfo = checkoutService.prepareCheckout(cartItems, shippingRate);
		
	 
		Order createdOrder = orderService.createOrder(customer, defaultAddress, cartItems, paymentMethod, checkoutInfo);
		
		cartService.deleteByCustomer(customer);
		
		try {
			OrderUtil.sendOrderConfirmationEmail(request, createdOrder, settingService);
		} catch (UnsupportedEncodingException | MessagingException e) {
			// TODO Auto-generated catch block
 			e.printStackTrace();
		}
		
		return "checkout/order_completed";
	}
	
	@PostMapping("/process_paypal_order")
	public String processPayPalOrder(HttpServletRequest request, Model model) 
			throws UnsupportedEncodingException, MessagingException, CustomerNotFoundException {
		
 		
		String orderId = request.getParameter("orderId");
		
 
		String pageTitle = "Checkout Failure";
		String message = null;
		
	 

		try {
			if (paypalService.validateOrder(orderId)) {
 				return placeOrder(request);
			} else {
				pageTitle = "Checkout Failure";
				message = "ERROR: Transaction could not be completed because order information is invalid";
				
				  
				
			}
		} catch (PayPalApiException e) {
			message = "ERROR: Transaction failed due to error: " + e.getMessage();
 		}

		model.addAttribute("pageTitle", pageTitle);
		model.addAttribute("title", pageTitle);
		model.addAttribute("message", message);
	 

		return "message";
	}
}
