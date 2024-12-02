package com.websales.admin.product;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.websales.admin.FileUploadUtil;
import com.websales.admin.brand.BrandService;
import com.websales.common.entity.Brand;
import com.websales.common.entity.Product;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class ProductController {
	
	@Autowired
	private ProductService proService;
	
	@Autowired
	private BrandService brandService;
	
	@GetMapping("/products")
	public String listAllProducts(Model model) { 
		
		List<Product> listProducts = proService.listAll();
		
		model.addAttribute("listProducts", listProducts);
		
		return "/products/products";
	}
	
	@GetMapping("/products/new")
	public String newProduct(Model model)  {
		Product product = new Product();
		product.setEnabled(true);
		product.setInStock(true);
		
		List<Brand> listBrands = brandService.listAll();
		
		model.addAttribute("listBrands", listBrands);
		model.addAttribute("product", product);
		model.addAttribute("pageTitle", "Create New Product");
		
		return "/products/product_form";
	}
	
	@GetMapping("/products/delete/{id}")
	public String deleteProduct(@PathVariable("id") Integer id, RedirectAttributes re) throws ProductNotFound { 
		try { 
			proService.delete(id);
			String proMainDir = "../product-images/" + id;
			String proExtraDir = "../product-images/" + id + "/extras"; 
			
			FileUploadUtil.removeDir(proExtraDir);
			FileUploadUtil.removeDir(proMainDir);
			
			re.addFlashAttribute("message", "The product id: " + id + " has been deleted");
		} catch (ProductNotFound e) {
			re.addFlashAttribute("message", e);
		}
		return "redirect:/products";
	}
	
	@GetMapping("/products/{id}/enabled/{status}")
	public String changeEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled, RedirectAttributes re) {
		
		proService.updateEnabledStatus(id, enabled);
		
		String status = enabled ? "enabled" : "disabled";
		
		re.addFlashAttribute("message", "The product id: "  + id + " has been " + status);
		return "redirect:/products";
	}
	
	 @PostMapping("/products/save")
	 public String saveProduct(Product product, RedirectAttributes re,
			 @RequestParam("fileImage") MultipartFile mainImageMultipartFile, 
			 @RequestParam("extraImage") MultipartFile[] extraMultipartFile) throws IOException {
		 
			 setMainImageName(mainImageMultipartFile, product);
			 setExtraImageName(extraMultipartFile, product);
			 
			 Product savedProduct = proService.save(product);
			 
			 saveUploadedImages(mainImageMultipartFile, extraMultipartFile, savedProduct);
			 
			 
			 re.addFlashAttribute("message", "The product has been saved successfully!");
		 
	 	return "redirect:/products";
	 }
	 
	 private void saveUploadedImages(MultipartFile mainImageMultipartFile, MultipartFile[] extraMultipartFile,
			Product savedProduct) throws IOException {
		 if(!mainImageMultipartFile.isEmpty())  { 
			 	String fileName = StringUtils.cleanPath(mainImageMultipartFile.getOriginalFilename());
			 	String uploadDir = "../product-images/" + savedProduct.getId();
				
				FileUploadUtil.cleanDir(uploadDir);
				FileUploadUtil.saveFile(uploadDir, fileName, mainImageMultipartFile);
			}
		 	
		 if(extraMultipartFile.length > 0) { 
			 String uploadDir = "../product-images/" + savedProduct.getId() + "/extras";
			 
			 for(MultipartFile multipartFile : extraMultipartFile) { 
				 if(multipartFile.isEmpty()) continue;
					String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
	
					FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
				 
			 }
		 }
	
		
	}

	private void setMainImageName(MultipartFile mainImageMultipartFile, Product product) { 
			if(!mainImageMultipartFile.isEmpty())  { 
				String fileName = StringUtils.cleanPath(mainImageMultipartFile.getOriginalFilename());
				product.setMainImage(fileName);
			}
	 }
	 
	 private void setExtraImageName(MultipartFile[] extraMultipartFile, Product product) { 
		 if(extraMultipartFile.length > 0) { 
			 for(MultipartFile multipartFile : extraMultipartFile) { 
				 if(!multipartFile.isEmpty()) { 
						String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
						product.addExtraImage(fileName);
				 }
			 }
		 }
	 }

	
}
