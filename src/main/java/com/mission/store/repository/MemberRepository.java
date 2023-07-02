package com.mission.store.repository;

import com.mission.store.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 이메일로 유저 검색
    Optional<Member> findByEmail(String email);

    // 전화번호로 유저 검색
    Optional<Member> findByPhone(String phone);

    // 닉네임으로 유저 검색
    Optional<Member> findByNickname(String nickname);
}
