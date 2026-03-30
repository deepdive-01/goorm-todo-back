package com.example.goormtodoback.controller;

import com.example.goormtodoback.common.response.ApiResponse;
import com.example.goormtodoback.domain.dto.todo.TodoRequest;
import com.example.goormtodoback.domain.dto.todo.TodoResponse;
import com.example.goormtodoback.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/todos")
@RequiredArgsConstructor
public class TodoController {
    private final TodoService todoService;

    // 할일 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<TodoResponse>>> getTodos(
            @AuthenticationPrincipal String username,
            @RequestParam(required = false) String date_type,
            @RequestParam(required = false) Boolean is_completed) {
        List<TodoResponse> data = todoService.getTodos(username, date_type, is_completed);
        return ResponseEntity.ok(ApiResponse.success("할일 목록을 조회했습니다.", data));
    }

    // 할일 단건 조회
    @GetMapping("/{todoId}")
    public ResponseEntity<ApiResponse<TodoResponse>> getTodo(
            @AuthenticationPrincipal String username,
            @PathVariable Long todoId){
        TodoResponse data = todoService.getTodo(username, todoId);
        return ResponseEntity.ok(ApiResponse.success("할일을 조회했습니다.", data));
    }

    // 할일 생성
    @PostMapping
    public ResponseEntity<ApiResponse<TodoResponse>> createTodo(
            @AuthenticationPrincipal String username,
            @RequestBody TodoRequest request) {
        TodoResponse data = todoService.createTodo(username, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("할일이 생성되었습니다.", data));
    }

    // 할일 수정
    @PatchMapping("/{todoId}")
    public ResponseEntity<ApiResponse<TodoResponse>> updateTodo(
            @AuthenticationPrincipal String username,
            @PathVariable Long todoId,
            @RequestBody TodoRequest request) {
        TodoResponse data = todoService.updateTodo(username, todoId, request);
        return ResponseEntity.ok(ApiResponse.success("할일을 수정했습니다.", data));
    }

    // 할일 삭제
    @DeleteMapping("{todoId}")
    public ResponseEntity<ApiResponse<TodoResponse>> deleteTodo(
            @AuthenticationPrincipal String username,
            @PathVariable Long todoId) {
        todoService.deleteTodo(username, todoId);
        return ResponseEntity.ok(ApiResponse.success("할일을 삭제했습니다.", null));
    }
}