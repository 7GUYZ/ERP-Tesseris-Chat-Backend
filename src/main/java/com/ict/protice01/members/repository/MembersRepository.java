package com.ict.protice01.members.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ict.protice01.members.entity.MembersEntity;

public interface MembersRepository extends JpaRepository<MembersEntity, Long> {
    /**
     *  계정 인증
     * @param mId
     * @param mPw
     */
    Optional<MembersEntity> findByMidAndMpw(String mId, String mPw);
    /**
     *  계정 조회
     * @param mId
     */
    Optional<MembersEntity> findByMid(String mId);
}
