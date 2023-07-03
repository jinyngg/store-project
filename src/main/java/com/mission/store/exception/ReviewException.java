package com.mission.store.exception;

import com.mission.store.type.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewException extends RuntimeException {

    private ErrorCode errorCode;
    private String errorMessage;
    private HttpStatus httpStatus;

    public ReviewException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
        this.httpStatus = errorCode.getHttpStatus();
    }

}
