package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration

public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // CHO PHÉP TẤT CẢ MỌI REQUEST TRUY CẬP MÀ KHÔNG CẦN ĐĂNG NHẬP
                        .anyRequest().permitAll() // <--- Dòng quan trọng này cho phép tất cả
                )

                .formLogin(formLogin -> formLogin
                        .loginPage("/login") // Vẫn có thể có trang login nếu muốn
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/perform_logout") // (Tùy chọn) URL để kích hoạt đăng xuất, POST được ưu tiên
                        // Mặc định Spring Security dùng /logout
                        .logoutSuccessUrl("/")        // **CHUYỂN HƯỚNG VỀ TRANG CHỦ (/) SAU KHI ĐĂNG XUẤT**
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID") // (Tùy chọn)
                        .permitAll()
                )
                .csrf(csrf -> csrf.disable()); // Tạm thời tắt CSRF

        return http.build();
    }
}
