package com.mission.store.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류가 발생하였습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    CONSTRAINT_VIOLATION(HttpStatus.CONFLICT, "제약 조건 위반"),

    ALREADY_EXISTS_EMAIL(HttpStatus.BAD_REQUEST, "이미 사용중인 이메일입니다."),
    ALREADY_EXISTS_PHONE(HttpStatus.BAD_REQUEST, "이미 사용중인 전화번호입니다."),
    ALREADY_EXISTS_NICKNAME(HttpStatus.BAD_REQUEST, "이미 사용중인 닉네임입니다."),
    ALREADY_VISITED_RESERVATION(HttpStatus.BAD_REQUEST, "이미 매장 방문이 확인된 예약입니다."),
    ALREADY_CANCELED_RESERVATION(HttpStatus.BAD_REQUEST, "이미 취소된 예약입니다."),
    ALREADY_PROCESSED_RESERVATION(HttpStatus.BAD_REQUEST, "이미 처리된 예약입니다."),
    ALREADY_WRITTEN_REVIEW(HttpStatus.BAD_REQUEST, "리뷰를 작성할 수 없습니다. 이미 리뷰가 작성된 예약입니다."),

    INVALID_MEMBER_ID(HttpStatus.BAD_REQUEST, "존재하지 않는 회원 아이디입니다."),
    INVALID_STORE_ID(HttpStatus.BAD_REQUEST, "존재하지 않는 매장 아이디입니다."),
    INVALID_RESERVATION_ID(HttpStatus.BAD_REQUEST, "존재하지 않는 예약 아이디입니다."),
    INVALID_EMAIL(HttpStatus.BAD_REQUEST, "존재하지 않는 이메일입니다."),
    INVALID_PHONE(HttpStatus.BAD_REQUEST, "존재하지 않는 전화번호입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    INVALID_REVIEW_TYPE(HttpStatus.BAD_REQUEST, "리뷰를 작성할 수 없습니다. 리뷰는 '공개'와 '점주 공개'만 가능합니다."),

    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 만료되었습니다."),
    NO_REFRESH_TOKEN_FOUND(HttpStatus.UNAUTHORIZED, "계정에 저장된 리프레시 토큰이 없습니다."),
    MISMATCHED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 일치하지 않습니다."),

    NO_SEARCH_RESULTS(HttpStatus.NOT_FOUND, "검색된 매장이 없습니다."),

    STORE_CLOSED(HttpStatus.BAD_REQUEST, "매장이 현재 영업 중이지 않습니다."),
    DUPLICATE_STORE(HttpStatus.BAD_REQUEST, "등록된 매장 중 중복된 매장이 존재합니다."),
    EXCEEDED_MAX_STORE_LIMIT(HttpStatus.BAD_REQUEST, "등록할 수 있는 매장 수를 초과했습니다. (최대 2개)"),

    DUPLICATE_RESERVATION(HttpStatus.BAD_REQUEST, "요청 시간에 중복된 예약이 존재합니다."),
    NOT_AVAILABLE_DURING_BREAK_TIME(HttpStatus.BAD_REQUEST, "휴무시간에는 예약이 불가능합니다."),

    NO_PARTNER_AUTHORITY(HttpStatus.FORBIDDEN, "점주 권한이 없습니다. 파트너 회원 가입이 필요합니다."),
    ACCESS_DENIED_FOR_CANCELLATION(HttpStatus.FORBIDDEN, "예약 취소 권한이 없습니다. 예약 취소는 본인만 가능합니다."),
    ACCESS_DENIED_FOR_REVIEW(HttpStatus.FORBIDDEN, "리뷰 작성 권한이 없습니다. 리뷰 작성은 본인만 가능합니다."),
    ACCESS_DENIED_APPROVE_OR_REJECT(HttpStatus.FORBIDDEN, "예약 승인 또는 거절 권한이 없습니다. 예약 승인 또는 거절은 해당 매장 점주만 가능합니다."),

    UNAPPROVED_RESERVATION(HttpStatus.BAD_REQUEST, "승인되지 않은 예약입니다."),
    DECLINED_RESERVATION(HttpStatus.BAD_REQUEST, "거절된 예약입니다."),
    UNABLE_TO_CONFIRM_RESERVATION(HttpStatus.BAD_REQUEST, "예약 확인이 불가능합니다. 예약 확인은 방문 1분 전까지만 가능합니다."),
    MISMATCHED_RESERVATION_CODE(HttpStatus.BAD_REQUEST, "예약 확인이 불가능합니다. 예약 코드가 일치하지 않습니다."),
    NO_RESERVATION_RELATED_TO_STORE(HttpStatus.NOT_FOUND, "매장과 관련된 예약이 존재하지 않습니다."),

    ;


    private final HttpStatus httpStatus;
    private final String description;
}