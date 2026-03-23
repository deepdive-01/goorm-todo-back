package com.example.goormtodoback.controller;

import com.example.goormtodoback.common.response.ApiResponse;
import com.example.goormtodoback.domain.dto.user.UserSearchResponseDto;
import com.example.goormtodoback.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService; // 서비스 생성

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<UserSearchResponseDto>>> searchUsers (
            @AuthenticationPrincipal String username,
            @RequestParam String nickname
    ) {
        List<UserSearchResponseDto> response = userService.searchByNickname(username, nickname); // 유저 이름과 닉네임을 입력받아서
        return ResponseEntity
                .ok(ApiResponse.success("유저 검색 결과입니다.", response));
    }

}
