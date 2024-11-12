package com.websales.admin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfiguration {
	
	@Bean
	public PasswordEncoder PasswordEncoder() { 
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception { 
		http
        .authorizeHttpRequests((authz) -> authz
            .anyRequest().permitAll()
        )
        .httpBasic(withDefaults());
    return http.build();
	}
}
