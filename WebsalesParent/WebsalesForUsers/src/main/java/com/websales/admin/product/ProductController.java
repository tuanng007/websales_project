package com.websales.admin.product;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.websales.admin.brand.BrandService;
import com.websales.admin.category.CategoryService;
import com.websales.admin.paging.PagingAndSortingHelper;
import com.websales.admin.paging.PagingAndSortingParam;
import com.websales.admin.security.WebSalesUserDetails;
import com.websales.admin.util.FileUploadUtil;
import com.websales.common.entity.Brand;
import com.websales.common.entity.Category;
import com.websales.common.entity.Product;
import com.websales.common.entity.ProductImage;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class ProductController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private BrandService brandService;
	
	@Autowired
	private CategoryService categoryService;
	
	private String defaultRedirectURL = "redirect:/products/page/1?sortField=name&sortDir=asc&categoryId=0";
	
	

	@GetMapping("/products")
	public String listFirstPage() {
		
		LOGGER.info("ProductController | listFirstPage is started");
		
		return defaultRedirectURL;
	}
	
	
	@GetMapping("/products/page/{pageNum}")
	public String listByPage(
			@PagingAndSortingParam(listName = "listProducts", moduleURL = "/products") PagingAndSortingHelper helper,
			@PathVariable int pageNum, Model model,
			@RequestParam Integer categoryId
			) {
		
		LOGGER.info("ProductController | listByPage is started");
		
		productService.listByPage(pageNum, helper, categoryId);
		
		List<Category> listCategories = categoryService.listCategoriesUsedInForm();	
		
		if (categoryId != null) model.addAttribute("categoryId", categoryId);
		model.addAttribute("listCategories", listCategories);
		
		LOGGER.info("ProductController | newProduct | categoryId : " + categoryId);
		LOGGER.info("ProductController | newProduct | listCategories : " + listCategories.toString());

		return "products/products";	
	}
	
	@GetMapping("/products/new")
	public String newProduct(Model model) {
		
		LOGGER.info("ProductController | newProduct is started");
		
		List<Brand> listBrands = brandService.listAll();

		Product product = new Product();
		product.setEnabled(true);
		product.setInStock(true);
		
		LOGGER.info("ProductController | newProduct | product : " + product);
		LOGGER.info("ProductController | newProduct | listBrands : " + listBrands.size());
		

		model.addAttribute("product", product);
		model.addAttribute("listBrands", listBrands);
		model.addAttribute("pageTitle", "Create New Product");
		model.addAttribute("numberOfExistingExtraImages", 0);

		return "products/product_form";
	}

	@PostMapping("/products/save")
	public String saveProduct(Product product, RedirectAttributes ra,
			@RequestParam(value = "fileImage", required = false) MultipartFile mainImageMultipart,			
			@RequestParam(value = "extraImage", required = false) MultipartFile[] extraImageMultiparts,
			@RequestParam(required = false) String[] detailIDs,
			@RequestParam(required = false) String[] detailNames,
			@RequestParam(required = false) String[] detailValues,
			@RequestParam(required = false) String[] imageIDs,
			@RequestParam(required = false) String[] imageNames,
			@AuthenticationPrincipal WebSalesUserDetails loggedUser
			) throws IOException {
		
		LOGGER.info("ProductController | saveProduct is started");
		
		LOGGER.info("ProductController | saveProduct | mainImageMultipart.isEmpty() : " + mainImageMultipart.isEmpty());
		
		LOGGER.info("ProductController | saveProduct | extraImageMultiparts size : " + extraImageMultiparts.length);
		
		LOGGER.info("ProductController | saveProduct | loggedUser.getUsername() : " + loggedUser.getUsername());
		LOGGER.info("ProductController | saveProduct | loggedUser.getFullname() : " + loggedUser.getFullname());
		LOGGER.info("ProductController | saveProduct | loggedUser.getAuthorities() : " + loggedUser.getAuthorities());
		
		if (!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Editor")) {
			if (loggedUser.hasRole("Salesperson")) {
				productService.saveProductPrice(product);
				ra.addFlashAttribute("messageSuccess", "The product has been saved successfully.");			
				return defaultRedirectURL;
			}
		}
		
		setMainImageName(mainImageMultipart, product);
		
		setExistingExtraImageNames(imageIDs, imageNames, product);
		
		setNewExtraImageNames(extraImageMultiparts, product);
		
		setProductDetails(detailIDs, detailNames, detailValues, product);
		
		Product savedProduct = productService.save(product);
		
		// Image Folder
		saveUploadedImages(mainImageMultipart, extraImageMultiparts, savedProduct);
		deleteExtraImagesWeredRemovedOnForm(product);
		

		ra.addFlashAttribute("messageSuccess", "The product has been saved successfully.");

		return defaultRedirectURL;
		
	}
	
	@GetMapping("/products/{id}/enabled/{status}")
	public String updateProductEnabledStatus(@PathVariable Integer id,
			@PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
		
		LOGGER.info("ProductController | updateCategoryEnabledStatus is started");
		
		productService.updateProductEnabledStatus(id, enabled);
		String status = enabled ? "enabled" : "disabled";
		
		LOGGER.info("ProductController | updateCategoryEnabledStatus | status : " + status);
		
		String message = "The Product ID " + id + " has been " + status;
		redirectAttributes.addFlashAttribute("messageSuccess", message);

		return defaultRedirectURL;
	}
	
	@GetMapping("/products/delete/{id}")
	public String deleteProduct(@PathVariable Integer id, 
			Model model,
			RedirectAttributes redirectAttributes) {
		
		LOGGER.info("ProductController | deleteProduct is started");
		
		try {
			productService.delete(id);
			
			String productExtraImagesDir = "../product-images/" + id + "/extras";
			String productImagesDir = "../product-images/" + id;
			
			LOGGER.info("ProductController | deleteProduct | productExtraImagesDir : " + productExtraImagesDir);
			LOGGER.info("ProductController | deleteProduct | productImagesDir : " + productImagesDir);

			// Image Folder
			FileUploadUtil.removeDir(productExtraImagesDir);
			FileUploadUtil.removeDir(productImagesDir);
			 

			LOGGER.info("ProductController | deleteProduct is done");
			
			redirectAttributes.addFlashAttribute("messageSuccess", 
					"The product ID " + id + " has been deleted successfully");
		} catch (ProductNotFound ex) {
			
			LOGGER.info("ProductController | deleteProduct | messageError : " + ex.getMessage());
			
			redirectAttributes.addFlashAttribute("messageError", ex.getMessage());
		}

		return defaultRedirectURL;
	}
	
	
	
	@GetMapping("/products/edit/{id}")
	public String editProduct(@PathVariable Integer id, Model model,
			RedirectAttributes ra,
			@AuthenticationPrincipal WebSalesUserDetails loggedUser
			) {
		
//		LOGGER.info("ProductController | editProduct is started");
		
		try {
			Product product = productService.get(id);
			List<Brand> listBrands = brandService.listAll();
			Integer numberOfExistingExtraImages = product.getImages().size();
			
//			LOGGER.info("ProductController | editProduct | loggedUser  : " + loggedUser.toString());
			
			boolean isReadOnlyForSalesperson = false;

			if (!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Editor")) {
				if (loggedUser.hasRole("Salesperson")) {
					isReadOnlyForSalesperson = true;
				}
			}

			
//			LOGGER.info("ProductController | editProduct | product  : " + product.toString());
//			LOGGER.info("ProductController | editProduct | listBrands : " + listBrands.toString());
//			LOGGER.info("ProductController | editProduct | numberOfExistingExtraImages : " + numberOfExistingExtraImages);
//			LOGGER.info("ProductController | editProduct | isReadOnlyForSalesperson  : " + isReadOnlyForSalesperson);

			model.addAttribute("isReadOnlyForSalesperson", isReadOnlyForSalesperson);
			model.addAttribute("product", product);
			model.addAttribute("listBrands", listBrands);
			model.addAttribute("pageTitle", "Edit Product (ID: " + id + ")");
			model.addAttribute("numberOfExistingExtraImages", numberOfExistingExtraImages);


			return "products/product_form";

		} catch (ProductNotFound e) {
			
//			LOGGER.info("ProductController | editProduct | error : " + e.getMessage());
			
			ra.addFlashAttribute("messageError", e.getMessage());

			return defaultRedirectURL;
		}
	}
	
	
	@GetMapping("/products/detail/{id}")
	public String viewProductDetails(@PathVariable Integer id, Model model,
			RedirectAttributes ra) {
		
		LOGGER.info("ProductController | viewProductDetails is started");
		
		try {
			Product product = productService.get(id);
			
			LOGGER.info("ProductController | viewProductDetails  | product : " + product.toString());
			
			model.addAttribute("product", product);		

			return "products/product_detail_modal";

		} catch (ProductNotFound e) {
			
			LOGGER.info("ProductController | viewProductDetails  | messageError : " + e.getMessage());
			
			ra.addFlashAttribute("messageError", e.getMessage());

			return defaultRedirectURL;
		}
	}	
	
	private void deleteExtraImagesWeredRemovedOnForm(Product product) {
		
		LOGGER.info("ProductSaveHelper | deleteExtraImagesWeredRemovedOnForm is started");
		
		String extraImageDir = "../product-images/" + product.getId() + "/extras";
		Path dirPath = Paths.get(extraImageDir);
		
		LOGGER.info("ProductSaveHelper | deleteExtraImagesWeredRemovedOnForm | dirPath  : " + dirPath);
		

		try {
			Files.list(dirPath).forEach(file -> {
				String filename = file.toFile().getName();

				if (!product.containsImageName(filename)) {
					try {
						Files.delete(file);
						LOGGER.info("Deleted extra image: " + filename);

					} catch (IOException e) {
						LOGGER.error("Could not delete extra image: " + filename);
					}
				}

			});
		} catch (IOException ex) {
			LOGGER.error("Could not list directory: " + dirPath);
		}
	}

	public static void  setExistingExtraImageNames(String[] imageIDs, String[] imageNames, 
			Product product) {
		
		LOGGER.info("ProductSaveHelper | setExistingExtraImageNames is started");
		
//		LOGGER.info("ProductSaveHelper | deleteExtraImagesWeredRemovedOnForm | imageIDs  : " + imageIDs.toString());
//		LOGGER.info("ProductSaveHelper | deleteExtraImagesWeredRemovedOnForm | imageNames  : " + imageNames.toString());
		
		if (imageIDs == null || imageIDs.length == 0) return;

		Set<ProductImage> images = new HashSet<>();

		for (int count = 0; count < imageIDs.length; count++) {
			Integer id = Integer.parseInt(imageIDs[count]);
			String name = imageNames[count];

			images.add(new ProductImage(id, name, product));
		}

		product.setImages(images);

	}
	
	public static void  setNewExtraImageNames(MultipartFile[] extraImageMultiparts, Product product) {
		
		LOGGER.info("ProductSaveHelper | setNewExtraImageNames is started");
		
		LOGGER.info("ProductSaveHelper | setNewExtraImageNames | extraImageMultiparts.length : " + extraImageMultiparts.length);
		
		if (extraImageMultiparts.length > 0) {
			
			for (MultipartFile multipartFile : extraImageMultiparts) {
				
				LOGGER.info("ProductSaveHelper | setNewExtraImageNames | !multipartFile.isEmpty() : " + !multipartFile.isEmpty());
				
				if (!multipartFile.isEmpty()) {
					
					String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
					
					LOGGER.info("ProductSaveHelper | setNewExtraImageNames | fileName : " + fileName);
					
					
					if (!product.containsImageName(fileName)) {
						product.addExtraImage(fileName);
					}
					
					
				}
			}
		}
		
		LOGGER.info("ProductSaveHelper | setExtraImageNames is completed");
	}

	private void  setMainImageName(MultipartFile mainImageMultipart, Product product) {
		
		LOGGER.info("ProductSaveHelper | setMainImageName is started");
		
		LOGGER.info("ProductSaveHelper | setMainImageName | !mainImageMultipart.isEmpty() : " + !mainImageMultipart.isEmpty());
		
		if (!mainImageMultipart.isEmpty()) {
			
			
			String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
			
			LOGGER.info("ProductSaveHelper | setMainImageName | fileName : " + fileName);
			
			product.setMainImage(fileName);
				
		}
		
		
		LOGGER.info("ProductSaveHelper | setMainImageName is completed");
	}
	
	public static void  saveUploadedImages(MultipartFile mainImageMultipart, 
			MultipartFile[] extraImageMultiparts, Product savedProduct) throws IOException {
		
		LOGGER.info("ProductSaveHelper | saveUploadedImages is started");
		
		LOGGER.info("ProductSaveHelper | saveUploadedImages | !mainImageMultipart.isEmpty() : " + !mainImageMultipart.isEmpty());
		
		if (!mainImageMultipart.isEmpty()) {
			String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
			
			LOGGER.info("ProductSaveHelper | saveUploadedImages | fileName : " + fileName);
			
			String uploadDir = "../product-images/" + savedProduct.getId();
			
			LOGGER.info("ProductSaveHelper | saveUploadedImages | uploadDir : " + uploadDir);

			FileUploadUtil.cleanDir(uploadDir);
			
			FileUploadUtil.saveFile(uploadDir, fileName, mainImageMultipart);
		}
		
		LOGGER.info("ProductSaveHelper | saveUploadedImages | extraImageMultiparts.length : " + extraImageMultiparts.length);
		
		if (extraImageMultiparts.length > 0) {
			
			String uploadDir = "../product-images/" + savedProduct.getId() + "/extras";
			
			LOGGER.info("ProductSaveHelper | saveUploadedImages | uploadDir : " + uploadDir);

			for (MultipartFile multipartFile : extraImageMultiparts) {
				
				LOGGER.info("ProductController | saveUploadedImages | multipartFile.isEmpty() : " + multipartFile.isEmpty());
				if (multipartFile.isEmpty()) continue;

				String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
				
				LOGGER.info("ProductSaveHelper | saveUploadedImages | fileName : " + fileName);
				
				FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
				
			}
		}
		
		
		LOGGER.info("ProductSaveHelper | saveUploadedImages is completed");
	}
	
	public static void  setProductDetails(String[] detailIDs, String[] detailNames, 
			String[] detailValues, Product product) {
		
		LOGGER.info("ProductSaveHelper | setProductDetails is started");
		
		LOGGER.info("ProductSaveHelper | setProductDetails | detailNames : " + detailNames.toString());
		LOGGER.info("ProductSaveHelper | setProductDetails | detailNames : " + detailValues.toString());
		LOGGER.info("ProductSaveHelper | setProductDetails | product : " + product.toString());
		
		
		if (detailNames == null || detailNames.length == 0) return;

		for (int count = 0; count < detailNames.length; count++) {
			String name = detailNames[count];
			String value = detailValues[count];
			Integer id = Integer.parseInt(detailIDs[count]);

			if (id != 0) {
				product.addDetail(id, name, value);
			} else if (!name.isEmpty() && !value.isEmpty()) {
				product.addDetail(name, value);
			}
		}
		
		LOGGER.info("ProductSaveHelper | setProductDetails | product with its detail : " + product.getDetails().toString());
		
		LOGGER.info("ProductSaveHelper | setProductDetails is completed");
	}

}

