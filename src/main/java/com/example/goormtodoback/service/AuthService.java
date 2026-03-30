package com.example.goormtodoback.service;

import com.example.goormtodoback.common.exception.CustomException;
import com.example.goormtodoback.common.exception.ErrorCode;
import com.example.goormtodoback.domain.entity.User;
import com.example.goormtodoback.domain.dto.auth.LoginRequest;
import com.example.goormtodoback.domain.dto.auth.LoginResponse;
import com.example.goormtodoback.domain.dto.auth.RegisterRequest;
import com.example.goormtodoback.domain.dto.auth.RegisterResponse;
import com.example.goormtodoback.jwt.JwtUtil;
import com.example.goormtodoback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new CustomException(ErrorCode.USERNAME_DUPLICATED);
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_DUPLICATED);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .username(request.getUsername())
                .nickname(request.getNickname())
                .email(request.getEmail())
                .passwordHash(encodedPassword)
                .build();

        userRepository.save(user);

        return new RegisterResponse(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }

    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_PASSWORD));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        String token = jwtUtil.generateToken(user.getUsername());

        return new LoginResponse(token);
    }
}