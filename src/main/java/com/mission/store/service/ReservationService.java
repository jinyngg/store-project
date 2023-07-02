package com.mission.store.service;

import com.mission.store.domain.Member;
import com.mission.store.domain.Reservation;
import com.mission.store.domain.Store;
import com.mission.store.dto.ReservationRequest;
import com.mission.store.repository.MemberRepository;
import com.mission.store.repository.ReservationRepository;
import com.mission.store.repository.StoreRepository;
import com.mission.store.type.ReservationApprovalStatus;
import com.mission.store.type.StoreStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final StoreRepository storeRepository;
    private final MemberRepository memberRepository;

    /** 당일 예약 요청 */ 
    // TODO Request 필드 값 추가해서 당일 예약이 아닌 그냥 예약으로 변경할지 고민
    public void reserve(ReservationRequest request) {
        Long storeId = request.getStoreId();
        Long userId = request.getUserId();

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
            throw new RuntimeException("중복된 예약이 존재합니다.");
        }

        // 5. 예약
        reservationRepository.save(Reservation.builder()
                .store(store)
                .customer(customer)
                .reservationDate(request.getReservationDate())
                .reservationTime(request.getReservationTime())
                .reservationMemo(request.getReservationMemo())
                .numberOfCustomer(request.getNumberOfCustomer())
                .reservationApprovalStatus(ReservationApprovalStatus.PENDING)
                .build());

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
        } else if (reservationHour == endHour && reservationMinute <= endMinute) {
            return true;
        }

        return false;
    }

    
    /** 예약 승인 및 거절 */
    
    /** 키오스크 예약 확인 */
}
