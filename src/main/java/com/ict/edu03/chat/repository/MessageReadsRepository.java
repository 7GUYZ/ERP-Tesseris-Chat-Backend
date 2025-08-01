package com.ict.edu03.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ict.edu03.chat.entity.MessageReads;

@Repository
public interface MessageReadsRepository extends JpaRepository<MessageReads, Long> {
        @Query(value = "SELECT msgrds.* FROM messagereads msgrds " +
                        "LEFT JOIN message msg ON msgrds.message_index = msg.message_index " +
                        "WHERE msg.room_index = :roomindex AND msgrds.user_id = :userid " +
                        "ORDER BY msg.sent_at DESC", nativeQuery = true)
        List<MessageReads> findMessageReadsByRoomAndUser(@Param("roomindex") Long roomindex,
                        @Param("userid") String userid);

        // 보낸사람 메세지 읽음 처리
        MessageReads findByMessageindexAndUserid(Long messageindex, String userid);
}
