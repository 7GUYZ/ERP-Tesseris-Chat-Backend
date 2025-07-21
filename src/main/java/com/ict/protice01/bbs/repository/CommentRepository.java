package com.ict.protice01.bbs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ict.protice01.bbs.entity.CommentEntity;

public interface CommentRepository extends JpaRepository<CommentEntity, Long>{
    
}
