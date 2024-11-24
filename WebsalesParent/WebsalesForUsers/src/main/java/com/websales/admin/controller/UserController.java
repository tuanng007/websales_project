package com.websales.admin.controller;

import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.List;

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

import com.websales.admin.FileUploadUtil;
import com.websales.admin.exception.UserNotFoundException;
import com.websales.admin.export.UserCsvExporter;
import com.websales.admin.export.UserExcelExporter;
import com.websales.admin.export.UserPdfExporter;
import com.websales.admin.service.UserService;
import com.websales.common.entity.Role;
import com.websales.common.entity.User;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/users")
	public String listFirstPage(Model model) { 
		return listByPage(1, model,"firstName", "asc", null);
	}
	
	
	@GetMapping("/users/page/{pageNum}"	)
	public String listByPage(@PathVariable(name="pageNum") int pageNum, Model model
			, @Param("sortField") String sortField, @Param("sortDir") String sortDir, @Param("keyword") String keyword) {
		Page<User> page = userService.listByPage(pageNum, sortField, sortDir, keyword);
		List<User> listUsers = page.getContent();
	
		long startCount = (pageNum - 1) * UserService.USERS_PER_PAGE + 1;
		long endCount = startCount + UserService.USERS_PER_PAGE - 1;
		if(endCount > page.getTotalElements()) { 
			endCount = page.getTotalElements();
		}
		
		String reSort = sortDir.equals("asc") ? "desc" : "asc"; 
		
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reSort", reSort);
		model.addAttribute("keyword", keyword);
		model.addAttribute("listUsers", listUsers);
		
		return "/users/users";
	} 
	
	@GetMapping("/users/new")
	public String newUser(Model model) {
		
		List<Role> listRoles = userService.listRoles();
		User user = new User();
		user.setEnabled(true);
		model.addAttribute("user", user);
		model.addAttribute("listRoles", listRoles);
		model.addAttribute("pageTitle", "Create New User");
		return "/users/user_form";
	}
	
	
	@GetMapping("/users/edit/{id}")
	public String editUser(@PathVariable(name="id") Integer id, Model model, RedirectAttributes redirectAttributes) { 
		
		try { 
			User user = userService.get(id);
			List<Role> listRoles = userService.listRoles();
			
			model.addAttribute("user", user);
			model.addAttribute("listRoles", listRoles);
			model.addAttribute("pageTitle", "Edit User ID: " + id);
			return "/users/user_form";
		}catch (UserNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message", ex);
			return "redirect:/users";
		}
	}
	
	@GetMapping("/users/delete/{id}")
	public String deleteUser(@PathVariable(name="id") Integer id, Model model, RedirectAttributes redirectAttributes) { 
		
		try { 
			userService.delete(id) ;
			redirectAttributes.addFlashAttribute("message", "The user ID " + id + " has been deleted successfully");
			
		}catch (UserNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message", ex);
		}
		
		return "redirect:/users";
	}
	
	
	@GetMapping("/users/{id}/enabled/{status}")
	public String changeUserEnabledStatus(@PathVariable(name="id") Integer id, @PathVariable(name="status") boolean enabled, 
			RedirectAttributes redirectAttributes) {
		
		userService.updateUserEnabledStatus(id, enabled);
		String status = enabled ? "enabled" : "disabled";
		
		redirectAttributes.addFlashAttribute("message", "The user ID " + id + " has been " + status);
		return "redirect:/users";
	} 
	

	
	@PostMapping("/users/save")
	public String saveUser(User user, RedirectAttributes redirectAttributes,
			@RequestParam("image") MultipartFile multipartFile) throws IOException { 
		
		if(!multipartFile.isEmpty())  { 
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			user.setPhotos(fileName);
			User savedUser = userService.save(user);
			
			String uploadDir = "user-photos/" + savedUser.getId();
			
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

		} else { 
			if(user.getPhotos().isEmpty()) {
				user.setPhotos(null);
			}
			userService.save(user);
		}
						
		redirectAttributes.addFlashAttribute("message", "The user has been saved successfully!");
		
		String firstPartEmail = user.getEmail().split("@")[0];
		
		return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + firstPartEmail ;
	}
	
	
	@GetMapping("/users/export/csv")
	public void exportToCSV(HttpServletResponse re) throws IOException { 
		List<User> listUsers = userService.listAll();
		UserCsvExporter exporter = new UserCsvExporter();
		exporter.export(listUsers, re);
	} 
	
	
	@GetMapping("/users/export/excel")
	public void exportToExcel(HttpServletResponse re) throws IOException { 
		List<User> listUsers = userService.listAll();
		UserExcelExporter exporter = new UserExcelExporter();
		exporter.export(listUsers, re);
		
	}
	
	@GetMapping("/users/export/pdf")
	public void exportToPdf(HttpServletResponse re) throws IOException { 
		List<User> listUsers = userService.listAll();
		UserPdfExporter exporter = new UserPdfExporter();
		exporter.export(listUsers, re);
	}
 }
