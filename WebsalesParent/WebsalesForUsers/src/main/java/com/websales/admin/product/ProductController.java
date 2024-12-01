package com.websales.admin.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.websales.admin.brand.BrandService;
import com.websales.common.entity.Brand;
import com.websales.common.entity.Product;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


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
	
	@GetMapping("/products/delete/{id}")
	public String deleteProduct(@PathVariable("id") Integer id, RedirectAttributes re) throws ProductNotFound { 
		try { 
			proService.delete(id);
			re.addFlashAttribute("message", "The product id: " + id + " has been deleted");
		} catch (ProductNotFound e) {
			re.addFlashAttribute("message", e);
		}
		return "redirect:/products";
	}
	
	@GetMapping("/products/{id}/enabled/{status}")
	public String changeEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled, RedirectAttributes re) {
		
		proService.updateEnabledStatus(id, enabled);
		
		String status = enabled ? "enabled" : "disabled";
		
		re.addFlashAttribute("message", "The product id: "  + id + " has been " + status);
		return "redirect:/products";
	}
	
	 @PostMapping("/products/save")
	 public String saveProduct(Product product, RedirectAttributes re) {
		 proService.save(product);
		 re.addFlashAttribute("message", "The product has been saved successfully!");
		 
	 	return "redirect:/products";
	 }
	 
	
}
