package com.rev.app.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;
        private final CustomAuthenticationSuccessHandler successHandler;

        public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                        CustomAuthenticationSuccessHandler successHandler) {
                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
                this.successHandler = successHandler;
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(AbstractHttpConfigurer::disable)
                                .authorizeHttpRequests(authz -> authz
                                                // Permit public UI and Auth endpoints
                                                .requestMatchers("/", "/login", "/login/artist",
                                                                "/register/**", "/forgot-password/**",
                                                                "/api/auth/**", "/css/**", "/js/**", "/uploads/**",
                                                                "/error")
                                                .permitAll()
                                                // Require roles for specific dashboards
                                                .requestMatchers("/user/**").hasAnyRole("LISTENER", "ARTIST")
                                                .requestMatchers("/artist/**").hasRole("ARTIST")
                                                // Require auth for APIs
                                                .requestMatchers("/api/**").authenticated()
                                                .anyRequest().authenticated())
                                // Hybrid Security Logic:
                                // 1. Session-based for Thymeleaf UI (Form Login)
                                // 2. JWT-based for REST API (Postman/REST testing)
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .loginProcessingUrl("/perform_login")
                                                .successHandler(successHandler) // Use the custom redirect handler
                                                .failureUrl("/login?error=true")
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/?logout=true")
                                                .permitAll())
                                // Keep the JWT filter for API requests (Postman testing)
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }
}
