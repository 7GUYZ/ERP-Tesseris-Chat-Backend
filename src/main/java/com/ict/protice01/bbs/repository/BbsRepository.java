package com.ict.protice01.bbs.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ict.protice01.bbs.entity.BbsEntity;


public interface BbsRepository extends JpaRepository<BbsEntity, Long>{
    Page<BbsEntity> findByActive(int active, Pageable pageable);
}
