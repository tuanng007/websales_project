package com.websales.admin.shipping;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.websales.admin.paging.PagingAndSortingParam;
import com.websales.common.entity.Country;
import com.websales.common.entity.ShippingRate;

@Controller
public class ShippingRateController {
	

	private String defaultRedirectURL = "redirect:/shipping_rates/page/1?sortField=country&sortDir=asc";

	@Autowired 
	private ShippingRateService service;
	
	@GetMapping("/shipping_rates")
	public String listFirstPage() {
		
		
		return defaultRedirectURL;
	}

	@GetMapping("/shipping_rates/page/{pageNum}")
	public String listByPage(@PagingAndSortingParam(listName = "shippingRates", 
						moduleURL = "/shipping_rates") com.websales.admin.paging.PagingAndSortingHelper helper,
						@PathVariable int pageNum) {
		
		
		service.listByPage(pageNum, helper);
		return "shipping_rates/shipping_rates";
	}	

	@GetMapping("/shipping_rates/new")
	public String newRate(Model model) {
		

			
		List<Country> listCountries = service.listAllCountries();
		

		model.addAttribute("rate", new ShippingRate());
		model.addAttribute("listCountries", listCountries);
		model.addAttribute("pageTitle", "New Rate");

		return "shipping_rates/shipping_rate_form";		
	}

	@PostMapping("/shipping_rates/save")
	public String saveRate(ShippingRate rate, RedirectAttributes ra) {
		
		
		try {
			service.save(rate);
			
			
			ra.addFlashAttribute("message", "The shipping rate has been saved successfully.");
		} catch (ShippingRateAlreadyExistsException ex) {
			
			
			ra.addFlashAttribute("message", ex.getMessage());
		}
		return defaultRedirectURL;
	}

	@GetMapping("/shipping_rates/edit/{id}")
	public String editRate(@PathVariable Integer id,
			Model model, RedirectAttributes ra) {
		
		
		
		try {
			ShippingRate rate = service.get(id);
			
			
			List<Country> listCountries = service.listAllCountries();
			

			model.addAttribute("listCountries", listCountries);			
			model.addAttribute("rate", rate);
			model.addAttribute("pageTitle", "Edit Rate (ID: " + id + ")");

			return "shipping_rates/shipping_rate_form";
		} catch (ShippingRateNotFoundException ex) {
			
			
			ra.addFlashAttribute("message", ex.getMessage());
			return defaultRedirectURL;
		}
	}

	@GetMapping("/shipping_rates/cod/{id}/enabled/{supported}")
	public String updateCODSupport(@PathVariable Integer id,
			@PathVariable Boolean supported,
			Model model, RedirectAttributes ra) {
		
		
		try {
			service.updateCODSupport(id, supported);
			
			
			ra.addFlashAttribute("message", "COD support for shipping rate ID " + id + " has been updated.");
		} catch (ShippingRateNotFoundException ex) {
			
			
			ra.addFlashAttribute("message", ex.getMessage());
		}
		return defaultRedirectURL;
	}

	@GetMapping("/shipping_rates/delete/{id}")
	public String deleteRate(@PathVariable Integer id,
			Model model, RedirectAttributes ra) {
		
		
		try {
			service.delete(id);
			
			
			ra.addFlashAttribute("message", "The shipping rate ID " + id + " has been deleted.");
		} catch (ShippingRateNotFoundException ex) {
			
			
			ra.addFlashAttribute("message", ex.getMessage());
		}
		return defaultRedirectURL;
	}	
}
