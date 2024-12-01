package com.websales.admin.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.websales.admin.brand.BrandService;
import com.websales.common.entity.Brand;
import com.websales.common.entity.Product;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class ProductController {
	
	@Autowired
	private ProductService proService;
	
	@Autowired
	private BrandService brandService;
	
	@GetMapping("/products")
	public String listAllProducts(Model model) { 
		
		List<Product> listProducts = proService.listAll();
		
		model.addAttribute("listProducts", listProducts);
		
		return "/products/products";
	}
	
	@GetMapping("/products/new")
	public String newProduct(Model model)  {
		Product product = new Product();
		product.setEnabled(true);
		product.setInStock(true);
		
		List<Brand> listBrands = brandService.listAll();
		
		model.addAttribute("listBrands", listBrands);
		model.addAttribute("product", product);
		model.addAttribute("pageTitle", "Create New Product");
		
		return "/products/product_form";
	}
	
	 @PostMapping("/products/save")
	 public String saveProduct(Product product) {
		 System.out.println("Product Name: " + product.getName());
		 System.out.println("Product Brand: " + product.getBrand().getId());
		 System.out.println("Product Category: " + product.getCategory().getId());
	 	return "redirect:/products";
	 }
	 
	
}
