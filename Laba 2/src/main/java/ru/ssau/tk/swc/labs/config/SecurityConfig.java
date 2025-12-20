package ru.ssau.tk.swc.labs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        //разрешаем регистрацию, вход и проверки без авторизации
                        .requestMatchers("/api/users/register").permitAll()
                        .requestMatchers("/api/users/auth").permitAll()
                        .requestMatchers("/api/users/check-login").permitAll()
                        .requestMatchers("/api/users/check-email").permitAll()
                        //все остальное требует авторизации
                        .requestMatchers("/api/users/**").authenticated()
                        .anyRequest().authenticated()
                )
                .httpBasic(httpBasic -> {}) //вкл Basic Auth, выдаст ошибку если не удалось авторизоваться
                .csrf(csrf -> csrf.disable()); //выкл CSRF для API

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}