package com.example.goormtodoback.repository;

import com.example.goormtodoback.domain.entity.Friend;
import com.example.goormtodoback.domain.entity.Friend.FriendStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    // 받은 친구 요청 목록 조회
    // receiveId의 유저가 받은 PENDING 상태의 요청 목록 반환
    List<Friend> findByReceiveIdAndStatus(Long receiveId, FriendStatus status);

    // 친구 목록을 조회 (ACCEPTED 상태인 친구 관계를 찾음)
    @Query("""
        SELECT f FROM Friend f
        WHERE (f.request.id = :userId OR f.receive.id = :userId)
        AND f.status = 'ACCEPTED'
    """)
    List<Friend> findFriends(@Param("userId") Long userId);

    // 친구 관계를 확인
    // 친구 캘린더를 조회하기 전에 실제 친구 관계인지 검증
    // 양방향 검증을 통해 친구 관계 여부를 확인함
    @Query("""
        SELECT COUNT(f) > 0 FROM Friend f
        WHERE ((f.request.id = :userId AND f.receive.id = :friendId)
        OR (f.request.id = :friendId AND f.receive.id = :userId))
        AND f.status = 'ACCEPTED'
    """)
    boolean isFriend(@Param("userId") Long userId, @Param("friendId") Long friendId);

    // 중복 친구 요청을 확인
    boolean existsByRequestIdAndReceiveId(Long requestId, Long receiveId);

    // 친구 관계를 단건으로 조회할 수 있음
    // 친구 삭제를 하기 위함
    @Query("""
    SELECT f FROM Friend f
    WHERE ((f.request.id = :userId AND f.receive.id = :friendId)
    OR (f.request.id = :friendId AND f.receive.id = :userId))
    AND f.status = 'ACCEPTED'
    """)
    Optional<Friend> findFriendRelation(@Param("userId") Long userId, @Param("friendId") Long friendId);
}
