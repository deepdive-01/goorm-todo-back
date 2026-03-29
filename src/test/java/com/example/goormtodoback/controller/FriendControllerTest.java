package com.example.goormtodoback.controller;

import com.example.goormtodoback.common.exception.CustomException;
import com.example.goormtodoback.common.exception.ErrorCode;
import com.example.goormtodoback.domain.dto.friend.*;
import com.example.goormtodoback.service.FriendService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FriendControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FriendService friendService;

    private Authentication auth;

    @BeforeEach
    void setUp() {
        auth = new UsernamePasswordAuthenticationToken("loginuser", null, List.of());
    }

    @Test
    @DisplayName("친구 요청 전송 - 성공 (201)")
    void sendRequest_success() throws Exception {
        FriendRequestResponseDto response = mockFriendRequestResponseDto(10L, 2L, "다른유저", "PENDING");
        given(friendService.sendRequest(eq("loginuser"), any())).willReturn(response);

        mockMvc.perform(post("/api/v1/friends/request")
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "receive_id": 2 }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.receive_nickname").value("다른유저"))
                .andExpect(jsonPath("$.data.status").value("PENDING"));
    }

    @Test
    @DisplayName("친구 요청 전송 - receive_id 없으면 400")
    void sendRequest_missingReceiveId() throws Exception {
        mockMvc.perform(post("/api/v1/friends/request")
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("친구 요청 전송 - 자기 자신에게 요청 시 400")
    void sendRequest_selfRequest() throws Exception {
        given(friendService.sendRequest(eq("loginuser"), any()))
                .willThrow(new CustomException(ErrorCode.SELF_REQUEST));

        mockMvc.perform(post("/api/v1/friends/request")
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "receive_id": 1 }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("SELF_REQUEST"));
    }

    @Test
    @DisplayName("친구 요청 전송 - 인증 없으면 403")
    void sendRequest_unauthorized() throws Exception {
        mockMvc.perform(post("/api/v1/friends/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "receive_id": 2 }
                                """))
                .andExpect(status().isForbidden());
    }


    @Test
    @DisplayName("친구 요청 수락 - 성공 (200)")
    void updateStatus_accept() throws Exception {
        FriendStatusResponseDto response = mockFriendStatusResponseDto(10L, 1L, 2L, "로그인유저", "다른유저", "ACCEPTED");
        given(friendService.updateStatus(eq("loginuser"), eq(10L), any())).willReturn(response);

        mockMvc.perform(patch("/api/v1/friends/request/10")
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "status": "ACCEPTED" }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.status").value("ACCEPTED"));
    }

    @Test
    @DisplayName("친구 요청 수락/거절 - 수신자가 아닌 경우 403")
    void updateStatus_forbidden() throws Exception {
        given(friendService.updateStatus(eq("loginuser"), eq(10L), any()))
                .willThrow(new CustomException(ErrorCode.FORBIDDEN));

        mockMvc.perform(patch("/api/v1/friends/request/10")
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "status": "ACCEPTED" }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));
    }


    @Test
    @DisplayName("받은 친구 요청 목록 조회 - 성공 (200)")
    void getReceivedRequests_success() throws Exception {
        FriendReceivedResponseDto item = mockFriendReceivedResponseDto(10L, 1L, "로그인유저", "PENDING");
        given(friendService.getReceivedRequests("loginuser")).willReturn(List.of(item));

        mockMvc.perform(get("/api/v1/friends/request/received")
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data[0].request_nickname").value("로그인유저"))
                .andExpect(jsonPath("$.data[0].status").value("PENDING"));
    }


    @Test
    @DisplayName("친구 목록 조회 - 성공 (200)")
    void getFriends_success() throws Exception {
        FriendListResponseDto item = mockFriendListResponseDto(2L, "다른유저");
        given(friendService.getFriends("loginuser")).willReturn(List.of(item));

        mockMvc.perform(get("/api/v1/friends")
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data[0].friend_id").value(2))
                .andExpect(jsonPath("$.data[0].nickname").value("다른유저"));
    }


    @Test
    @DisplayName("친구 캘린더 조회 - 성공 (200)")
    void getFriendCalendar_success() throws Exception {
        FriendCalendarResponseDto response = new FriendCalendarResponseDto(2L, "다른유저", List.of());
        given(friendService.getFriendCalendar("loginuser", 2L)).willReturn(response);

        mockMvc.perform(get("/api/v1/friends/2/calendar")
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.friend_nickname").value("다른유저"));
    }

    @Test
    @DisplayName("친구 캘린더 조회 - 친구 관계가 아니면 403")
    void getFriendCalendar_notFriend() throws Exception {
        given(friendService.getFriendCalendar("loginuser", 2L))
                .willThrow(new CustomException(ErrorCode.NOT_FRIEND));

        mockMvc.perform(get("/api/v1/friends/2/calendar")
                        .with(authentication(auth)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("NOT_FRIEND"));
    }


    @Test
    @DisplayName("친구 삭제 - 성공 (200)")
    void deleteFriend_success() throws Exception {
        doNothing().when(friendService).deleteFriend("loginuser", 2L);

        mockMvc.perform(delete("/api/v1/friends/2")
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"));
    }

    @Test
    @DisplayName("친구 삭제 - 친구 관계 없으면 404")
    void deleteFriend_notFound() throws Exception {
        doThrow(new CustomException(ErrorCode.FRIEND_NOT_FOUND))
                .when(friendService).deleteFriend("loginuser", 2L);

        mockMvc.perform(delete("/api/v1/friends/2")
                        .with(authentication(auth)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("FRIEND_NOT_FOUND"));
    }


    private FriendRequestResponseDto mockFriendRequestResponseDto(
            Long friendId, Long receiveId, String receiveNickname, String status) {
        try {
            var dto = allocate(FriendRequestResponseDto.class);
            setField(dto, "friendId", friendId);
            setField(dto, "receiveId", receiveId);
            setField(dto, "receiveNickname", receiveNickname);
            setField(dto, "status", status);
            return dto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private FriendStatusResponseDto mockFriendStatusResponseDto(
            Long friendId, Long requestId, Long receiveId,
            String requestNickname, String receiveNickname, String status) {
        try {
            var dto = allocate(FriendStatusResponseDto.class);
            setField(dto, "friendId", friendId);
            setField(dto, "requestId", requestId);
            setField(dto, "receiveId", receiveId);
            setField(dto, "requestNickname", requestNickname);
            setField(dto, "receiveNickname", receiveNickname);
            setField(dto, "status", status);
            return dto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private FriendReceivedResponseDto mockFriendReceivedResponseDto(
            Long friendId, Long requestId, String requestNickname, String status) {
        try {
            var dto = allocate(FriendReceivedResponseDto.class);
            setField(dto, "friendId", friendId);
            setField(dto, "requestId", requestId);
            setField(dto, "requestNickname", requestNickname);
            setField(dto, "status", status);
            return dto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private FriendListResponseDto mockFriendListResponseDto(Long friendId, String nickname) {
        try {
            var dto = allocate(FriendListResponseDto.class);
            setField(dto, "friendId", friendId);
            setField(dto, "nickname", nickname);
            return dto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T allocate(Class<T> clazz) throws Exception {
        var unsafe = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
        unsafe.setAccessible(true);
        var u = (sun.misc.Unsafe) unsafe.get(null);
        return (T) u.allocateInstance(clazz);
    }

    private void setField(Object obj, String fieldName, Object value) {
        try {
            Class<?> clazz = obj.getClass();
            while (clazz != null) {
                try {
                    var field = clazz.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    field.set(obj, value);
                    return;
                } catch (NoSuchFieldException e) {
                    clazz = clazz.getSuperclass();
                }
            }
            throw new NoSuchFieldException(fieldName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
