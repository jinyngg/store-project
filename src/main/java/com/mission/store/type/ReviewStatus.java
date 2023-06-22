package com.mission.store.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReviewStatus {

    PUBLIC("공개"),
    PRIVATE("비공개"),
    OWNER_PUBLIC("점주 공개"),
    DELETED("삭제"),
    REVIEW_SUSPENDED("리뷰 검토")

    ;

    private final String description;
}
