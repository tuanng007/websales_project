package com.websales.admin.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.websales.common.entity.Product;

@Controller
public class ProductController {
	
	@Autowired
	private ProductService proService;
	
	@GetMapping("/products")
	public String listAllProducts(Model model) { 
		
		List<Product> listProducts = proService.listAll();
		
		model.addAttribute("listProducts", listProducts);
		
		return "/products/products";
	}
	
}
