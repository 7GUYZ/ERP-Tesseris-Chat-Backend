package com.ict.edu03.chat.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.ict.edu03.chat.entity.RoomParticipants;

@Repository
public interface RoomParticipantsRepository extends JpaRepository<RoomParticipants, Long> {
    // @Query(value = "SELECT * FROM roomparticipants rp LEFT JOIN room rm ON rp.room_index = rm.room_index WHERE rp.user_id = :userid", nativeQuery = true)
    List<RoomParticipants> findByUserid(String userid);
    List<RoomParticipants> findByRoomindex(Long roomindex);
    //방 알람체크
    RoomParticipants findByUseridAndRoomindex(String userid, Long roomindex);
    //방에 몇명이 남았는지 체크
    List<RoomParticipants> findByRoomindexAndLeftatIsNull(Long roomindex);
}
