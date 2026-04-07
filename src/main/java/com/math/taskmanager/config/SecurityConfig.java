package com.math.taskmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/login",
                    "/error",
                    "/css/**",
                    "/js/**",

                    //  LIBERAR PRIMEIRO ACESSO
                    "/auth/first-access",
                    "/auth/me",

                    //  LIBERAR SETORES (ESSENCIAL)
                    "/sectors/**"
                ).permitAll()
                .anyRequest().authenticated()
            )

            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/index.html", true)
                .permitAll()
            )

            .logout(logout -> logout
                .logoutSuccessUrl("/login")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}