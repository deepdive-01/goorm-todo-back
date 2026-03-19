package com.example.goormtodoback.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 공통
    INVALID_INPUT(400, "INVALID_INPUT", "잘못된 입력값입니다."),
    UNAUTHORIZED(401, "UNAUTHORIZED", "로그인이 필요합니다."),
    FORBIDDEN(403, "FORBIDDEN", "접근 권한이 없습니다."),
    SERVER_ERROR(500, "SERVER_ERROR", "서버 오류가 발생했습니다."),

    // 유저
    USER_NOT_FOUND(404, "USER_NOT_FOUND", "존재하지 않는 사용자입니다."),
    EMAIL_DUPLICATED(400, "EMAIL_DUPLICATED", "이미 사용 중인 이메일입니다."),
    USERNAME_DUPLICATED(400, "USERNAME_DUPLICATED", "이미 사용 중인 아이디입니다."),
    INVALID_PASSWORD(401, "INVALID_PASSWORD", "비밀번호가 일치하지 않습니다."),

    // 할 일
    TODO_NOT_FOUND(404, "TODO_NOT_FOUND", "할 일을 찾을 수 없습니다."),
    TODO_ACCESS_DENIED(403, "TODO_ACCESS_DENIED", "해당 할 일에 접근 권한이 없습니다."),

    // 친구
    FRIEND_REQUEST_NOT_FOUND(404, "REQUEST_NOT_FOUND", "친구 요청을 찾을 수 없습니다."),
    ALREADY_REQUESTED(400, "ALREADY_REQUESTED", "이미 친구 요청을 보냈습니다."),
    ALREADY_FRIEND(400, "ALREADY_FRIEND", "이미 친구 관계입니다."),
    SELF_REQUEST(400, "SELF_REQUEST", "자기 자신에게 친구 요청을 보낼 수 없습니다."),
    NOT_FRIEND(403, "NOT_FRIEND", "친구 관계가 아닙니다."),
    FRIEND_NOT_FOUND(404, "FRIEND_NOT_FOUND", "친구 관계를 찾을 수 없습니다.");

    private final int status;
    private final String code;
    private final String message;
}
