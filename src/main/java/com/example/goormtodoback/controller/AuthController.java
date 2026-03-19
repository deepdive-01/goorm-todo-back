package com.example.goormtodoback.controller;

import com.example.goormtodoback.common.response.ApiResponse;
import com.example.goormtodoback.domain.dto.auth.LoginRequest;
import com.example.goormtodoback.domain.dto.auth.LoginResponse;
import com.example.goormtodoback.domain.dto.auth.RegisterRequest;
import com.example.goormtodoback.domain.dto.auth.RegisterResponse;
import com.example.goormtodoback.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
            @RequestBody RegisterRequest request) {
        RegisterResponse data = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("회원가입이 완료되었습니다.", data));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @RequestBody LoginRequest request) {
        LoginResponse data = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("로그인에 성공했습니다.", data));
    }
}