package com.websales.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.websales.admin.service.CategoryService;

@RestController
public class CategoryRestController {
	
	@Autowired
	private CategoryService cateService;
	
	@PostMapping("/categories/check_unique")
	public String checkUnique(@Param("id") Integer id, @Param("name") String name, @Param("alias") String alias) { 
		return cateService.checkUniqueCategory(id, name, alias);
	}
}
