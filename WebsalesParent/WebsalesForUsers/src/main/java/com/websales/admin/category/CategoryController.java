package com.websales.admin.category;

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
import com.websales.admin.export.CategoryCsvExporter;
import com.websales.admin.user.UserService;
import com.websales.common.entity.Category;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class CategoryController {
	
	@Autowired
	private CategoryService categoryService;
	
	@GetMapping("/categories")
	public String listFirstPage(@Param("sortDir") String sortDir, Model model) {
		return  listCategoryByPage(1, sortDir, null ,model);
	}
	
	@GetMapping("/categories/page/{pageNum}")
	public String listCategoryByPage(
			@PathVariable(name ="pageNum") int pageNum,
			@Param("sortDir") String sortDir, 
			@Param("keyword") String keyword,
			Model model) { 
		
		if(sortDir == null || sortDir.isEmpty()) { 
			sortDir = "asc";
		}
		
		
		CategoryPageInfo pageInfo = new CategoryPageInfo();
		List<Category> listCategories = categoryService.listByPage(pageInfo, pageNum, sortDir, keyword);

		
		long startCount = (pageNum - 1) * CategoryService.CATEGORIES_PER_PAGE + 1;
		long endCount = startCount + CategoryService.CATEGORIES_PER_PAGE - 1;
		if(endCount > pageInfo.getTotalElements()) { 
			endCount = pageInfo.getTotalElements();
		}
		
		
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", pageInfo.getTotalPages());
		model.addAttribute("totalItems", pageInfo.getTotalElements());
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("sortField", "name");
		model.addAttribute("keyword", keyword);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("reverseSortDir", reverseSortDir);
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
	
	@GetMapping("/categories/{id}/enabled/{status}")
	public String changeEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) { 
		
		categoryService.updateCategoryEnabledStatus(id, enabled);
		
		String status = enabled ? "enabled" : "disabled";
		
		redirectAttributes.addFlashAttribute("message", "The category ID: " + id + " has been " + status);
		
		return "redirect:/categories";
		
		
	}
	
	@GetMapping("/categories/delete/{id}")
	public String deleteCategory(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) throws CategoryNotFoundException { 
		try {
			categoryService.delete(id);
			String categoryDir = "../category-images/";
			FileUploadUtil.removeDir(categoryDir);
			
			redirectAttributes.addFlashAttribute("message", "The category ID: " + id + " has been deleted successfully");
			
		} catch (CategoryNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e);
		}
		return "redirect:/categories";
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
	
	@GetMapping("/categories/export/csv")
	public void exportToCSV(HttpServletResponse re) throws IOException { 
		List<Category> listCategories = categoryService.listCategoriesInForm();
		CategoryCsvExporter exporter = new CategoryCsvExporter();
		exporter.export(listCategories, re);
	}
 }
