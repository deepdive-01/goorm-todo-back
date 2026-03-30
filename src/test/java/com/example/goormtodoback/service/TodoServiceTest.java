package com.example.goormtodoback.service;

import com.example.goormtodoback.common.exception.CustomException;
import com.example.goormtodoback.domain.dto.todo.TodoRequest;
import com.example.goormtodoback.domain.dto.todo.TodoResponse;
import com.example.goormtodoback.domain.entity.Todo;
import com.example.goormtodoback.domain.entity.User;
import com.example.goormtodoback.repository.TodoRepository;
import com.example.goormtodoback.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @InjectMocks
    private TodoService todoService;

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private UserRepository userRepository;

    private User createUser() {
        return User.builder()
                .username("goorm")
                .nickname("구름")
                .email("goorm@gmail.com")
                .passwordHash("encodedPassword")
                .build();
    }

    private Todo createTodo(User user) {
        return Todo.builder()
                .user(user)
                .title("운동하기")
                .dateType("specific")
                .specificDate(LocalDate.of(2025, 3, 20))
                .build();
    }

    @Test
    @DisplayName("할일 목록 조회 - 성공")
    void getTodos_success() {
        User user = createUser();
        Todo todo = createTodo(user);

        given(userRepository.findByUsername("goorm")).willReturn(Optional.of(user));
        given(todoRepository.findByUser(user)).willReturn(List.of(todo));

        List<TodoResponse> result = todoService.getTodos("goorm", null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("운동하기");
        assertThat(result.get(0).getDateType()).isEqualTo("specific");
    }

    @Test
    @DisplayName("할일 단건 조회 - 성공")
    void getTodo_success() {
        User user = createUser();
        Todo todo = createTodo(user);

        given(userRepository.findByUsername("goorm")).willReturn(Optional.of(user));
        given(todoRepository.findByIdAndUser(1L, user)).willReturn(Optional.of(todo));

        TodoResponse result = todoService.getTodo("goorm", 1L);

        assertThat(result.getTitle()).isEqualTo("운동하기");
        assertThat(result.getDateType()).isEqualTo("specific");
    }

    @Test
    @DisplayName("할일 단건 조회 실패 - 없는 할일")
    void getTodo_fail_notFound() {
        User user = createUser();

        given(userRepository.findByUsername("goorm")).willReturn(Optional.of(user));
        given(todoRepository.findByIdAndUser(999L, user)).willReturn(Optional.empty());

        assertThatThrownBy(() -> todoService.getTodo("goorm", 999L))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("할 일을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("할일 생성 - 성공")
    void createTodo_success() {
        User user = createUser();
        Todo todo = createTodo(user);

        // category, memo 추가
        TodoRequest request = new TodoRequest("운동하기", null, "specific",
                LocalDate.of(2025, 3, 20), null, null, "FOCUS", "오늘 꼭 하기");

        given(userRepository.findByUsername("goorm")).willReturn(Optional.of(user));
        given(todoRepository.save(any(Todo.class))).willReturn(todo);

        TodoResponse result = todoService.createTodo("goorm", request);

        assertThat(result.getTitle()).isEqualTo("운동하기");
        assertThat(result.getDateType()).isEqualTo("specific");
        verify(todoRepository).save(any(Todo.class));
    }

    @Test
    @DisplayName("할일 생성 실패 - 잘못된 dateType")
    void createTodo_fail_invalidDateType() {
        User user = createUser();

        given(userRepository.findByUsername("goorm")).willReturn(Optional.of(user));

        TodoRequest request = new TodoRequest("운동하기", null, "invalid",
                null, null, null, null, null);

        assertThatThrownBy(() -> todoService.createTodo("goorm", request))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("잘못된 입력값입니다.");
    }

    @Test
    @DisplayName("할일 수정 - 성공")
    void updateTodo_success() {
        User user = createUser();
        Todo todo = createTodo(user);

        TodoRequest request = new TodoRequest("운동하기 (수정)", true, null, null, null, null, null, null);

        given(userRepository.findByUsername("goorm")).willReturn(Optional.of(user));
        given(todoRepository.findByIdAndUser(1L, user)).willReturn(Optional.of(todo));

        todoService.updateTodo("goorm", 1L, request);

        // update 메서드가 실제로 호출됐는지 확인
        assertThat(todo.getTitle()).isEqualTo("운동하기 (수정)");
        assertThat(todo.isCompleted()).isTrue();
    }

    @Test
    @DisplayName("할일 수정 실패 - 없는 할일")
    void updateTodo_fail_notFound() {
        User user = createUser();

        given(userRepository.findByUsername("goorm")).willReturn(Optional.of(user));
        given(todoRepository.findByIdAndUser(999L, user)).willReturn(Optional.empty());

        TodoRequest request = new TodoRequest("수정", null, null, null, null, null, null, null);

        assertThatThrownBy(() -> todoService.updateTodo("goorm", 999L, request))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("할 일을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("할일 삭제 - 성공")
    void deleteTodo_success() {
        User user = createUser();
        Todo todo = createTodo(user);

        given(userRepository.findByUsername("goorm")).willReturn(Optional.of(user));
        given(todoRepository.findByIdAndUser(1L, user)).willReturn(Optional.of(todo));

        todoService.deleteTodo("goorm", 1L);

        verify(todoRepository).delete(todo);
    }

    @Test
    @DisplayName("할일 삭제 실패 - 없는 할일")
    void deleteTodo_fail_notFound() {
        User user = createUser();

        given(userRepository.findByUsername("goorm")).willReturn(Optional.of(user));
        given(todoRepository.findByIdAndUser(999L, user)).willReturn(Optional.empty());

        assertThatThrownBy(() -> todoService.deleteTodo("goorm", 999L))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("할 일을 찾을 수 없습니다.");
    }
}