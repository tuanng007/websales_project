package com.websales.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderTest {

	@Test
	public void testEncodePassword() { 
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String rawPassword = "123";
		String encoderPass = passwordEncoder.encode(rawPassword);
		
		System.out.println(encoderPass);
		
		boolean matches = passwordEncoder.matches(rawPassword, encoderPass);
		
		assertThat(matches).isTrue();
	}
}
