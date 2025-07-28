package com.ict.edu03.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ict.edu03.chat.entity.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    
}
