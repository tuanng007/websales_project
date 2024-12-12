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

import com.websales.admin.category.CategoryNotFoundException;
import com.websales.admin.category.CategoryService;
import com.websales.admin.paging.PagingAndSortingHelper;
import com.websales.admin.paging.PagingAndSortingParam;
import com.websales.admin.user.UserService;
import com.websales.admin.util.FileUploadUtil;
import com.websales.common.entity.Brand;
import com.websales.common.entity.Category;

@Controller
public class BrandController {
	
	@Autowired
	private BrandService brandService;
	
	@Autowired
	private CategoryService categoryService;
	
	private String redirectDefaultURL = "redirect:/brands/page/1?sortField=name&sortDir=asc";
	
	@GetMapping("/brands")
	public String viewFirstPage() {
		return redirectDefaultURL;
	}
	
	
	@GetMapping("/brands/page/{pageNum}")
	public String listByPage(@PagingAndSortingParam(listName = "listBrands", moduleURL = "/brands") PagingAndSortingHelper helper,
			@PathVariable("pageNum") int pageNum) { 		
		
		brandService.listByPage(pageNum, helper);
		return "/brands/brands";
	}
	
	@GetMapping("/brands/new")
	public String newBrand(Model model)  {
		List<Category> listCategories = categoryService.listCategoriesUsedInForm();
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
			List<Category> listCategories = categoryService.listCategoriesUsedInForm();
			
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
			String brandDir = "../brand-logos/" + id;
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
