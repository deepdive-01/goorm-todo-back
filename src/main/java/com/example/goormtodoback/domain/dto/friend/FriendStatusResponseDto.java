package com.example.goormtodoback.domain.dto.friend;

import com.example.goormtodoback.domain.entity.Friend;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class FriendStatusResponseDto {

    @JsonProperty("friend_id")
    private final Long friendId;

    @JsonProperty("request_id")
    private final Long requestId;

    @JsonProperty("receive_id")
    private final Long receiveId;

    @JsonProperty("request_nickname")
    private final String requestNickname;

    @JsonProperty("receive_nickname")
    private final String receiveNickname;

    @JsonProperty("status")
    private final String status;

    @JsonProperty("create_at")
    private final LocalDateTime createAt;

    private FriendStatusResponseDto(Friend friend) {
        this.friendId = friend.getId();
        this.requestId = friend.getRequest().getId();
        this.receiveId = friend.getReceive().getId();
        this.requestNickname = friend.getRequest().getNickname();
        this.receiveNickname = friend.getReceive().getNickname();
        this.status = friend.getStatus().name();
        this.createAt = friend.getCreateAt();
    }

    public static FriendStatusResponseDto from(Friend friend) {
        return new FriendStatusResponseDto(friend);
    }
}
