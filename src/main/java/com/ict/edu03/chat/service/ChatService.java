package com.ict.edu03.chat.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ict.edu03.chat.dto.SearchResponseDTO;
import com.ict.edu03.chat.entity.ChatLog;
import com.ict.edu03.chat.entity.Message;
import com.ict.edu03.chat.entity.RoomParticipants;
import com.ict.edu03.chat.repository.ChatLogRepository;
import com.ict.edu03.chat.repository.MessageReadsRepository;
import com.ict.edu03.chat.repository.MessageRepository;
import com.ict.edu03.chat.repository.RoomParticipantsRepository;
import com.ict.edu03.chat.repository.RoomRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {
    private final ChatLogRepository chatLogRepository;
    private final RoomRepository roomRepository;
    private final RoomParticipantsRepository roomParticipantsRepository;
    private final MessageRepository messageRepository;
    private final MessageReadsRepository messageReadRepository;

    /**
     * Search Room
     */
    public List<SearchResponseDTO> SearchRoom(String userid) {
        // Entity 조회 및 dto 로 반환
        List<RoomParticipants> roomParticipantsList = roomParticipantsRepository.findByUserid(userid);
        if (roomParticipantsList.isEmpty()) {
            throw new RuntimeException("참여중인 채팅방이 없습니다.");
        }
        return roomParticipantsList.stream()
                .map(roomParticipant -> new SearchResponseDTO(String.valueOf(roomParticipant.getRoomindex()),
                        roomParticipant.getRoom().getRoomname(),
                        roomParticipant.getJoinedat().toString(),
                        roomParticipant.getLeftat() != null ? roomParticipant.getLeftat().toString() : null,
                        roomParticipant.getNotificationsenabled() ? "true" : "false"))
                .collect(Collectors.toList());
    }
}
