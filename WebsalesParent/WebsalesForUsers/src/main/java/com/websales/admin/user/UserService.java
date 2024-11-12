package com.websales.admin.user;

import java.util.List;
import java.util.NoSuchElementException;

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
	
	public List<User> listAll() { 
		
		return (List<User>) userRepo.findAll();
	}
	
	public List<Role> listRoles() { 
		
		return (List<Role>) roleRepo.findAll();
	}
	
	private void encodePassword(User user) { 
		String encodePassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodePassword);
	}
	
	
	public User get(Integer id) throws UserNotFoundException {
		try { 
			return userRepo.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new UserNotFoundException("Could not find any user with ID: " + id);
		}
	}
	
	// Kiểm tra email có phải duy nhất ?
	public boolean isEmailUnique(Integer id ,String email) { 
		User userByEmail = userRepo.getUserByEmail(email); 
		
		if (userByEmail == null) return true; // Nếu không có email trong hệ thống thì là oke
		
		boolean isCreatingNewUser = (id == null); // Kiểm tra xem có phải đang tạo mới User hay không
		
		if(isCreatingNewUser)  {  
			if(userByEmail != null) return false; // Tạo mới user mà giá trị email không bằng null thì lỗi
		} else { 
			if(userByEmail.getId() != id)  return false; // Cập nhật thông tin user mà id user không trùng với id cũng lỗi
		}
		
		return true;
	}
	
	public void save(User user) { 
		boolean isUpdatingUser = (user.getId() != null);
		
		if(isUpdatingUser) { 
			User existingUser = userRepo.findById(user.getId()).get();
			
			if(user.getPassword().isEmpty()) { 
				user.setPassword(existingUser.getPassword());
			} else {
				encodePassword(user);
			}
		}else { 
			encodePassword(user);
		}
		 	userRepo.save(user);
	}


	

 	
 }
