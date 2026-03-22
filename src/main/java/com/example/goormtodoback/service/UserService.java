package com.example.goormtodoback.service;

import com.example.goormtodoback.common.exception.CustomException;
import com.example.goormtodoback.common.exception.ErrorCode;
import com.example.goormtodoback.domain.dto.user.UserSearchResponseDto;
import com.example.goormtodoback.domain.entity.User;
import com.example.goormtodoback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<UserSearchResponseDto> searchByNickname(String username, String nickname) {
        // 현재 로그인한 유저를 조회
        User loginUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 닉네임으로 유저를 검색하게 되는데
        // 닉네임에 검색어가 포함된 유저 전체를 조회하며
        // 본인을 제외한 나머지를 조회한다.
        return userRepository
                .findByNicknameContaining(nickname) // 닉네임 조회
                .stream()
                .filter(user -> !user.getId().equals(loginUser.getId())) // 본인 제외
                .map(UserSearchResponseDto::from) // DTO로 변환
                .toList();
    }
}
