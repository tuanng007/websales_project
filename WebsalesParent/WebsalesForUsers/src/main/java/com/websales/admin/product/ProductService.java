package com.websales.admin.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.websales.common.entity.Product;

@Service
public class ProductService {
	
	@Autowired
	private ProductRepository proRepo;
	
	public List<Product> listAll() { 
		return (List<Product>)  proRepo.findAll();
	}
}
