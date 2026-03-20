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

    @JsonProperty("todos")
    private final List<TodoDto> todos;

    public FriendCalendarResponseDto(Long friendId, String friendNickname,
                                     List<Todo> todos) {
        this.friendId = friendId;
        this.friendNickname = friendNickname;
        this.todos = todos.stream()
                .map(TodoDto::from)
                .toList();
    }

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