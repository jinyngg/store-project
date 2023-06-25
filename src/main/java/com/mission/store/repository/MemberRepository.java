package com.mission.store.repository;

import com.mission.store.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);
    Optional<Member> findByPhone(String phone);
    Optional<Member> findByNickname(String nickname);
}
