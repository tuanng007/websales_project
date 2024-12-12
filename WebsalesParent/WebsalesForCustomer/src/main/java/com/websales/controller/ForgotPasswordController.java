package com.websales.controller;

import java.io.UnsupportedEncodingException;

 
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

 
import com.websales.common.entity.Customer;
import com.websales.common.exception.CustomerNotFoundException;
import com.websales.service.CustomerService;
import com.websales.service.SettingService;
import com.websales.util.CustomerForgetPasswordUtil;
import com.websales.util.CustomerRegisterUtil;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ForgotPasswordController {
	
	@Autowired
	private CustomerService customerService;
	@Autowired
	private SettingService settingService;
	
 
	@GetMapping("/forgot_password")
	public String showRequestForm() {
		
 		
		return "customer/forgot_password_form";
	}

	@PostMapping("/forgot_password")
	public String processRequestForm(HttpServletRequest request, Model model) {
		
 		
		String email = request.getParameter("email");
		
 		
		try {
			String token = customerService.updateResetPasswordToken(email);
			
 			
			String link = CustomerRegisterUtil.getSiteURL(request) + "/reset_password?token=" + token;
			
 			
			CustomerForgetPasswordUtil.sendEmail(link, email, settingService);

			model.addAttribute("message", "We have sent a reset password link to your email."
					+ " Please check.");
		} catch (CustomerNotFoundException e) {
			
 			
			model.addAttribute("error", e.getMessage());
		} catch (UnsupportedEncodingException | MessagingException  e) {
			
 			
			model.addAttribute("error", "Could not send email");
		}

		return "customer/forgot_password_form";
	}


	@GetMapping("/reset_password")
	public String showResetForm(@RequestParam("token") String token, Model model) {
		
 		
		Customer customer = customerService.getByResetPasswordToken(token);
		
 		
		if (customer != null) {
			
 			
			model.addAttribute("token", token);
		} else {
			
			model.addAttribute("pageTitle", "Invalid Token");
			model.addAttribute("message", "Invalid Token");
			
 			
			return "message";
		}

		return "customer/reset_password_form";
	}

	@PostMapping("/reset_password")
	public String processResetForm(HttpServletRequest request, Model model) {
		
 		
		String token = request.getParameter("token");
		String password = request.getParameter("password");
		
	 

		try {
			customerService.updatePassword(token, password);

			model.addAttribute("pageTitle", "Reset Your Password");
			model.addAttribute("title", "Reset Your Password");
			model.addAttribute("message", "You have successfully changed your password.");
			
	 

		} catch (CustomerNotFoundException e) {
			model.addAttribute("pageTitle", "Invalid Token");
			model.addAttribute("message", e.getMessage());
		 
		}	

		return "message";		
	}
}
