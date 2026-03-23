package com.example.goormtodoback.service;

import com.example.goormtodoback.common.exception.CustomException;
import com.example.goormtodoback.common.exception.ErrorCode;
import com.example.goormtodoback.domain.dto.user.UserSearchResponseDto;
import com.example.goormtodoback.domain.entity.User;
import com.example.goormtodoback.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;  // ← 추가

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User loginUser;
    private User testUserB;
    private User testUserC;

    @BeforeEach
    void setUp() {
        loginUser = User.builder()
                .username("testUserA")
                .nickname("테스트유저A")
                .email("testUserA@test.com")
                .passwordHash("encoded")
                .build();
        setId(loginUser, 1L);

        testUserB = User.builder()
                .username("testUserB")
                .nickname("테스트유저B")
                .email("testUserB@test.com")
                .passwordHash("encoded")
                .build();
        setId(testUserB, 2L);

        testUserC = User.builder()
                .username("testUserC")
                .nickname("테스트유저C")
                .email("testUserC@test.com")
                .passwordHash("encoded")
                .build();
        setId(testUserC, 3L);
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
    @DisplayName("닉네임 검색 - 성공 (관련 닉네임 반환)")
    void searchByNickname_success() {
        // userRepository.findByUsername()을 호출하여 loginUser(현재 로그인이 되어 있는 사람)을 반환
        given(userRepository.findByUsername("testUserA"))
                .willReturn(Optional.of(loginUser));

        given(userRepository.findByNicknameContaining("테스트"))
                .willReturn(List.of(loginUser, testUserB, testUserC));

        // 서비스 결과를 뱉을 수 있게 서비스를 동작
        List<UserSearchResponseDto> result = userService.searchByNickname("testUserA", "테스트");

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting("username")
                .containsExactlyInAnyOrder("testUserB", "testUserC");
    }

    @Test
    @DisplayName("닉네임 검색 - 본인만 검색되면 빈 배열을 반환")
    void searchByNickname_onlyMe() {
        // userRepository.findByUsername()을 호출하여 loginUser(현재 로그인이 되어 있는 사람)을 반환
        given(userRepository.findByUsername("testUserA"))
                .willReturn(Optional.of(loginUser));

        // 본인이름을 검색
        given(userRepository.findByNicknameContaining("테스트유저A"))
                .willReturn(List.of(loginUser));

        // 서비스 결과를 뱉을 수 있게 서비스를 동작
        List<UserSearchResponseDto> result = userService.searchByNickname("testUserA", "테스트");

        // 빈 배열 반환
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("닉네임 검색 - 검색 결과가 없으면 빈 배열 반환")
    void searchByNickname_noResult() {
        // userRepository.findByUsername()을 호출하여 loginUser(현재 로그인이 되어 있는 사람)을 반환
        given(userRepository.findByUsername("testUserA"))
                .willReturn(Optional.of(loginUser));

        // 검색 결과가 없는 경우
        given(userRepository.findByNicknameContaining("존재하지않는닉네임"))
                .willReturn(List.of());

        // 서비스 결과를 뱉을 수 있게 서비스를 동작
        List<UserSearchResponseDto> result = userService.searchByNickname("testUserA", "테스트");

        // 빈 배열 반환
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("닉네임 검색 - 로그인 유저가 존재하지 않으면 USER_NOT_FOUND 예외")
    void searchByNickname_loginUserNotFound() {

        // 존재하지 않는 유저로 조회
        given(userRepository.findByUsername("unknownUser"))
                .willReturn(Optional.empty());


        // CustomException이 발생하고 에러 코드가 USER_NOT_FOUND인지 확인
        assertThatThrownBy(() ->
                userService.searchByNickname("unknownUser", "모르는닉네임"))
                .isInstanceOf(CustomException.class)
                .satisfies(e -> {
                    CustomException ce = (CustomException) e;
                    assertThat(ce.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
                });
    }

    @Test
    @DisplayName("닉네임 검색 - 응답 DTO 필드 검증")
    void searchByNickname_dtoFieldCheck() {
        // given
        given(userRepository.findByUsername("testUserA"))
                .willReturn(Optional.of(loginUser));

        given(userRepository.findByNicknameContaining("테스트유저B"))
                .willReturn(List.of(testUserB));

        // when
        List<UserSearchResponseDto> result = userService.searchByNickname("testUserA", "테스트유저B");

        // then
        // → 반환된 DTO의 필드값이 올바른지 검증
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(2L);
        assertThat(result.get(0).getUsername()).isEqualTo("testUserB");
        assertThat(result.get(0).getNickname()).isEqualTo("테스트유저B");
    }
}

