package com.example.goormtodoback.config;

import com.example.goormtodoback.jwt.JwtFilter;
import com.example.goormtodoback.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Security 설정
// 요청이 들어왔을 때 보안 검사를 진행

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsConfig corsConfig;
    private final JwtUtil jwtUtil;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화
                // 웹 공격 방어 기능이지만 JWT 방식에선 사용하지 않음
                .csrf(AbstractHttpConfigurer::disable)

                // 세션 사용 안 함
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // CORS 설정 적용
                // CorsConfig를 연결
                .cors(cors -> cors.configurationSource(corsConfig.corsConfigurationSource()))

                // 요청 권한 설정
                // 로그인과 회원가입 경로는 누구나 접근할 수 있게 하고
                .authorizeHttpRequests(auth -> auth
                        // 인증 없이 허용할 경로
                        .requestMatchers(
                                "/api/v1/auth/**",  // 로그인, 회원가입
                                "/api/v1/quotes/**", // 명언 조회의 경우 로그인 없이 허용
                                "/swagger-ui/**",   // Swagger
                                "/v3/api-docs/**"   // Swagger
                        ).permitAll()
                        // 나머지는 인증 필요
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        new JwtFilter(jwtUtil),
                        UsernamePasswordAuthenticationFilter.class
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}