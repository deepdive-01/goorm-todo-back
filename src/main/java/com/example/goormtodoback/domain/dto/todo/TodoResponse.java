package com.example.goormtodoback.domain.dto.todo;

import com.example.goormtodoback.domain.entity.Todo;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TodoResponse {
    private Long id;
    private String title;
    private boolean isCompleted;
    private String dateType;
    private LocalDate specificDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private String category;
    private String memo;
    private LocalDateTime createdAt;

    public TodoResponse(Todo todo) {
        this.id = todo.getId();
        this.title = todo.getTitle();
        this.isCompleted = todo.isCompleted();
        this.dateType = todo.getDateType();
        this.specificDate = todo.getSpecificDate();
        this.startDate = todo.getStartDate();
        this.endDate = todo.getEndDate();
        this.category = todo.getCategory();
        this.memo = todo.getMemo();
        this.createdAt = todo.getCreatedAt();
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    @JsonProperty("isCompleted")
    public boolean getIsCompleted() { return isCompleted; }
    public String getDateType() { return dateType; }
    public LocalDate getSpecificDate() { return specificDate; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public String getCategory() { return category; }
    public String getMemo() { return memo; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}