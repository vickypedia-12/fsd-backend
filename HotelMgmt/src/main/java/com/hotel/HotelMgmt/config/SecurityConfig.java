package com.hotel.HotelMgmt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // Disable CSRF for stateless JWT-based authentication
                .authorizeHttpRequests() // Updated method for configuring authorization
                // .requestMatchers("/api/users/signup", "/api/users/verify",
                // "/api/users/login",
                // "/api/user-preferences/create-or-update",
                // "/api/users/forgot-password/send-otp",
                // "/api/users/forgot-password/verify-otp", "/api/users/forgot-password/reset",
                // "/api/admins/signup", "/api/admins/login", "/api/admins/verify",
                // "/api/admins/login/verify-otp",
                // "/error")
                // .permitAll() // Public
                // .requestMatchers("/api/user-preferences/**").authenticated()
                .anyRequest().permitAll()// Secure all other endpoints
                .and()
                .httpBasic().disable() // Disable basic authentication
                .formLogin().disable(); // Disable form-based login

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}