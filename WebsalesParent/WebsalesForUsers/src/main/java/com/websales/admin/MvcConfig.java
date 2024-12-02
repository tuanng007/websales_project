package com.websales.admin;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		String userPhotodirName = "user-photos";
		exposeDirectory(userPhotodirName, registry);
		
		String categoryImageDirName = "../category-images";
		exposeDirectory(categoryImageDirName, registry);
		
		String brandLogoDirName = "../brand-logos";
		exposeDirectory(brandLogoDirName, registry);
		
		String productImageDirName = "../product-images";
		exposeDirectory(productImageDirName, registry);
	}
	
	private void exposeDirectory(String pathPattern, ResourceHandlerRegistry registry) { 
		Path path = Paths.get(pathPattern);
		String absolutePath = path.toFile().getAbsolutePath();
		
		String logicalPath = pathPattern.replace("../", "") + "/**";
		
		registry.addResourceHandler(logicalPath).addResourceLocations("file:/" +  absolutePath + "/");
	}
	
}
