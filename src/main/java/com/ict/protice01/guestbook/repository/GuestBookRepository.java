package com.ict.protice01.guestbook.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ict.protice01.guestbook.entity.GuestBookEntity;

public interface GuestBookRepository extends JpaRepository<GuestBookEntity, Long>{

}
