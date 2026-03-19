package com.example.goormtodoback.service;

import com.example.goormtodoback.common.exception.CustomException;
import com.example.goormtodoback.domain.entity.User;
import com.example.goormtodoback.domain.dto.auth.LoginRequest;
import com.example.goormtodoback.domain.dto.auth.LoginResponse;
import com.example.goormtodoback.domain.dto.auth.RegisterRequest;
import com.example.goormtodoback.domain.dto.auth.RegisterResponse;
import com.example.goormtodoback.jwt.JwtUtil;
import com.example.goormtodoback.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    private User createUser() {
        return User.builder()
                .username("goorm")
                .nickname("구름")
                .email("goorm@gmail.com")
                .passwordHash("encodedPassword")
                .build();
    }

    @Test
    @DisplayName("회원가입 - 성공")
    void register_success() {
        RegisterRequest request = new RegisterRequest("goorm", "구름", "password123", "goorm@gmail.com");

        given(userRepository.existsByUsername("goorm")).willReturn(false);
        given(userRepository.existsByEmail("goorm@gmail.com")).willReturn(false);
        given(passwordEncoder.encode("password123")).willReturn("encodedPassword");
        given(userRepository.save(any(User.class))).willReturn(createUser());

        RegisterResponse response = authService.register(request);

        assertThat(response.getUsername()).isEqualTo("goorm");
        assertThat(response.getNickname()).isEqualTo("구름");
        assertThat(response.getEmail()).isEqualTo("goorm@gmail.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 실패 - username 중복")
    void register_fail_duplicateUsername() {
        given(userRepository.existsByUsername(any())).willReturn(true);

        assertThatThrownBy(() -> authService.register(new RegisterRequest()))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("이미 사용 중인 아이디입니다.");
    }

    @Test
    @DisplayName("회원가입 실패 - email 중복")
    void register_fail_duplicateEmail() {
        given(userRepository.existsByUsername(any())).willReturn(false);
        given(userRepository.existsByEmail(any())).willReturn(true);

        assertThatThrownBy(() -> authService.register(new RegisterRequest()))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("이미 사용 중인 이메일입니다.");
    }

    @Test
    @DisplayName("로그인 성공")
    void login_success() {
        User user = createUser();

        given(userRepository.findByUsername(any())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(any(), any())).willReturn(true);
        given(jwtUtil.generateToken(anyString())).willReturn("fake-token");

        LoginResponse response = authService.login(new LoginRequest());

        assertThat(response.getAccessToken()).isEqualTo("fake-token");
    }

    @Test
    @DisplayName("로그인 실패 - 없는 username")
    void login_fail_userNotFound() {
        given(userRepository.findByUsername(any())).willReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(new LoginRequest()))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("비밀번호가 일치하지 않습니다.");
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_fail_wrongPassword() {
        User user = createUser();

        given(userRepository.findByUsername(any())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(any(), any())).willReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginRequest()))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("비밀번호가 일치하지 않습니다.");
    }
}