package com.example.goormtodoback.service;

import com.example.goormtodoback.common.exception.CustomException;
import com.example.goormtodoback.common.exception.ErrorCode;
import com.example.goormtodoback.domain.dto.friend.*;
import com.example.goormtodoback.domain.entity.Friend;
import com.example.goormtodoback.domain.entity.Friend.FriendStatus;
import com.example.goormtodoback.domain.entity.Todo;
import com.example.goormtodoback.domain.entity.User;
import com.example.goormtodoback.domain.dto.friend.FriendRequestDto;
import com.example.goormtodoback.domain.dto.friend.FriendStatusUpdateDto;
import com.example.goormtodoback.repository.FriendRepository;
import com.example.goormtodoback.repository.TodoRepository;
import com.example.goormtodoback.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FriendServiceTest {

    @Mock
    private FriendRepository friendRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private FriendService friendService;

    private User loginUser;
    private User otherUser;
    private Friend friend;

    @BeforeEach
    void setUp() {
        loginUser = User.builder()
                .username("loginuser")
                .nickname("로그인유저")
                .email("login@test.com")
                .passwordHash("hash1")
                .build();
        setField(loginUser, "id", 1L);

        otherUser = User.builder()
                .username("otheruser")
                .nickname("다른유저")
                .email("other@test.com")
                .passwordHash("hash2")
                .build();
        setField(otherUser, "id", 2L);

        friend = Friend.builder()
                .request(loginUser)
                .receive(otherUser)
                .build();
        setField(friend, "id", 10L);
    }


    @Test
    @DisplayName("친구 요청 전송 - 성공")
    void sendRequest_success() {
        // given
        FriendRequestDto dto = new FriendRequestDto();
        setField(dto, "receiveId", 2L);

        given(userRepository.findByUsername("loginuser")).willReturn(Optional.of(loginUser));
        given(userRepository.findById(2L)).willReturn(Optional.of(otherUser));
        given(friendRepository.isFriend(1L, 2L)).willReturn(false);
        given(friendRepository.existsByRequestIdAndReceiveId(1L, 2L)).willReturn(false);
        given(friendRepository.save(any(Friend.class))).willReturn(friend);

        // when
        FriendRequestResponseDto result = friendService.sendRequest("loginuser", dto);

        // then
        assertThat(result.getStatus()).isEqualTo("PENDING");
        assertThat(result.getReceiveNickname()).isEqualTo("다른유저");
        verify(friendRepository).save(any(Friend.class));
    }

    @Test
    @DisplayName("친구 요청 전송 - 자기 자신에게 요청 시 SELF_REQUEST 예외")
    void sendRequest_selfRequest() {
        // given
        FriendRequestDto dto = new FriendRequestDto();
        setField(dto, "receiveId", 1L);  // loginUser와 동일한 id

        given(userRepository.findByUsername("loginuser")).willReturn(Optional.of(loginUser));

        // when & then
        assertThatThrownBy(() -> friendService.sendRequest("loginuser", dto))
                .isInstanceOf(CustomException.class)
                .satisfies(e -> assertThat(((CustomException) e).getErrorCode()).isEqualTo(ErrorCode.SELF_REQUEST));
    }

    @Test
    @DisplayName("친구 요청 전송 - 이미 친구 관계이면 ALREADY_FRIEND 예외")
    void sendRequest_alreadyFriend() {
        // given
        FriendRequestDto dto = new FriendRequestDto();
        setField(dto, "receiveId", 2L);

        given(userRepository.findByUsername("loginuser")).willReturn(Optional.of(loginUser));
        given(userRepository.findById(2L)).willReturn(Optional.of(otherUser));
        given(friendRepository.isFriend(1L, 2L)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> friendService.sendRequest("loginuser", dto))
                .isInstanceOf(CustomException.class)
                .satisfies(e -> assertThat(((CustomException) e).getErrorCode()).isEqualTo(ErrorCode.ALREADY_FRIEND));
    }

    @Test
    @DisplayName("친구 요청 전송 - 중복 요청 시 ALREADY_REQUESTED 예외")
    void sendRequest_alreadyRequested() {
        // given
        FriendRequestDto dto = new FriendRequestDto();
        setField(dto, "receiveId", 2L);

        given(userRepository.findByUsername("loginuser")).willReturn(Optional.of(loginUser));
        given(userRepository.findById(2L)).willReturn(Optional.of(otherUser));
        given(friendRepository.isFriend(1L, 2L)).willReturn(false);
        given(friendRepository.existsByRequestIdAndReceiveId(1L, 2L)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> friendService.sendRequest("loginuser", dto))
                .isInstanceOf(CustomException.class)
                .satisfies(e -> assertThat(((CustomException) e).getErrorCode()).isEqualTo(ErrorCode.ALREADY_REQUESTED));
    }


    @Test
    @DisplayName("친구 요청 수락 - 성공")
    void updateStatus_accept() {
        // given
        FriendStatusUpdateDto dto = new FriendStatusUpdateDto();
        setField(dto, "status", FriendStatus.ACCEPTED);

        given(friendRepository.findById(10L)).willReturn(Optional.of(friend));
        given(userRepository.findByUsername("otheruser")).willReturn(Optional.of(otherUser));

        // when
        FriendStatusResponseDto result = friendService.updateStatus("otheruser", 10L, dto);

        // then
        assertThat(result.getStatus()).isEqualTo("ACCEPTED");
    }

    @Test
    @DisplayName("친구 요청 거절 - 성공")
    void updateStatus_reject() {
        // given
        FriendStatusUpdateDto dto = new FriendStatusUpdateDto();
        setField(dto, "status", FriendStatus.REJECTED);

        given(friendRepository.findById(10L)).willReturn(Optional.of(friend));
        given(userRepository.findByUsername("otheruser")).willReturn(Optional.of(otherUser));

        // when
        FriendStatusResponseDto result = friendService.updateStatus("otheruser", 10L, dto);

        // then
        assertThat(result.getStatus()).isEqualTo("REJECTED");
    }

    @Test
    @DisplayName("친구 요청 수락/거절 - 수신자가 아닌 경우 FORBIDDEN 예외")
    void updateStatus_forbidden() {
        // given
        FriendStatusUpdateDto dto = new FriendStatusUpdateDto();
        setField(dto, "status", FriendStatus.ACCEPTED);

        // friend의 receive는 otherUser(id=2)인데, loginUser(id=1)가 처리 시도
        given(friendRepository.findById(10L)).willReturn(Optional.of(friend));
        given(userRepository.findByUsername("loginuser")).willReturn(Optional.of(loginUser));

        // when & then
        assertThatThrownBy(() -> friendService.updateStatus("loginuser", 10L, dto))
                .isInstanceOf(CustomException.class)
                .satisfies(e -> assertThat(((CustomException) e).getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN));
    }


    @Test
    @DisplayName("받은 친구 요청 목록 조회 - 성공")
    void getReceivedRequests_success() {
        // given
        given(userRepository.findByUsername("otheruser")).willReturn(Optional.of(otherUser));
        given(friendRepository.findByReceiveIdAndStatus(2L, FriendStatus.PENDING))
                .willReturn(List.of(friend));

        // when
        List<FriendReceivedResponseDto> result = friendService.getReceivedRequests("otheruser");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRequestNickname()).isEqualTo("로그인유저");
    }


    @Test
    @DisplayName("친구 목록 조회 - 성공")
    void getFriends_success() {
        // given
        friend.accept();  // ACCEPTED 상태로 변경

        given(userRepository.findByUsername("loginuser")).willReturn(Optional.of(loginUser));
        given(friendRepository.findFriends(1L)).willReturn(List.of(friend));

        // when
        List<FriendListResponseDto> result = friendService.getFriends("loginuser");

        // then
        assertThat(result).hasSize(1);
        // loginUser가 요청자이므로 상대방은 otherUser
        assertThat(result.get(0).getFriendId()).isEqualTo(2L);
        assertThat(result.get(0).getNickname()).isEqualTo("다른유저");
    }


    @Test
    @DisplayName("친구 캘린더 조회 - 성공")
    void getFriendCalendar_success() {
        // given
        Todo todo = Todo.builder()
                .user(otherUser)
                .title("운동하기")
                .dateType("SPECIFIC")
                .specificDate(LocalDate.of(2026, 3, 29))
                .build();

        given(userRepository.findByUsername("loginuser")).willReturn(Optional.of(loginUser));
        given(userRepository.findById(2L)).willReturn(Optional.of(otherUser));
        given(friendRepository.isFriend(1L, 2L)).willReturn(true);
        given(todoRepository.findByUser(otherUser)).willReturn(List.of(todo));

        // when
        FriendCalendarResponseDto result = friendService.getFriendCalendar("loginuser", 2L);

        // then
        assertThat(result.getFriendNickname()).isEqualTo("다른유저");
        assertThat(result.getTodos()).hasSize(1);
    }

    @Test
    @DisplayName("친구 캘린더 조회 - 친구 유저가 없으면 USER_NOT_FOUND 예외")
    void getFriendCalendar_userNotFound() {
        // given
        given(userRepository.findByUsername("loginuser")).willReturn(Optional.of(loginUser));
        given(userRepository.findById(99L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> friendService.getFriendCalendar("loginuser", 99L))
                .isInstanceOf(CustomException.class)
                .satisfies(e -> assertThat(((CustomException) e).getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND));
    }

    @Test
    @DisplayName("친구 캘린더 조회 - 친구 관계가 아니면 NOT_FRIEND 예외")
    void getFriendCalendar_notFriend() {
        // given
        given(userRepository.findByUsername("loginuser")).willReturn(Optional.of(loginUser));
        given(userRepository.findById(2L)).willReturn(Optional.of(otherUser));
        given(friendRepository.isFriend(1L, 2L)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> friendService.getFriendCalendar("loginuser", 2L))
                .isInstanceOf(CustomException.class)
                .satisfies(e -> assertThat(((CustomException) e).getErrorCode()).isEqualTo(ErrorCode.NOT_FRIEND));
    }


    @Test
    @DisplayName("친구 삭제 - 성공")
    void deleteFriend_success() {
        // given
        given(userRepository.findByUsername("loginuser")).willReturn(Optional.of(loginUser));
        given(friendRepository.findFriendRelation(1L, 2L)).willReturn(Optional.of(friend));

        // when
        friendService.deleteFriend("loginuser", 2L);

        // then
        verify(friendRepository).delete(friend);
    }

    @Test
    @DisplayName("친구 삭제 - 친구 관계가 없으면 FRIEND_NOT_FOUND 예외")
    void deleteFriend_notFound() {
        // given
        given(userRepository.findByUsername("loginuser")).willReturn(Optional.of(loginUser));
        given(friendRepository.findFriendRelation(1L, 2L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> friendService.deleteFriend("loginuser", 2L))
                .isInstanceOf(CustomException.class)
                .satisfies(e -> assertThat(((CustomException) e).getErrorCode()).isEqualTo(ErrorCode.FRIEND_NOT_FOUND));
    }


    private void setField(Object obj, String fieldName, Object value) {
        try {
            var field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
