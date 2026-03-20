package com.example.goormtodoback.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

// 친구 관계를 나타내는 Entity


@Entity // JPA 엔티티
@Table(name = "friend", // 테이블 이름
uniqueConstraints = {
@UniqueConstraint(columnNames = {"request_id", "receive_id"}) // 유니크 제약
})
@Getter
@NoArgsConstructor
public class Friend {

    @Id // PK 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false) // request_id라는 컬럼으로 저장
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User request;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receive_id", nullable = false) // receive_id라는 컬럼으로 저장
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User receive;

    // 친구 요청 상태
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private FriendStatus status = FriendStatus.PENDING;

    @Column(name = "create_at", nullable = false, updatable = false)
    private LocalDateTime createAt;

    public enum FriendStatus {
        PENDING, ACCEPTED, REJECTED
    }

    @Builder
    public Friend(User request, User receive) {
        this.request = request;
        this.receive = receive;
        this.createAt = LocalDateTime.now();
    }

    public void accept() {
        this.status = FriendStatus.ACCEPTED;
    }

    public void reject() {
        this.status = FriendStatus.REJECTED;
    }
}
