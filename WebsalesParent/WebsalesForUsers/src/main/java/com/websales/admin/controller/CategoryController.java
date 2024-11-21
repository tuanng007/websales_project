package com.websales.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.websales.admin.service.CategoryService;
import com.websales.common.entity.Category;

@Controller
public class CategoryController {
	
	@Autowired
	private CategoryService categoryService;
	
	@GetMapping("/categories")
	public String listAllCategories(Model model) { 
		List<Category> listCategories = categoryService.listAll();
		
		model.addAttribute("listCategories", listCategories);
		
		return "/categories/categories";
	}
	
	@GetMapping("/categories/new")
	public String newCategory(Model model) { 
		Category category = new Category();
		List<Category> listCategories = categoryService.listCategoriesInForm();

		model.addAttribute("listCategories", listCategories);
		model.addAttribute("category", category);
		model.addAttribute("pageTitle", "Create new Category");
		
		return "/categories/category_form";
	}
 }
