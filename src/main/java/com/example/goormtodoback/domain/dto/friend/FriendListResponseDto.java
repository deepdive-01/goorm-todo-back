package com.example.goormtodoback.domain.dto.friend;

import com.example.goormtodoback.domain.entity.Friend;
import com.example.goormtodoback.domain.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class FriendListResponseDto {

    @JsonProperty("friend_id")
    private final Long friendId;

    @JsonProperty("nickname")
    private final String nickname;

    private FriendListResponseDto(Long friendId, String nickname) {
        this.friendId = friendId;
        this.nickname = nickname;
    }


    public static FriendListResponseDto from(Friend friend, Long userId) {
        User friendUser = friend.getRequest().getId().equals(userId)
                ? friend.getReceive()   // 내가 요청자면 상대방은 수신자
                : friend.getRequest();  // 내가 수신자면 상대방은 요청자
        return new FriendListResponseDto(friendUser.getId(), friendUser.getNickname());
    }
}