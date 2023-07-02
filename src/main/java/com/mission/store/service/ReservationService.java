package com.mission.store.service;

import com.mission.store.domain.Member;
import com.mission.store.domain.Reservation;
import com.mission.store.domain.Store;
import com.mission.store.dto.ReservationDto;
import com.mission.store.dto.ReservationRegistration;
import com.mission.store.repository.MemberRepository;
import com.mission.store.repository.ReservationRepository;
import com.mission.store.repository.StoreRepository;
import com.mission.store.type.ReservationApprovalStatus;
import com.mission.store.type.StoreStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import static com.mission.store.type.ReservationApprovalStatus.PENDING;
import static com.mission.store.type.ReservationApprovalStatus.REJECTED;
import static com.mission.store.type.ReservationVisitStatus.*;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private static final int ARRIVAL_THRESHOLD_MINUTES = 10;

    private final ReservationRepository reservationRepository;
    private final StoreRepository storeRepository;
    private final MemberRepository memberRepository;

    /** 예약 요청 */
    public ReservationRegistration.Response reserve(ReservationRegistration.Request request) {
        Long storeId = request.getStoreId();
        Long userId = request.getCustomerId();

        // 1. 매장 및 유저 확인
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 매장입니다."));

        Member customer = memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));

        // 2. 매장 영업 여부 확인
        if (store.getStoreStatus() != StoreStatus.OPEN) {
            throw new RuntimeException("매장이 현재 영업 중이지 않습니다.");
        }

        // 3. 휴무 시간 예약 여부 확인
        String storeBreakTime = store.getBreakTime();
        String reservationTime = request.getReservationTime();
        if (storeBreakTime != null && !storeBreakTime.isEmpty() && reservationTime != null && isReservationDuringBreakTime(reservationTime, storeBreakTime)) {
            throw new IllegalStateException("휴무 시간에는 예약이 불가능합니다.");
        }

        // 4. 요청 시간대에 이미 예약이 존재하는지 확인
        if (reservationRepository.existsByReservationDateAndReservationTime(request.getReservationDate(), request.getReservationTime())) {
            // TODO DuplicateReservationException
            throw new RuntimeException("요청 시간에 중복된 예약이 존재합니다.");
        }

        // 5. 예약 확인을 위한 예약 코드 생성
        String reservationCode = generateReservationCode();

        // 6. 예약
        Reservation reservation = reservationRepository.save(Reservation.builder()
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

        return ReservationRegistration.Response.builder()
                .id(reservation.getId())
                .reservationCode(reservationCode)
                .build();

    }

    /** 예약 취소 */
    public void cancelReservation(Long reservationId) {
        // 1. 예약 여부 확인
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("예약을 찾을 수 없습니다."));
        
        // 2. 예약 취소를 요청하는 사용자가 본인인지 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUserEmail = authentication.getName();
        String customerEmail = reservation.getCustomer().getEmail();
        if (!Objects.equals(customerEmail, authenticatedUserEmail)) {
            throw new RuntimeException("예약 취소는 본인만 가능합니다.");
        }

        // 3. 이미 방문한 예약이라면 취소할 수 없음
        if (reservation.getReservationVisitStatus() == VISITED_WITHIN_RESERVATION_TIME) {
            throw new RuntimeException("이미 방문한 예약은 취소할 수 없습니다.");
        }

        // 4. 이미 취소된 예약이라면 중복 취소 처리
        if (reservation.getReservationVisitStatus() == CANCELLED_NOT_VISITED || reservation.getReservationVisitStatus() == CANCELLED_NO_SHOW) {
            throw new RuntimeException("이미 취소된 예약입니다.");
        }

        // 5. 예약 취소
        reservation.updateReservationVisitStatus(CANCELLED_NOT_VISITED);
        reservationRepository.save(reservation);
    }

    /** 키오스크 예약 방문 확인 */
    public void confirmVisit(Long reservationId, String reservationCode) {
        // 1. 예약 확인
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("예약을 찾을 수 없습니다."));

        // 2. 예약 승인 여부 확인
        ReservationApprovalStatus reservationApprovalStatus = reservation.getReservationApprovalStatus();
        if (reservationApprovalStatus == PENDING) {
            throw new RuntimeException("예약이 승인되지 않았습니다.");
        } else if (reservationApprovalStatus == REJECTED) {
            throw new RuntimeException("거절된 예약입니다.");
        }

        // 3. 예약 시간 10분전에 도착했는지 확인
        LocalDateTime reservedAt = LocalDateTime.of(reservation.getReservationDate(), LocalTime.parse(reservation.getReservationTime()));
        if (reservedAt.isBefore(LocalDateTime.now().minusMinutes(ARRIVAL_THRESHOLD_MINUTES))) {
            throw new RuntimeException("방문 10분 전에만 예약 확인이 가능합니다.");
        }

        // 4. 예약 코드 일치 여부 확인
        if (!reservation.getReservationCode().equals(reservationCode)) {
            throw new RuntimeException("예약 코드가 일치하지 않습니다.");
        }

        // 5. 방문 상태 변경
        reservation.updateReservationVisitStatus(VISITED_WITHIN_RESERVATION_TIME);
        reservationRepository.save(reservation);
    }
    
    /** 매장 점주가 매장의 매장 정보와 해당 매장의 모든 예약 확인 */
    public List<ReservationDto> getReservationsByStoreId(Long storeId) {
        // 매장 조회
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("매장을 찾을 수 없습니다."));

        // 매장과 관련된 모든 예약 조회
        List<Reservation> reservations = reservationRepository.findByStore(store);
        if (reservations.size() == 0) {
            throw new RuntimeException("매장과 관련된 예약이 존재하지 않습니다.");
        }

        return reservations.stream()
                .map(ReservationDto::fromEntity)
                .collect(Collectors.toList());
    }

    /** 예약 승인 및 거절 */
    public void approveOrRejectReservation(Long reservationId, ReservationApprovalStatus approvalStatus) {
        // 1. 예약 확인
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("예약을 찾을 수 없습니다."));

        // 2. 예약 승인 또는 거절 권한 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUserEmail = authentication.getName();
        Member owner = reservation.getStore().getOwner();
        String ownerEmail = owner.getEmail();
        if (!Objects.equals(ownerEmail, authenticatedUserEmail)) {
            throw new RuntimeException("예약 승인 또는 거절은 해당 매장의 주인만 가능합니다.");
        }

        // 3. 이미 승인된 예약인 경우 예외 처리
        if (reservation.getReservationApprovalStatus() != PENDING) {
            throw new RuntimeException("승인이 처리된 예약입니다.");
        }

        // 4. 예약 승인 또는 거절 처리
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
        } else return reservationHour == endHour && reservationMinute <= endMinute;
    }

    /** 예약 코드 생성 */
    public static String generateReservationCode() {
        Random random = new Random();
        int randomNumber = random.nextInt(10000); // 0 <= randomNumber <10000

        return String.format("%04d", randomNumber);
    }
}
