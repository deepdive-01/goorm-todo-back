package com.example.goormtodoback.domain.dto.friend;

import com.example.goormtodoback.domain.entity.Todo;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class FriendCalendarResponseDto {

    @JsonProperty("friend_id")
    private final Long friendId;

    @JsonProperty("friend_nickname")
    private final String friendNickname;

    @JsonProperty("month")
    private final String month;

    @JsonProperty("todos")
    private final List<TodoDto> todos;

    public FriendCalendarResponseDto(Long friendId, String friendNickname,
                                     String month, List<Todo> todos) {
        this.friendId = friendId;
        this.friendNickname = friendNickname;
        this.month = month;
        this.todos = todos.stream()
                .map(TodoDto::from)
                .toList();
    }

    /**
     * 캘린더에서 보여줄 Todo 정보
     * 내부 정적 클래스로 정의해서 FriendCalendarResponseDto 안에서만 사용
     */
    @Getter
    public static class TodoDto {

        @JsonProperty("todo_id")
        private final Long todoId;

        @JsonProperty("title")
        private final String title;

        @JsonProperty("date")
        private final LocalDate date;

        @JsonProperty("is_completed")
        private final boolean isCompleted;

        private TodoDto(Todo todo) {
            this.todoId = todo.getId();
            this.title = todo.getTitle();
            // SPECIFIC이면 specificDate, RANGE면 startDate 표시
            this.date = todo.getDateType().equals("SPECIFIC")
                    ? todo.getSpecificDate()
                    : todo.getStartDate();
            this.isCompleted = todo.isCompleted();
        }

        public static TodoDto from(Todo todo) {
            return new TodoDto(todo);
        }
    }
}