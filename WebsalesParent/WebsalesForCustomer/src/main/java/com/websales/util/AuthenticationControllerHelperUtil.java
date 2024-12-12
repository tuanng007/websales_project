package com.websales.util;

 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

 
import com.websales.common.entity.Customer;
import com.websales.service.CustomerService;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class AuthenticationControllerHelperUtil {

	@Autowired 
	private CustomerService customerService;

	public Customer getAuthenticatedCustomer(HttpServletRequest request) {
		String email = CustomerAccountUtil.getEmailOfAuthenticatedCustomer(request);
		return customerService.getCustomerByEmail(email);	
	}
}
