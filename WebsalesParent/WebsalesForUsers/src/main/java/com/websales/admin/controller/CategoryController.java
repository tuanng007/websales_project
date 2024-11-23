package com.websales.admin.controller;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.websales.admin.FileUploadUtil;
import com.websales.admin.exception.CategoryNotFoundException;
import com.websales.admin.service.CategoryService;
import com.websales.common.entity.Category;

@Controller
public class CategoryController {
	
	@Autowired
	private CategoryService categoryService;
	
	@GetMapping("/categories")
	public String listAllCategories(@Param("sortDir") String sortDir, Model model) {
		if(sortDir == null || sortDir.isEmpty()) { 
			sortDir = "asc";
		}
		
		List<Category> listCategories = categoryService.listAll(sortDir);
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		 
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("reverseSortDir", reverseSortDir);
		
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
	
	@GetMapping("/categories/edit/{id}")
	public String editCategory(@PathVariable(name="id") Integer id, Model model, RedirectAttributes redirectAttributes ) throws CategoryNotFoundException { 
		
		try {
			Category category = categoryService.get(id);
			List<Category> listCategories = categoryService.listCategoriesInForm();
			
			model.addAttribute("category", category);
			model.addAttribute("listCategories", listCategories);
			model.addAttribute("pageTitle", "Edit Category ID: " + id);
			
			return "/categories/category_form";
		}catch (CategoryNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e);
			return "redirect:/categories";
		} 
	}
	
	@PostMapping("/categories/save")
	public String saveCategory(Category category, RedirectAttributes redirectAttributes,
			@RequestParam("fileImage") MultipartFile multipartFile) throws IOException { 
		
		if(!multipartFile.isEmpty()) { 
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			category.setImages(fileName);
			
			Category savedCategory = categoryService.save(category);
			String uploadDir = "../category-images/" + savedCategory.getId();
			
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
			
		} else { 
			categoryService.save(category);
		}
		
		redirectAttributes.addFlashAttribute("message","The category has been saved successfully");
		
		return "redirect:/categories";
	}
 }
