package com.websales.admin.user;

import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.websales.common.entity.Role;
import com.websales.common.entity.User;

@Controller
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/users")
	public String listAllUser(Model model) { 
		List<User> listUsers = userService.listAll();
		model.addAttribute("listUsers", listUsers);
		
		return "users";
	}
	
	@GetMapping("/users/new")
	public String newUser(Model model) {
		
		List<Role> listRoles = userService.listRoles();
		User user = new User();
		user.setEnabled(true);
		model.addAttribute("user", user);
		model.addAttribute("listRoles", listRoles);
		model.addAttribute("pageTitle", "Create New User");
		return "user_form";
	}
	
	
	@GetMapping("/users/edit/{id}")
	public String editUser(@PathVariable(name="id") Integer id, Model model, RedirectAttributes redirectAttributes) { 
		
		try { 
			User user = userService.get(id);
			List<Role> listRoles = userService.listRoles();
			
			model.addAttribute("user", user);
			model.addAttribute("listRoles", listRoles);
			model.addAttribute("pageTitle", "Edit User ID: " + id);
			return "user_form";
		}catch (UserNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message", ex);
			return "redirect:/users";
		}
	}
	
	@GetMapping("users/delete/{id}")
	public String deleteUser(@PathVariable(name="id") Integer id, Model model, RedirectAttributes redirectAttributes) { 
		
		try { 
			userService.delete(id) ;
			redirectAttributes.addFlashAttribute("message", "The user ID " + id + " has been deleted successfully");
			
		}catch (UserNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message", ex);
		}
		
		return "redirect:/users";
	}
	
	
	@GetMapping("users/{id}/enabled/{status}")
	public String changeUserEnabledStatus(@PathVariable(name="id") Integer id, @PathVariable(name="status") boolean enabled, 
			RedirectAttributes redirectAttributes) {
		
		userService.updateUserEnabledStatus(id, enabled);
		String status = enabled ? "enabled" : "disabled";
		
		redirectAttributes.addFlashAttribute("message", "The user ID " + id + " has been " + status);
		return "redirect:/users";
	} 
	
	@PostMapping("/users/save")
	public String saveUser(User user, RedirectAttributes redirectAttributes, @RequestParam("image") MultipartFile multipartFile) throws IOException { 
		
		if(!multipartFile.isEmpty())  { 
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			user.setPhotos(fileName);
			User savedUser = userService.save(user);
			
			String uploadDir = "user-photos/" + savedUser.getId();
			
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

		} else { 
			if(user.getPhotos().isEmpty()) user.setPhotos(null);
			userService.save(user);
		}
						
		redirectAttributes.addFlashAttribute("message", "The user has been saved successfully!");
		
		return "redirect:/users";
	}
 }
