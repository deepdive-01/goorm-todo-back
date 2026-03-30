package com.example.goormtodoback.controller;

import com.example.goormtodoback.common.response.ApiResponse;
import com.example.goormtodoback.domain.dto.friend.FriendRequestDto;
import com.example.goormtodoback.domain.dto.friend.FriendStatusUpdateDto;
import com.example.goormtodoback.domain.dto.friend.*;
import com.example.goormtodoback.service.FriendService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


// 친구 관련 API
@RestController
@RequestMapping("/api/v1/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;


    // 친구 요청 전송
    // SecurityContext에 저장된 username을 꺼내서 사용
    @PostMapping("/request")
    public ResponseEntity<ApiResponse<FriendRequestResponseDto>> sendRequest(
            @AuthenticationPrincipal String username,
            @Valid @RequestBody FriendRequestDto dto
    ) {
        FriendRequestResponseDto response = friendService.sendRequest(username, dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)  // 201
                .body(ApiResponse.success("친구 요청을 전송했습니다.", response));
    }


    // 친구 요청 수락 및 거절
    @PatchMapping("/request/{friendId}")
    public ResponseEntity<ApiResponse<FriendStatusResponseDto>> updateStatus(
            @AuthenticationPrincipal String username,
            @PathVariable Long friendId,
            @Valid @RequestBody FriendStatusUpdateDto dto
    ) {
        FriendStatusResponseDto response = friendService.updateStatus(username, friendId, dto);
        return ResponseEntity
                .ok(ApiResponse.success("친구 요청 상태가 변경되었습니다.", response));
    }


    // 받은 친구 요청 목록 보기
    @GetMapping("/request/received")
    public ResponseEntity<ApiResponse<List<FriendReceivedResponseDto>>> getReceivedRequests(
            @AuthenticationPrincipal String username
    ) {
        List<FriendReceivedResponseDto> response = friendService.getReceivedRequests(username);
        return ResponseEntity
                .ok(ApiResponse.success("받은 친구 요청 목록을 조회했습니다.", response));
    }

    // 친구 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<FriendListResponseDto>>> getFriends(
            @AuthenticationPrincipal String username
    ) {
        List<FriendListResponseDto> response = friendService.getFriends(username);
        return ResponseEntity
                .ok(ApiResponse.success("친구 목록을 조회했습니다.", response));
    }


    // 친구 캘린더 조회
    @GetMapping("/{friendId}/calendar")
    public ResponseEntity<ApiResponse<FriendCalendarResponseDto>> getFriendCalendar(
            @AuthenticationPrincipal String username,
            @PathVariable Long friendId
    ) {
        FriendCalendarResponseDto response = friendService.getFriendCalendar(username, friendId);
        return ResponseEntity
                .ok(ApiResponse.success("친구의 캘린더를 조회했습니다.", response));
    }


    // 친구 삭제
    @DeleteMapping("/{friendId}")
    public ResponseEntity<ApiResponse<Void>> deleteFriend(
            @AuthenticationPrincipal String username,
            @PathVariable Long friendId
    ) {
        friendService.deleteFriend(username, friendId);
        return ResponseEntity
                .ok(ApiResponse.success("친구 관계가 해제되었습니다."));
    }
}