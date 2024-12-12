package com.websales.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;

 
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
 
import com.websales.common.entity.Country;
import com.websales.common.entity.Customer;
import com.websales.service.CustomerService;
import com.websales.service.SettingService;
import com.websales.util.CustomerAccountUtil;
import com.websales.util.CustomerRegisterUtil;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomerController {
	
 	
	@Autowired 
	private CustomerService customerService;
	
	@Autowired 
	private SettingService settingService;

	@GetMapping("/register")
	public String showRegisterForm(Model model) {
		
 		
		List<Country> listCountries = customerService.listAllCountries();

 		
		model.addAttribute("listCountries", listCountries);
		model.addAttribute("pageTitle", "Customer Registration");
		model.addAttribute("customer", new Customer());

		return "register/register_form";
	}
	
	@PostMapping("/create_customer")
	public String createCustomer(Customer customer, Model model,
			HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {
		
 		
		customerService.registerCustomer(customer);
		
 		
		CustomerRegisterUtil.sendVerificationEmail(request, customer, settingService);

		model.addAttribute("pageTitle", "Registration Succeeded!");

		return "register/register_success";
	}
	
	@GetMapping("/verify")
	public String verifyAccount(@RequestParam("code") String code, Model model) {
		
 		
		boolean verified = customerService.verify(code);
		
 
		return "register/" + (verified ? "verify_success" : "verify_fail");
	}
	
	@GetMapping("/account_details")
	public String viewAccountDetails(Model model, HttpServletRequest request) {
		
 		
		String email = CustomerAccountUtil.getEmailOfAuthenticatedCustomer(request);
		
 		
		Customer customer = customerService.getCustomerByEmail(email);
		
 		
		List<Country> listCountries = customerService.listAllCountries();
		
 
		model.addAttribute("customer", customer);
		model.addAttribute("listCountries", listCountries);

		return "customer/account_form";
	}
	
	@PostMapping("/update_account_details")
	public String updateAccountDetails(Model model, Customer customer, RedirectAttributes ra,
			HttpServletRequest request) {
		
 		
		customerService.update(customer);
		
		ra.addFlashAttribute("message", "Your account details have been updated.");

		CustomerAccountUtil.updateNameForAuthenticatedCustomer(customer, request);

		String redirectOption = request.getParameter("redirect");
		
 		
		String redirectURL = "redirect:/account_details";

		if ("address_book".equals(redirectOption)) {
			redirectURL = "redirect:/address_book";
		}else if ("cart".equals(redirectOption)) {
			redirectURL = "redirect:/cart";
		}else if ("checkout".equals(redirectOption)) {
			redirectURL = "redirect:/address_book?redirect=checkout";
		}

 		
		return redirectURL;
	}
}
