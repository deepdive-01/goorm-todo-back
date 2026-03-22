package com.example.goormtodoback.domain.dto.user;

import com.example.goormtodoback.domain.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class UserSearchResponseDto {

    @JsonProperty("user_id") // 유저 아이디
    private final Long id;

    @JsonProperty("username") // 유저 이름 (실명)
    private final String username;

    @JsonProperty("nickname") // 유저 닉네임
    private final String nickname;

    // 생성자
    private UserSearchResponseDto(User user) {
        this.id = user.getId(); // 아이디
        this.username = user.getUsername(); // 이름
        this.nickname = user.getNickname(); // 닉네임
    }

    // DTO로 변환하는 메서드
    public static UserSearchResponseDto from(User user) {
        return new UserSearchResponseDto(user);
    }
}
