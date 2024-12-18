package com.websales.admin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfiguration {

    @Bean
    public UserDetailsService userDetailsService() {
        return new WebsalesUserDetailsService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auProvider = new DaoAuthenticationProvider();
        auProvider.setUserDetailsService(userDetailsService());
        auProvider.setPasswordEncoder(passwordEncoder());
        return auProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
            		.requestMatchers("/users/**")
            		.hasAuthority("Admin")
            		.requestMatchers("/categories/**", "/brands/**").hasAnyAuthority("Admin", "Storekeeper")
            		.anyRequest()
            		.authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login").usernameParameter("email").permitAll()
            ).logout(logout ->
            	logout.permitAll()
            ).rememberMe(rememberMe -> 
            	rememberMe.key("uniqueSecret").tokenValiditySeconds(604800))
            .httpBasic(withDefaults());
        return http.build();
    }
    
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
    	return (web) -> web.ignoring().requestMatchers("/images/**", "/webjars/**", "/css/**", "/js/**"); 
    	}
    
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//            .authorizeHttpRequests(authz -> authz
//           
//                .anyRequest().permitAll()
//            )
////            .formLogin(form -> form
////                .loginPage("/login").usernameParameter("email").permitAll()
////            )
//            .httpBasic(withDefaults());
//        return http.build();
//    }
}
