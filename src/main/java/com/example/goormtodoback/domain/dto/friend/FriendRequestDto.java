package com.example.goormtodoback.domain.dto.friend;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class FriendRequestDto {
    // 친구 요청을 받을 유저 ID
    @NotNull(message = "받는 사람 ID는 필수입니다.") // 유효성 검사
    @JsonProperty("receive_id") // receiveId 필드로 매핑
    private Long receiveId;

}
