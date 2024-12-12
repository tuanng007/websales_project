package com.websales.controller;

import java.util.List;

 
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

 
import com.websales.common.entity.Address;
import com.websales.common.entity.Country;
import com.websales.common.entity.Customer;
import com.websales.common.exception.CustomerNotFoundException;
import com.websales.service.AddressService;
import com.websales.service.CustomerService;
import com.websales.util.AuthenticationControllerHelperUtil;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class AddressController {
	
private AddressService addressService;

	private CustomerService customerService;
	
	private AuthenticationControllerHelperUtil authenticationControllerHelperUtil;
	
	@Autowired
	public AddressController(AddressService addressService, 
			@Lazy CustomerService customerService,
			                 AuthenticationControllerHelperUtil authenticationControllerHelperUtil) {
		super();
		this.addressService = addressService;
		this.customerService = customerService;
		this.authenticationControllerHelperUtil = authenticationControllerHelperUtil;
	}
 
	@GetMapping("/address_book")
	public String showAddressBook(Model model, HttpServletRequest request) throws CustomerNotFoundException {
		
 		
		Customer customer = authenticationControllerHelperUtil.getAuthenticatedCustomer(request);
		
 		
		List<Address> listAddresses = addressService.listAddressBook(customer);
		
 
		boolean usePrimaryAddressAsDefault = true;
		
 		
		for (Address address : listAddresses) {
			
 			
			if (address.isDefaultForShipping()) {
				usePrimaryAddressAsDefault = false;
				break;
			}
		}
		
 
		model.addAttribute("listAddresses", listAddresses);
		model.addAttribute("customer", customer);
		model.addAttribute("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);

		return "address_book/addresses";
	}
	
	@GetMapping("/address_book/new")
	public String newAddress(Model model) {
		
 		
		List<Country> listCountries = customerService.listAllCountries();

		model.addAttribute("listCountries", listCountries);
		model.addAttribute("address", new Address());
		model.addAttribute("pageTitle", "Add New Address");
		
 
		return "address_book/address_form";
	}

	@PostMapping("/address_book/save")
	public String saveAddress(Address address, HttpServletRequest request, RedirectAttributes ra) throws CustomerNotFoundException {
		
 		
		Customer customer = authenticationControllerHelperUtil.getAuthenticatedCustomer(request);
		
	 
		
		address.setCustomer(customer);
		addressService.save(address);
		
	 

		ra.addFlashAttribute("message", "The address has been saved successfully.");
		
		String redirectOption = request.getParameter("redirect");
		String redirectURL = "redirect:/address_book";
		 

		if ("checkout".equals(redirectOption)) {
			redirectURL += "?redirect=checkout";
		}
		
 
		return redirectURL;
		
		
	}

	@GetMapping("/address_book/edit/{id}")
	public String editAddress(@PathVariable("id") Integer addressId, Model model,
			HttpServletRequest request) throws CustomerNotFoundException {
		
 		
		Customer customer = authenticationControllerHelperUtil.getAuthenticatedCustomer(request);
		
		List<Country> listCountries = customerService.listAllCountries();
		
	 

		Address address = addressService.get(addressId, customer.getId());
		
 
		model.addAttribute("address", address);
		model.addAttribute("listCountries", listCountries);
		model.addAttribute("pageTitle", "Edit Address (ID: " + addressId + ")");

		return "address_book/address_form";
	}

	@GetMapping("/address_book/delete/{id}")
	public String deleteAddress(@PathVariable("id") Integer addressId, RedirectAttributes ra,
			HttpServletRequest request) throws CustomerNotFoundException {
		
 		
		Customer customer = authenticationControllerHelperUtil.getAuthenticatedCustomer(request);
		
 		
		addressService.delete(addressId, customer.getId());
		
 		
		ra.addFlashAttribute("message", "The address ID " + addressId + " has been deleted.");

		return "redirect:/address_book";
	}
	
	@GetMapping("/address_book/default/{id}")
	public String setDefaultAddress(@PathVariable("id") Integer addressId,
			HttpServletRequest request) throws CustomerNotFoundException {
		
 

		Customer customer  = authenticationControllerHelperUtil.getAuthenticatedCustomer(request);
		
	 
		
		addressService.setDefaultAddress(addressId, customer.getId());

		String redirectOption = request.getParameter("redirect");
		String redirectURL = "redirect:/address_book";
		
	 
		if ("cart".equals(redirectOption)) {
			redirectURL = "redirect:/cart";
		}else if ("checkout".equals(redirectOption)) {
			redirectURL = "redirect:/checkout";
		}

		
 
		return redirectURL; 
	}
}
