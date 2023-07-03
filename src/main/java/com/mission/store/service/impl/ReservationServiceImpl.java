package com.mission.store.service.impl;

import com.mission.store.domain.Member;
import com.mission.store.domain.Reservation;
import com.mission.store.domain.Store;
import com.mission.store.dto.ReservationDto;
import com.mission.store.dto.ReservationRegistration;
import com.mission.store.exception.MemberException;
import com.mission.store.exception.ReservationException;
import com.mission.store.exception.StoreException;
import com.mission.store.repository.MemberRepository;
import com.mission.store.repository.ReservationRepository;
import com.mission.store.repository.StoreRepository;
import com.mission.store.service.ReservationService;
import com.mission.store.type.ReservationApprovalStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import static com.mission.store.type.ErrorCode.*;
import static com.mission.store.type.ReservationApprovalStatus.PENDING;
import static com.mission.store.type.ReservationVisitStatus.*;
import static com.mission.store.type.StoreStatus.OPEN;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private static final int ARRIVAL_THRESHOLD_MINUTES = 10;

    private final ReservationRepository reservationRepository;
    private final StoreRepository storeRepository;
    private final MemberRepository memberRepository;

    /** 예약 요청 */
    @Override
    @Transactional
    public ReservationRegistration.Response reserve(ReservationRegistration.Request request) {
        Long storeId = request.getStoreId();
        Long memberId = request.getCustomerId();

        // 1. 매장 및 유저 확인
        Store store = getStoreById(storeId);
        Member customer = getCustomerById(memberId);

        // 2. 매장 영업 여부 확인
        validateStoreOpen(store);

        // 3. 휴무 시간 예약 여부 확인
        String storeBreakTime = store.getBreakTime();
        String reservationTime = request.getReservationTime();
        validateBreakTime(storeBreakTime, reservationTime);

        // 4. 요청 시간대에 이미 예약이 존재하는지 확인
        validateDuplicateReservation(request);

        // 5. 예약 확인을 위한 예약 코드 생성
        String reservationCode = generateReservationCode();

        // 6. 예약
        Reservation reservation = saveReservation(request, store, customer, reservationCode);

        // 7. 예약 생성 응답 생성
        return createReservationRegistrationResponse(reservationCode, reservation);

    }

    /** 아이디로 매장 조회 */
    private Store getStoreById(Long storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(INVALID_STORE_ID));
    }

    /** 아이디로 회원 조회 */
    private Member getCustomerById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(INVALID_MEMBER_ID));
    }


    /** 영업중인 매장인지 확인 */
    private static void validateStoreOpen(Store store) {
        if (store.getStoreStatus() != OPEN) {
            throw new StoreException(STORE_CLOSED);
        }
    }

    /** 예약 시간이 휴무 시간에 포함되는지 확인 */
    private void validateBreakTime(String storeBreakTime, String reservationTime) {
        if (storeBreakTime != null
                && !storeBreakTime.isEmpty()
                && reservationTime != null
                && isReservationDuringBreakTime(reservationTime, storeBreakTime)) {
            throw new ReservationException(NOT_AVAILABLE_DURING_BREAK_TIME);
        }
    }

    /** 예약 시간에 중복된 예약이 있는지 확인 */
    private void validateDuplicateReservation(ReservationRegistration.Request request) {
        if (reservationRepository.existsByReservationDateAndReservationTime(request.getReservationDate(), request.getReservationTime())) {
            throw new ReservationException(DUPLICATE_RESERVATION);
        }
    }

    /** 예약 저장(등록) */
    private Reservation saveReservation(ReservationRegistration.Request request, Store store, Member customer, String reservationCode) {
        return reservationRepository.save(Reservation.builder()
                .store(store)
                .customer(customer)
                .reservationDate(request.getReservationDate())
                .reservationTime(request.getReservationTime())
                .reservationMemo(request.getReservationMemo())
                .numberOfCustomer(request.getNumberOfCustomer())
                .reservationCode(reservationCode)
                .reservationVisitStatus(NOT_VISITED)
                .reservationApprovalStatus(PENDING)
                .build());
    }

    /** 예약 등록 응답 생성 */
    private static ReservationRegistration.Response createReservationRegistrationResponse(String reservationCode, Reservation reservation) {
        return ReservationRegistration.Response.builder()
                .id(reservation.getId())
                .reservationCode(reservationCode)
                .build();
    }

    /** 예약 취소 */
    @Override
    @Transactional
    public void cancelReservation(Long reservationId) {
        // 1. 예약 정보 조회
        Reservation reservation = getReservationById(reservationId);

        // 2. 유효성 검사(예약 소유자, 예약 상태 확인)
        validateUserOwnership(reservation);
        validateReservationStatus(reservation);

        // 4. 예약 취소 및 상태 업데이트
        cancelReservationAndUpdateStatus(reservation);
    }

    /** 예약 정보 조회 */
    private Reservation getReservationById(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException(INVALID_RESERVATION_ID));
    }

    /** 예약 손님 확인 */
    private void validateUserOwnership(Reservation reservation) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUserEmail = authentication.getName();
        String customerEmail = reservation.getCustomer().getEmail();

        if (!Objects.equals(customerEmail, authenticatedUserEmail)) {
            throw new ReservationException(ACCESS_DENIED_FOR_CANCELLATION);
        }
    }

    /** 예약 상태 확인 */
    private void validateReservationStatus(Reservation reservation) {
        // 이미 방문한 예약인 경우 예외 발생
        if (reservation.getReservationVisitStatus() == VISITED_WITHIN_RESERVATION_TIME) {
            throw new ReservationException(ALREADY_VISITED_RESERVATION);
        }

        // 이미 취소된 예약인 경우 예외 발생
        if (reservation.getReservationVisitStatus() == CANCELLED_NOT_VISITED || reservation.getReservationVisitStatus() == CANCELLED_NO_SHOW) {
            throw new ReservationException(ALREADY_CANCELED_RESERVATION);
        }
    }

    /** 예약 상태를 '취소로 인한 미방문'으로 업데이트 */
    private void cancelReservationAndUpdateStatus(Reservation reservation) {
        reservation.updateReservationVisitStatus(CANCELLED_NOT_VISITED);
        reservationRepository.save(reservation);
    }

    /** 키오스크 예약 방문 확인 */
    @Override
    @Transactional
    public void confirmVisit(Long reservationId, String reservationCode) {
        // 1. 예약 확인
        Reservation reservation = getReservationById(reservationId);

        // 2. 유효성 검사(예약 승인 여부, 예약 시간 10분 전 도착 확인)
        LocalDateTime reservedAt = LocalDateTime.of(reservation.getReservationDate(), LocalTime.parse(reservation.getReservationTime()));
        validateReservationApprovalStatus(reservation);
        validateArrivalTime(reservedAt);

        // 3. 예약 코드 일치 여부 확인
        validateReservationCode(reservation, reservationCode);

        // 4. 방문 상태 변경
        reservation.updateReservationVisitStatus(VISITED_WITHIN_RESERVATION_TIME);
        reservationRepository.save(reservation);
    }

    /** 예약 승인 여부 확인 */
    private void validateReservationApprovalStatus(Reservation reservation) {
        ReservationApprovalStatus approvalStatus = reservation.getReservationApprovalStatus();
        if (approvalStatus == ReservationApprovalStatus.PENDING) {
            throw new ReservationException(UNAPPROVED_RESERVATION);
        } else if (approvalStatus == ReservationApprovalStatus.REJECTED) {
            throw new ReservationException(DECLINED_RESERVATION);
        }
    }

    /** 예약 시간 10분 전에 도착했는지 확인 */
    private void validateArrivalTime(LocalDateTime reservedAt) {
        LocalDateTime thresholdTime = LocalDateTime.now().minusMinutes(ARRIVAL_THRESHOLD_MINUTES);
        if (reservedAt.isBefore(thresholdTime)) {
            throw new ReservationException(UNABLE_TO_CONFIRM_RESERVATION);
        }
    }

    /** 예약 코드 일치 여부 확인 */
    private void validateReservationCode(Reservation reservation, String reservationCode) {
        if (!reservation.getReservationCode().equals(reservationCode)) {
            throw new ReservationException(MISMATCHED_RESERVATION_CODE);
        }
    }

    /** 매장 점주가 매장의 매장 정보와 해당 매장의 모든 예약 확인 */
    @Override
    public List<ReservationDto> getReservationsByStoreId(Long storeId) {
        // 매장 조회
        Store store = getStoreById(storeId);

        // 매장과 관련된 모든 예약 조회
        List<Reservation> reservations = getReservationsByStore(store);

        return reservations.stream()
                .map(ReservationDto::fromEntity)
                .collect(Collectors.toList());
    }

    /** 매장과 관련된 모든 예약 조회 */
    private List<Reservation> getReservationsByStore(Store store) {
        List<Reservation> reservations = reservationRepository.findByStore(store);
        if (reservations.isEmpty()) {
            throw new ReservationException(NO_RESERVATION_RELATED_TO_STORE);
        }

        return reservations;
    }

    /** 예약 승인 및 거절 */
    @Override
    @Transactional
    public void approveOrRejectReservation(Long reservationId, ReservationApprovalStatus approvalStatus) {
        // 1. 예약 확인
        Reservation reservation = getReservationById(reservationId);

        // 2. 유효성 검사(예약 승인 또는 거절하였거나 이미 승인된 예약 확인)
        validateApprovalAuthority(reservation);
        validatePendingReservation(reservation);

        // 3. 예약 승인 또는 거절 처리
        processReservationApproval(reservation, approvalStatus);
    }

    /** 예약 승인 또는 거절 권한 확인 */
    private void validateApprovalAuthority(Reservation reservation) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUserEmail = authentication.getName();
        Member owner = reservation.getStore().getOwner();
        String ownerEmail = owner.getEmail();
        if (!Objects.equals(ownerEmail, authenticatedUserEmail)) {
            throw new ReservationException(ACCESS_DENIED_APPROVE_OR_REJECT);
        }
    }

    /** 이미 승인된 예약인 경우 예외 처리 */
    private void validatePendingReservation(Reservation reservation) {
        if (reservation.getReservationApprovalStatus() != PENDING) {
            throw new ReservationException(ALREADY_PROCESSED_RESERVATION);
        }
    }

    /** 예약 승인 또는 거절 처리 */
    private void processReservationApproval(Reservation reservation, ReservationApprovalStatus approvalStatus) {
        reservation.updateReservationApprovalStatus(approvalStatus);
        reservationRepository.save(reservation);
    }

    /** 예약 요청 시간이 휴무 시간의 포함되는지 확인 */
    private boolean isReservationDuringBreakTime(String reservationTime, String storeBreakTime) {
        String[] breakTimeParts = storeBreakTime.split(" - ");
        String breakStartTime = breakTimeParts[0];
        String breakEndTime = breakTimeParts[1];

        // 예약 요청 시간을 시간과 분으로 분리
        String[] reservationTimeParts = reservationTime.split(":");
        int reservationHour = Integer.parseInt(reservationTimeParts[0]);
        int reservationMinute = Integer.parseInt(reservationTimeParts[1]);

        // 휴무 시작 시간을 시간과 분으로 분리
        String[] startTimeParts = breakStartTime.split(":");
        int startHour = Integer.parseInt(startTimeParts[0]);
        int startMinute = Integer.parseInt(startTimeParts[1]);

        // 휴무 종료 시간을 시간과 분으로 분리
        String[] endTimeParts = breakEndTime.split(":");
        int endHour = Integer.parseInt(endTimeParts[0]);
        int endMinute = Integer.parseInt(endTimeParts[1]);

        // 예약 요청 시간이 휴무 시간 범위에 속하는지 확인
        if (reservationHour > startHour && reservationHour < endHour) {
            return true;
        } else if (reservationHour == startHour && reservationMinute >= startMinute) {
            return true;
        } else return reservationHour == endHour && reservationMinute < endMinute;
    }

    /** 예약 코드 생성 */
    private static String generateReservationCode() {
        Random random = new Random();
        int randomNumber = random.nextInt(10000); // 0 <= randomNumber <10000

        return String.format("%04d", randomNumber);
    }
}
