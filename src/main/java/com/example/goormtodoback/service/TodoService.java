package com.example.goormtodoback.service;

import com.example.goormtodoback.common.exception.CustomException;
import com.example.goormtodoback.common.exception.ErrorCode;
import com.example.goormtodoback.domain.entity.Todo;
import com.example.goormtodoback.domain.entity.User;
import com.example.goormtodoback.domain.dto.todo.TodoRequest;
import com.example.goormtodoback.domain.dto.todo.TodoResponse;
import com.example.goormtodoback.repository.TodoRepository;
import com.example.goormtodoback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));
    }

    // 할일 목록 조회
    public List<TodoResponse> getTodos(String username, String dateType, Boolean isCompleted) {
        User user = getUser(username);

        List<Todo> todos;

        if (dateType != null) {
            todos = todoRepository.findByUserAndDateType(user, dateType);
        } else if (isCompleted != null) {
            todos = todoRepository.findByUserAndIsCompleted(user, isCompleted);
        } else {
            todos = todoRepository.findByUser(user);
        }

        return todos.stream()
                .map(TodoResponse::new)
                .collect(Collectors.toList());
    }

    // 할일 단건 조회
    public TodoResponse getTodo(String username, Long todoId) {
        User user = getUser(username);

        Todo todo = todoRepository.findByIdAndUser(todoId, user)
                .orElseThrow(() -> new CustomException(ErrorCode.TODO_NOT_FOUND));

        return new TodoResponse(todo);
    }

    // 할일 생성
    public TodoResponse createTodo(String username, TodoRequest request) {
        User user = getUser(username);

        String dateType = request.getDateType();
        if (dateType == null || (!dateType.equals("someday") && !dateType.equals("specific") && !dateType.equals("range"))) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        Todo todo = Todo.builder()
                .user(user)
                .title(request.getTitle())
                .dateType(dateType)
                .specificDate(request.getSpecificDate())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .category(request.getCategory())
                .memo(request.getMemo())
                .build();

        todoRepository.save(todo);

        return new TodoResponse(todo);
    }

    // 할일 수정
    @Transactional
    public TodoResponse updateTodo(String username, Long todoId, TodoRequest request) {
        User user = getUser(username);

        Todo todo = todoRepository.findByIdAndUser(todoId, user)
                .orElseThrow(() -> new CustomException(ErrorCode.TODO_NOT_FOUND));

        todo.update(
                request.getTitle(),
                request.getIsCompleted(),
                request.getDateType(),
                request.getSpecificDate(),
                request.getStartDate(),
                request.getEndDate(),
                request.getCategory(),
                request.getMemo()
        );

        return new TodoResponse(todo);
    }

    // 할일 삭제
    public void deleteTodo(String username, Long todoId) {
        User user = getUser(username);

        Todo todo = todoRepository.findByIdAndUser(todoId, user)
                .orElseThrow(() -> new CustomException(ErrorCode.TODO_NOT_FOUND));

        todoRepository.delete(todo);
    }
}