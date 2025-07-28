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
}
