package com.example.goormtodoback.service;

import com.example.goormtodoback.common.exception.CustomException;
import com.example.goormtodoback.common.exception.ErrorCode;
import com.example.goormtodoback.domain.entity.Friend;
import com.example.goormtodoback.domain.entity.Friend.FriendStatus;
import com.example.goormtodoback.domain.entity.Todo;
import com.example.goormtodoback.domain.entity.User;
import com.example.goormtodoback.domain.dto.friend.FriendRequestDto;
import com.example.goormtodoback.domain.dto.friend.FriendStatusUpdateDto;
import com.example.goormtodoback.domain.dto.friend.*;
import com.example.goormtodoback.repository.FriendRepository;
import com.example.goormtodoback.repository.TodoRepository;
import com.example.goormtodoback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final TodoRepository todoRepository;

    // username을 꺼내서, User Entity로 조회
    private User getLoginUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }


    // 친구 요청 전송
    @Transactional
    public FriendRequestResponseDto sendRequest(String username, FriendRequestDto dto) {

        // 현재 로그인한 유저 조회
        User loginUser = getLoginUser(username);

        // 자기 자신에게 요청하는지 확인
        if (loginUser.getId().equals(dto.getReceiveId())) {
            throw new CustomException(ErrorCode.SELF_REQUEST);
        }

        // 받는 유저가 존재하는지 확인
        User receiveUser = userRepository.findById(dto.getReceiveId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 이미 친구 관계인지 확인
        if (friendRepository.isFriend(loginUser.getId(), receiveUser.getId())) {
            throw new CustomException(ErrorCode.ALREADY_FRIEND);
        }

        // 이미 요청을 보냈는지 확인 (중복 요청 방지)
        if (friendRepository.existsByRequestIdAndReceiveId(loginUser.getId(), receiveUser.getId())) {
            throw new CustomException(ErrorCode.ALREADY_REQUESTED);
        }

        // Friend Entity 생성 후 저장
        Friend friend = Friend.builder()
                .request(loginUser)
                .receive(receiveUser)
                .build();

        return FriendRequestResponseDto.from(friendRepository.save(friend));
    }


    // 친구 요청 수락 및 거절
    @Transactional
    public FriendStatusResponseDto updateStatus(String username, Long friendId, FriendStatusUpdateDto dto) {

        // 친구 요청 조회
        Friend friend = friendRepository.findById(friendId)
                .orElseThrow(() -> new CustomException(ErrorCode.FRIEND_REQUEST_NOT_FOUND));

        // 현재 로그인한 유저 조회
        User loginUser = getLoginUser(username);

        // 수신자 본인인지 확인
        if (!friend.getReceive().getId().equals(loginUser.getId())) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        // 상태값에 따라 처리
        if (dto.getStatus() == FriendStatus.ACCEPTED) {
            friend.accept();
        } else if (dto.getStatus() == FriendStatus.REJECTED) {
            friend.reject();
        } else {
            // PENDING 등 허용되지 않는 상태값
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        return FriendStatusResponseDto.from(friend);
    }


    // 받은 친구 요청 목록 조회
    @Transactional(readOnly = true)
    public List<FriendReceivedResponseDto> getReceivedRequests(String username) {

        User loginUser = getLoginUser(username);

        return friendRepository
                .findByReceiveIdAndStatus(loginUser.getId(), FriendStatus.PENDING)
                .stream()
                .map(FriendReceivedResponseDto::from)
                .toList();
    }


    // 친구 목록 조회
    @Transactional(readOnly = true)
    public List<FriendListResponseDto> getFriends(String username) {

        User loginUser = getLoginUser(username);

        return friendRepository
                .findFriends(loginUser.getId())
                .stream()
                .map(friend -> FriendListResponseDto.from(friend, loginUser.getId()))
                .toList();
    }


    // 친구 캘린더 조회
    @Transactional(readOnly = true)
    public FriendCalendarResponseDto getFriendCalendar(String username, Long friendId) {

        // 현재 로그인한 유저 조회
        User loginUser = getLoginUser(username);

        // 친구 유저 존재 확인
        User friendUser = userRepository.findById(friendId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 친구 관계 확인
        if (!friendRepository.isFriend(loginUser.getId(), friendId)) {
            throw new CustomException(ErrorCode.NOT_FRIEND);
        }

        // 친구의 전체 할 일 조회
        List<Todo> todos = todoRepository.findByUser(friendUser);

        return new FriendCalendarResponseDto(
                friendUser.getId(),
                friendUser.getNickname(),
                todos
        );
    }

    // 친구 삭제
    @Transactional
    public void deleteFriend(String username, Long friendId) {

        // 현재 로그인한 유저 조회
        User loginUser = getLoginUser(username);

        // 친구 관계 조회
        Friend friend = friendRepository
                .findFriendRelation(loginUser.getId(), friendId)
                .orElseThrow(() -> new CustomException(ErrorCode.FRIEND_NOT_FOUND));

        // 친구 관계 삭제
        friendRepository.delete(friend);
    }
}