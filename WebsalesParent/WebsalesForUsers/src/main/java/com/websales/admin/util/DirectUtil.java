package com.websales.admin.util;

import com.websales.common.entity.User;

public class DirectUtil {
	
	public static String getRedirectURL(User user)  {  
		String email = user.getEmail(); 
		return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + email;
	} 
}
