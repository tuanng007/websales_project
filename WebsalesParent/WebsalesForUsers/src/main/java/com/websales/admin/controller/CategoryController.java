package com.websales.admin.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.websales.admin.FileUploadUtil;
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
	
	@PostMapping("/categories/save")
	public String saveCategory(Category category, RedirectAttributes redirectAttributes,
			@RequestParam("fileImage") MultipartFile multipartFile) throws IOException { 
		
		String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		category.setImages(fileName);
		
		Category savedCategory = categoryService.save(category);
		String uploadDir = "../category-images/" + savedCategory.getId();
		FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		
		redirectAttributes.addFlashAttribute("message","The category has been saved successfully");
		
		return "redirect:/categories";
	}
 }
