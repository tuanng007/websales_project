package com.websales.admin.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.websales.common.entity.Role;
import com.websales.common.entity.User;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private RoleRepository roleRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	// List all from UserRepository
	public List<User> listAll() { 
		
		return (List<User>) userRepo.findAll();
	}
	
	// List all roles in Database
	public List<Role> listRoles() { 
		
		return (List<Role>) roleRepo.findAll();
	}
	
	// Mã hóa mật khẩu
	private void encodePassword(User user) { 
		String encodePassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodePassword);
	}
	
	public boolean isEmailUnique(String email) { 
		User userByEmail = userRepo.getUserByEmail(email);
		
		// return về null nếu email không có trong hệ thống -> true
		// nếu != null thì email có trong hệ thống rồi
		
		return userByEmail == null;
	}
	
	public void save(User user) { 
		 encodePassword(user);
		 userRepo.save(user);
	}
	

 	
 }
