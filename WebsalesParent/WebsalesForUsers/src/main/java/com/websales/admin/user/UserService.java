package com.websales.admin.user;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.websales.common.entity.Role;
import com.websales.common.entity.User;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserService {
	
	public static final int USERS_PER_PAGE = 4;
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private RoleRepository roleRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public List<User> listAll() { 
		
		return (List<User>) userRepo.findAll(Sort.by("firstName").ascending());
	}
	
	public List<Role> listRoles() { 
		
		return (List<Role>) roleRepo.findAll();
	}
	
	public Page<User> listByPage(int pageNum, String sortField, String sortDir, String keyword){ 
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		Pageable pageable = PageRequest.of(pageNum - 1, USERS_PER_PAGE, sort);
		
		if(keyword != null) { 
			return userRepo.findAll(keyword, pageable);
		}
		
		return  userRepo.findAll(pageable);
	}
	
	public User getUserByEmail(String email) { 
		return userRepo.getUserByEmail(email);
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
	
	public void delete(Integer id) throws UserNotFoundException {
		Long countById = userRepo.countById(id);
		if(countById == null || countById == 0) { 
			throw new UserNotFoundException("Could not find any user with ID: " + id);
		} else { 
			userRepo.deleteById(id);
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
	
	
	public void updateUserEnabledStatus(Integer id, boolean enabled) { 
		userRepo.updateEnabledStatus(id, enabled);
	}
	
	public User save(User user) { 
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
		 	return userRepo.save(user);
	}

	
	public User updateAccount(User userInForm) { 
		User userInDataBase = userRepo.findById(userInForm.getId()).get();
		
		if(!userInForm.getPassword().isEmpty()) { 
			userInDataBase.setPassword(userInForm.getPassword());
			encodePassword(userInDataBase);
		}
		
		if(userInForm.getPhotos() != null) { 
			userInDataBase.setPhotos(userInForm.getPhotos());
		}
		
		userInDataBase.setFirstName(userInForm.getFirstName());
		userInDataBase.setLastName(userInForm.getLastName());
		
		return userRepo.save(userInDataBase);
	}

	

 	
 }
