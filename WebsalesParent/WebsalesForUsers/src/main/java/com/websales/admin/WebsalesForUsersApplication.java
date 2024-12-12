package com.websales.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = {"com.websales.common.entity", "com.websales.admin"})
public class WebsalesForUsersApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebsalesForUsersApplication.class, args);
	}

}
