package com.example.goormtodoback.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "quotes")
@Getter
@NoArgsConstructor
public class Quote {
    // 명언 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 명언 내용
    @Column(nullable = false)
    private String content;

    // 출처
    @Column
    private String author;

    @Builder
    public Quote(String content, String author) {
        this.content = content;
        this.author = author;
    }
}
