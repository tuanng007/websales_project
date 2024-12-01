package com.websales.admin.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserRestController {
	@Autowired
	private UserService userService;
	
	@PostMapping("/user/check_email")
	public String checkDuplicateEmail(@Param("id") Integer id, @Param("email") String email) { 
		
		return userService.isEmailUnique(id, email) ? "OK" : "DUPLICATED";
	}
}