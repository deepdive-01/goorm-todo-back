package com.example.goormtodoback.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "friend",
uniqueConstraints = {
@UniqueConstraint(columnNames = {"request_id", "receive_id"})
})
@Getter
@NoArgsConstructor
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User request;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receive_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User receive;

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
