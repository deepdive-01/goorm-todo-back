package com.example.goormtodoback.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "todos")
@Getter
@NoArgsConstructor
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(name = "is_completed", nullable = false)
    private boolean isCompleted = false;

    @Column(name = "date_type", nullable = false, length = 10)
    private String dateType;

    @Column(name = "specific_date")
    private LocalDate specificDate;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Todo(User user, String title, String dateType,
                LocalDate specificDate, LocalDate startDate, LocalDate endDate) {
        this.user = user;
        this.title = title;
        this.isCompleted = false;
        this.dateType = dateType;
        this.specificDate = specificDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = LocalDateTime.now();
    }

    public void update(String title, Boolean isCompleted, String dateType,
                       LocalDate specificDate, LocalDate startDate, LocalDate endDate) {
        if (title != null) this.title = title;
        if (isCompleted != null) this.isCompleted = isCompleted;
        if (dateType != null) {
            this.dateType = dateType;
            this.specificDate = specificDate;
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }
}