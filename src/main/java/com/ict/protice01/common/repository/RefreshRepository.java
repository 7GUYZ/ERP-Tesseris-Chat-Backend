package com.ict.protice01.common.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ict.protice01.common.entity.RefreshEntity;

public interface RefreshRepository extends JpaRepository<RefreshEntity, String> {
    Optional<RefreshEntity> findByMid(String m_id);
}
