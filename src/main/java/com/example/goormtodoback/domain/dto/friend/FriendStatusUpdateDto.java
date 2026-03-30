package com.example.goormtodoback.domain.dto.friend;

import com.example.goormtodoback.domain.entity.Friend.FriendStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class FriendStatusUpdateDto {
    // 변경할 값
    // 상태만 허용
    @NotNull
    @JsonProperty("status")
    private FriendStatus status;
}
