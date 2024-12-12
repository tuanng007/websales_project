package com.websales.admin.user;

import java.util.List;

import com.websales.common.entity.Role;
import com.websales.common.entity.User;

public interface IUserService {

	public List<User> listAll();
	
	public List<Role> listRoles();
	
	public User save(User user);
	
	public boolean isEmailUnique(Integer id, String email);
	
	public User get(Integer id) throws UserNotFoundException;
	
	public void delete(Integer id) throws UserNotFoundException;
	
	public void updateUserEnabledStatus(Integer id, boolean enabled);
	
	public void listByPage(int pageNum, com.websales.admin.paging.PagingAndSortingHelper helper);
	
	public User getByEmail(String email);
	
	public User updateAccount(User userInForm);
}
