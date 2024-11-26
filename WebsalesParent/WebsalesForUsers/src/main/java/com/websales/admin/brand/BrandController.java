package com.websales.admin.brand;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.websales.admin.FileUploadUtil;
import com.websales.admin.category.CategoryNotFoundException;
import com.websales.admin.category.CategoryService;
import com.websales.admin.user.UserService;
import com.websales.common.entity.Brand;
import com.websales.common.entity.Category;

@Controller
public class BrandController {
	
	@Autowired
	private BrandService brandService;
	
	@Autowired
	private CategoryService categoryService;
	
	@GetMapping("/brands")
	public String viewFirstPage(@Param("sortDir") String sortDir, Model model) {
		return listByPage(1, sortDir, null, model);
	}
	
	
	@GetMapping("/brands/page/{pageNum}")
	public String listByPage(@PathVariable("pageNum") int pageNum, 
			@Param("sortDir") String sortDir,
			@Param("keyword") String keyword, 
			Model model) { 		
		
		if(sortDir == null || sortDir.isEmpty()) { 
			sortDir = "asc";
		}
		
		
		Page<Brand> pageBrand = brandService.listByPage(pageNum, sortDir, keyword);
		List<Brand> listBrands = pageBrand.getContent();
		
		long startCount = (pageNum - 1) * BrandService.BRANDS_PER_PAGE + 1;
		long endCount = startCount + BrandService.BRANDS_PER_PAGE - 1;
		if(endCount > pageBrand.getTotalElements()) { 
			endCount = pageBrand.getTotalElements();
		}
		
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", pageBrand.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", pageBrand.getTotalElements());
		model.addAttribute("sortField", "name");
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("listBrands", listBrands);
		
		
		return "/brands/brands";
	}
	
	@GetMapping("/brands/new")
	public String newBrand(Model model)  {
		List<Category> listCategories = categoryService.listCategoriesInForm();
		Brand brand = new Brand();
		
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("brand", brand);
		model.addAttribute("pageTitle", "Create New Brand");
		
		return "/brands/brand_form";
	} 
	
	@GetMapping("/brands/edit/{id}")
	public String editBrand(@PathVariable("id") Integer id, Model model, RedirectAttributes re) throws BrandNotFoundException {
		try { 
			Brand brand = brandService.get(id);
			List<Category> listCategories = categoryService.listCategoriesInForm();
			
			model.addAttribute("brand", brand);
			model.addAttribute("listCategories", listCategories);
			model.addAttribute("pageTitle", "Edit this Brands: " + id);
			
			return "/brands/brand_form";

		} catch (BrandNotFoundException e) {
			re.addFlashAttribute("message", e);
			return "redirect:/brands";
		}
		
	}
	
	@GetMapping("/brands/delete/{id}")
	public String deleteBrand(@PathVariable("id") Integer id, RedirectAttributes re) throws BrandNotFoundException  {
		try {
			brandService.delete(id);
			String brandDir = "../brand-logos/";
			FileUploadUtil.removeDir(brandDir);
			
			re.addFlashAttribute("message", "The brand ID: " + id + " has been deleted successfully");
			
		} catch (BrandNotFoundException e) {
			re.addFlashAttribute("message", e);
		}
		return "redirect:/brands";

	}
	
	@PostMapping("/brands/save")
	public String saveBrand(Brand brand, RedirectAttributes redirectAttributes,
				@RequestParam("fileImage") MultipartFile multipartFile) throws IOException  {
		
		if(!multipartFile.isEmpty()) { 
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			brand.setLogo(fileName);
			
			Brand savedBrand = brandService.save(brand);
			String uploadDir = "../brand-logos/" + savedBrand.getId();
			
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		} else { 
			brandService.save(brand);
		}
		
		redirectAttributes.addFlashAttribute("message", "The brand has been saved successfully");
		
		return "redirect:/brands";
	}
}
