package com.JDMGod.accounts_service.config;

import com.JDMGod.accounts_service.repo.AccountRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig{
        @Bean
        SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.csrf(csrf -> csrf.disable());
            http.authorizeHttpRequests(auth -> auth
                    // allow Swagger/OpenAPI and health
                    .requestMatchers(
                            "/swagger-ui/**",
                            "/v3/api-docs/**",
                            "/actuator/**"
                    ).permitAll()
                    // allow your APIs (until we add JWT later)
                    .requestMatchers("/api/**").permitAll()
                    // everything else also allowed for now
                    .anyRequest().permitAll()
            );

            // No login page / basic auth prompts
            http.httpBasic(basic -> basic.disable());
            http.formLogin(form -> form.disable());

            return http.build();



        }
    }

