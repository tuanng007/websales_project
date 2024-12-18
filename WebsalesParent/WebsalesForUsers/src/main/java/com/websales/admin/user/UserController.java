package com.websales.admin.user;

import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.service.annotation.PatchExchange;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.websales.admin.export.UserCsvExporter;
import com.websales.admin.export.UserExcelExporter;
import com.websales.admin.export.UserPdfExporter;
import com.websales.admin.paging.PagingAndSortingHelper;
import com.websales.admin.paging.PagingAndSortingParam;
import com.websales.admin.util.DirectUtil;
import com.websales.admin.util.FileUploadUtil;
import com.websales.common.entity.Role;
import com.websales.common.entity.User;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class UserController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService service;
	
	private String defaultRedirectURL = "redirect:/users/page/1?sortField=firstName&sortDir=asc";
	
	@GetMapping("/users")
	public String listFirstPage() {
		
		LOGGER.info("UserController | listFirstPage is started");
		
		return defaultRedirectURL;
	}
	
	@GetMapping("/users/page/{pageNum}")
	public String listByPage(@PagingAndSortingParam(listName = "listUsers", moduleURL = "/users") PagingAndSortingHelper helper,
			@PathVariable int pageNum) {
		
		LOGGER.info("UserController | listByPage is started");
		
		service.listByPage(pageNum, helper);

		return "users/users";	
	}
	
	@GetMapping("/users/new")
	public String newUser(Model model) {
		
		LOGGER.info("UserController | newUser is called");
		
		List<Role> listRoles = service.listRoles();
		
		LOGGER.info("UserController | newUser | listRoles.size() : " + listRoles.size());
		
		User user = new User();
		user.setEnabled(true);
		
		LOGGER.info("UserController | newUser | user : " + user.toString());
		
		model.addAttribute("user", user);
		model.addAttribute("listRoles", listRoles);
		model.addAttribute("pageTitle", "Create New User");
		
		return "users/user_form";
	}
	
	@PostMapping("/users/save")
	public String saveUser(User user, RedirectAttributes redirectAttributes, 
			@RequestParam("image") MultipartFile multipartFile) throws IOException {
		
		LOGGER.info("UserController | saveUser is called");
		
		LOGGER.info("UserController | saveUser | multipartFile : " + multipartFile);

		if (!multipartFile.isEmpty()) {
				String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
				
				LOGGER.info("UserController | saveUser | fileName : " + fileName);
				
				user.setPhotos(fileName);
				User savedUser = service.save(user);
				
				LOGGER.info("UserController | saveUser | savedUser : " + savedUser.toString());
	
				String uploadDir = "user-photos/" + savedUser.getId();
				
				LOGGER.info("UserController | saveUser | uploadDir : " + uploadDir);
	
				// Image Folder
				FileUploadUtil.cleanDir(uploadDir);
				FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
			
	
		} else {
			
			LOGGER.info("UserController | saveUser | user.getPhotos() : " + user.getPhotos());
			
			if (user.getPhotos().isEmpty()) user.setPhotos(null);
			service.save(user);
			
			LOGGER.info("UserController | saveUser | save completed");
		}
		
		
		redirectAttributes.addFlashAttribute("messageSuccess", "The user has been saved successfully.");
		
		return DirectUtil.getRedirectURL(user);
	}
	
	@GetMapping("/users/edit/{id}")
	public String editUser(@PathVariable Integer id, 
			Model model,
			RedirectAttributes redirectAttributes) {
		
		LOGGER.info("UserController | editUser is called");
		
		try {
			User user = service.get(id);
			
			LOGGER.info("UserController | editUser | user : " + user.toString());
			
			List<Role> listRoles = service.listRoles();
			
			LOGGER.info("UserController | editUser | listRoles.size() : " + listRoles.size());
			
			model.addAttribute("user", user);
			model.addAttribute("pageTitle", "Edit User (ID: " + id + ")");
			model.addAttribute("listRoles", listRoles);
			
			return "users/user_form";
			
		} catch (UserNotFoundException ex) {
			
			LOGGER.error("UserController | editUser | ex.getMessage() : " + ex.getMessage());
			
			redirectAttributes.addFlashAttribute("messageError", ex.getMessage());
			return defaultRedirectURL;
		}
	}
	
	@GetMapping("/users/delete/{id}")
	public String deleteUser(@PathVariable Integer id, 
			Model model,
			RedirectAttributes redirectAttributes) {
		
		LOGGER.info("UserController | deleteUser is called");
		
		try {
			service.delete(id);
			
			String userPhotosDir = "user-photos/" + id;
			
			LOGGER.info("CategoryController | deleteUser | userPhotosDir : " + userPhotosDir);
			
			// Image Folder
			FileUploadUtil.removeDir(userPhotosDir);
			
		
			
			LOGGER.info("UserController | deleteUser | delete completed");
			
			redirectAttributes.addFlashAttribute("messageSuccess", 
					"The user ID " + id + " has been deleted successfully");
		} catch (UserNotFoundException ex) {
			
			LOGGER.error("UserController | deleteUser | ex.getMessage() : " + ex.getMessage());
			
			redirectAttributes.addFlashAttribute("messageError", ex.getMessage());
		}
		
		return defaultRedirectURL;
	}
	
	@GetMapping("/users/{id}/enabled/{status}")
	public String updateUserEnabledStatus(@PathVariable Integer id,
			@PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
		
		LOGGER.info("UserController | updateUserEnabledStatus is called");
		
		service.updateUserEnabledStatus(id, enabled);
		
		LOGGER.info("UserController | updateUserEnabledStatus completed");
		
		String status = enabled ? "enabled" : "disabled";
		
		LOGGER.info("UserController | updateUserEnabledStatus | status : " + status);
		
		String message = "The user ID " + id + " has been " + status;
		
		LOGGER.info("UserController | updateUserEnabledStatus | message : " + message);
		
		if(message.contains("enabled")) {
			redirectAttributes.addFlashAttribute("messageSuccess", message);
		}else {
			redirectAttributes.addFlashAttribute("messageError", message);
		}
		
		
		return defaultRedirectURL;
	}
	
	@GetMapping("/users/export/csv")
	public void exportToCSV(HttpServletResponse response) throws IOException {
		
		LOGGER.info("UserController | exportToCSV is called");
		
		List<User> listUsers = service.listAll();
		
		LOGGER.info("UserController | exportToCSV | listUsers.size() : " + listUsers.size());
		
		UserCsvExporter exporter = new UserCsvExporter();
		
		
		LOGGER.info("UserController | exportToCSV | export is starting");
		
		exporter.export(listUsers, response);
		
		LOGGER.info("UserController | exportToCSV | export completed");
	}
	
	@GetMapping("/users/export/excel")
	public void exportToExcel(HttpServletResponse response) throws IOException {
		
		LOGGER.info("UserController | exportToExcel is called");
		
		List<User> listUsers = service.listAll();
		
		LOGGER.info("UserController | exportToExcel | listUsers.size() : " + listUsers.size());

		UserExcelExporter exporter = new UserExcelExporter();
		
		LOGGER.info("UserController | exportToExcel | export is starting");
		
		exporter.export(listUsers, response);
		
		LOGGER.info("UserController | exportToExcel | export completed");
	}
	
	@GetMapping("/users/export/pdf")
	public void exportToPDF(HttpServletResponse response) throws IOException {
		
		LOGGER.info("UserController | exportToPDF is called");
		
		List<User> listUsers = service.listAll();
		
		LOGGER.info("UserController | exportToPDF | listUsers.size() : " + listUsers.size());
		
		UserPdfExporter exporter = new UserPdfExporter();
		
		LOGGER.info("UserController | exportToPDF | export is starting");
		
		exporter.export(listUsers, response);
		
		LOGGER.info("UserController | exportToPDF | export completed");
	}	
}

