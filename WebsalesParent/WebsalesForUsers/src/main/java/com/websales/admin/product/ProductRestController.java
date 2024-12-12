package com.websales.admin.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductRestController {

	@Autowired
	private ProductService productService;
	
	@PostMapping("/products/check_unique")
	public String checkDuplicateProductByName(@Param("id") Integer id, @Param("name") String name) {
		return productService.checkUnique(id, name);
	}
	
//	@GetMapping("/products/get/{id}")
//	public ProductDTO getProductInfo(@PathVariable Integer id) 
//			throws ProductNotFoundException {
//		
//		Product product = service.get(id);
//		
//		return new ProductDTO(product.getName(), product.getMainImagePath(), 
//				product.getDiscountPrice(), product.getCost());
//	}
}
