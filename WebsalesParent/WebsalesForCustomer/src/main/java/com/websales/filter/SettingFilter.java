package com.websales.filter;

import java.io.IOException;
import java.util.List;

 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.websales.common.entity.Setting;
import com.websales.service.SettingService;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

 

@Component
@Order(-121) // use this value to fix Logout Error of Customer already signed out
// Default value -100 (https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html -> spring.security.filter.order)
// Select any value less than default value
public class SettingFilter implements Filter {

 	
	@Autowired
	private SettingService service; 
 
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		
 		
		HttpServletRequest servletRequest = (HttpServletRequest) request;
		String url = servletRequest.getRequestURL().toString();
		
 
		if (url.endsWith(".css") || url.endsWith(".js") || url.endsWith(".png") ||
				url.endsWith(".jpg")) {
 			chain.doFilter(request, response);
			return;
		}
		
		loadGeneralSettings(request);
 
		chain.doFilter(request, response);

	}
	
 
	private void loadGeneralSettings(ServletRequest request) {
		
 		
		List<Setting> generalSettings = service.getGeneralSettings();

		generalSettings.forEach(setting -> {
 			request.setAttribute(setting.getKey(), setting.getValue());
		});


	}

}
