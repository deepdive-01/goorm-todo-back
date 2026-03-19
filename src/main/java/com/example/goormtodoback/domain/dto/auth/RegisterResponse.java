package com.example.goormtodoback.domain.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class RegisterResponse {
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private LocalDateTime createdAt;
}
