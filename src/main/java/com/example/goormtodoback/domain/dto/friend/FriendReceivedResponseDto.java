package com.example.goormtodoback.domain.dto.friend;

import com.example.goormtodoback.domain.entity.Friend;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class FriendReceivedResponseDto {

    @JsonProperty("friend_id")
    private final Long friendId;

    @JsonProperty("request_id")
    private final Long requestId;

    @JsonProperty("request_nickname")
    private final String requestNickname;

    @JsonProperty("status")
    private final String status;

    @JsonProperty("create_at")
    private final LocalDateTime createAt;

    private FriendReceivedResponseDto(Friend friend) {
        this.friendId = friend.getId();
        this.requestId = friend.getRequest().getId();
        this.requestNickname = friend.getRequest().getNickname();
        this.status = friend.getStatus().name();
        this.createAt = friend.getCreateAt();
    }

    public static FriendReceivedResponseDto from(Friend friend) {
        return new FriendReceivedResponseDto(friend);
    }
}