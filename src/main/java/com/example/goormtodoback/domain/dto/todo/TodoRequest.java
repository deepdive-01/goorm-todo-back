package com.example.goormtodoback.domain.dto.todo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TodoRequest {
    private String title;
    private Boolean isCompleted;
    private String dateType;
    private LocalDate specificDate;
    private LocalDate startDate;
    private LocalDate endDate;
}