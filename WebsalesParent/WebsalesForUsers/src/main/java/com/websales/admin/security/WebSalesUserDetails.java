package com.websales.admin.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.websales.common.entity.Role;
import com.websales.common.entity.User;

public class WebSalesUserDetails implements UserDetails {
	
	private User user;
	
	public WebSalesUserDetails(User user) { 
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Set<Role> roles = user.getRoles();
		
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		
		for(Role role : roles) {
			authorities.add(new SimpleGrantedAuthority(role.getName()));
		}
		
		return authorities;
	}

	@Override
	public String getPassword() {
		
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		
		return user.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		
		return true;
	}

	@Override
	public boolean isEnabled() {
		return user.isEnabled();
	}
	
	public String getFullname() { 
		return this.user.getFirstName() + " " + this.user.getLastName();
	}
	
	
	public void setFirstName(String firstName) {
		this.user.setFirstName(firstName);
	}
	
	public boolean hasRole(String roleName)  { 
		return user.hasRole(roleName);
	}
	
	public void setLastName(String lastName) {
		this.user.setLastName(lastName);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	
}
