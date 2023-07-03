package com.mission.store.exception;

import com.mission.store.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.mission.store.type.ErrorCode.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MemberException.class)
    public ResponseEntity<?> handleMemberException(MemberException e) {
        log.error("{} is occurred. {}", e.getErrorCode(), e.getErrorMessage());

        ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
        HttpStatus httpStatus = e.getHttpStatus();

        return ResponseEntity.status(httpStatus).body(errorResponse);
    }

    @ExceptionHandler(StoreException.class)
    public ResponseEntity<?> handleStoreException(StoreException e) {
        log.error("{} is occurred. {}", e.getErrorCode(), e.getErrorMessage());

        ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
        HttpStatus httpStatus = e.getHttpStatus();

        return ResponseEntity.status(httpStatus).body(errorResponse);
    }

    @ExceptionHandler(ReservationException.class)
    public ResponseEntity<?> handleReservationException(ReservationException e) {
        log.error("{} is occurred. {}", e.getErrorCode(), e.getErrorMessage());

        ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
        HttpStatus httpStatus = e.getHttpStatus();

        return ResponseEntity.status(httpStatus).body(errorResponse);
    }

    @ExceptionHandler(ReviewException.class)
    public ResponseEntity<?> handleReviewException(ReviewException e) {
        log.error("{} is occurred. {}", e.getErrorCode(), e.getErrorMessage());

        ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
        HttpStatus httpStatus = e.getHttpStatus();

        return ResponseEntity.status(httpStatus).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        log.error("MethodArgumentNotValidException is occurred.", e);

        ErrorResponse errorResponse = new ErrorResponse(
                INVALID_REQUEST
                , INVALID_REQUEST.getDescription());
        HttpStatus httpStatus = INVALID_REQUEST.getHttpStatus();

        return ResponseEntity.status(httpStatus).body(errorResponse);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolationException(DataIntegrityViolationException e){
        log.error("DataIntegrityViolationException is occurred.", e);

        ErrorResponse errorResponse = new ErrorResponse(
                CONSTRAINT_VIOLATION
                , CONSTRAINT_VIOLATION.getDescription());
        HttpStatus httpStatus = CONSTRAINT_VIOLATION.getHttpStatus();

        return ResponseEntity.status(httpStatus).body(errorResponse);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e){
        log.error("Exception is occurred.", e);

        ErrorResponse errorResponse = new ErrorResponse(
                INTERNAL_SERVER_ERROR
                , INTERNAL_SERVER_ERROR.getDescription());
        HttpStatus httpStatus = INTERNAL_SERVER_ERROR.getHttpStatus();

        return ResponseEntity.status(httpStatus).body(errorResponse);
    }
}
