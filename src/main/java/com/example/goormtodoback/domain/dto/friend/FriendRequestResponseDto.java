package com.example.goormtodoback.domain.dto.friend;

import com.example.goormtodoback.domain.entity.Friend;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class FriendRequestResponseDto {
    // 엔티티를 DTO로 변환

    @JsonProperty("friend_id")
    private Long friendId;

    @JsonProperty("receive_id")
    private Long receiveId;

    @JsonProperty("receive_nickname")
    private String receiveNickname;

    @JsonProperty("status")
    private String status;

    @JsonProperty("create_at")
    private LocalDateTime createAt;

    private FriendRequestResponseDto(Friend friend) {
        this.friendId = friend.getId();
        this.receiveId = friend.getReceive().getId();
        this.receiveNickname = friend.getReceive().getNickname();
        this.status = friend.getStatus().name();
        this.createAt = friend.getCreateAt();
    }

    public static FriendRequestResponseDto from(Friend friend) {
        return new FriendRequestResponseDto(friend);
    }
}
