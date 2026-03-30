package com.example.goormtodoback.controller;

import com.example.goormtodoback.common.exception.CustomException;
import com.example.goormtodoback.common.exception.ErrorCode;
import com.example.goormtodoback.domain.dto.user.UserSearchResponseDto;
import com.example.goormtodoback.domain.entity.User;
import com.example.goormtodoback.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private UserSearchResponseDto userBDto;
    private UserSearchResponseDto userCDto;

    @BeforeEach
    void setUp() throws Exception {
        User userB = User.builder()
                .username("testUserB")
                .nickname("테스트유저B")
                .email("testUserB@test.com")
                .passwordHash("encoded")
                .build();
        setId(userB, 2L);

        User userC = User.builder()
                .username("testUserC")
                .nickname("테스트유저C")
                .email("testUserC@test.com")
                .passwordHash("encoded")
                .build();
        setId(userC, 3L);

        userBDto = UserSearchResponseDto.from(userB);;
        userCDto = UserSearchResponseDto.from(userC);
    }

    private void setId(User user, Long id) {
        try {
            var field = User.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(user, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("닉네임 검색 - 성공 (200)")
    @WithMockUser(username = "testUserA")
    void searchUsers_success() throws Exception {
        given(userService.searchByNickname(any(), any()))
                .willReturn(List.of(userBDto, userCDto));

        mockMvc.perform(get("/api/v1/users/search")
                .param("nickname", "테스트"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].username").value("testUserB"))
                .andExpect(jsonPath("$.data[0].nickname").value("테스트유저B"));
    }

    @Test
    @DisplayName("닉네임 검색 - 검색 결과가 없으면 빈 배열 반환 (200)")
    @WithMockUser(username = "testUserA")
    void searchUsers_emptyResult() throws Exception {
        given(userService.searchByNickname(any(), any()))
                .willReturn(List.of());

        mockMvc.perform(get("/api/v1/users/search")
                        .param("nickname", "존재하지않는닉네임"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("닉네임 검색 - nickname 파라미터가 없으면 400 반환")
    @WithMockUser(username = "testUserA")
    void searchUser_missingParam() throws Exception {
        mockMvc.perform(get("/api/v1/users/search"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("닉네임 검색 - 토큰이 없으면 403 반환")
    void searchUser_unauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/users/search")
                .param("nickname", "테스트"))
                .andExpect(status().isForbidden());

    }

    @Test
    @DisplayName("닉네임 검색 - 로그인 유저가 없으면 404 반환")
    @WithMockUser(username = "unknownUser")
    void searchUser_notFount() throws Exception {
        given(userService.searchByNickname(any(), any()))
                .willThrow(new CustomException(ErrorCode.USER_NOT_FOUND));

        mockMvc.perform(get("/api/v1/users/search")
                .param("nickname", "테스트"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
    }
}

