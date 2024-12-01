package com.websales.admin.product;

import java.util.Date;
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
	
	
	public String checkUnique(Integer id, String name){
		boolean isCreatingNew = (id == null || id == 0);
		
		Product productByName = proRepo.findByName(name);
		
		if(isCreatingNew) { 
			if(productByName != null) { 
				return "Duplicate";
			}
		} else { 
			if(productByName != null && productByName.getId() != id) { 
				return "Duplicate";
			}
		}
		
		
		return "OK";
	}
	
	public Product save(Product product) { 
		if(product.getId() == null) { 
			product.setCreatedTime(new Date());
		}
		
		if(product.getAlias() == null || product.getAlias().isEmpty()) { 
			String defaultAlias = product.getName().replaceAll(" ", "-");
			product.setAlias(defaultAlias);
		} else {
			product.setAlias(product.getAlias().replaceAll(" ", "-"));
		}
		
		product.setUpdatedTime(new Date());
		
		return proRepo.save(product);
	}
}
