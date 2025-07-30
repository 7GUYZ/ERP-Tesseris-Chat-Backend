package com.ict.edu03.chat.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.text.html.parser.Entity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ict.edu03.chat.dto.ResponseDTO;
import com.ict.edu03.chat.dto.SearchResponseDTO;
import com.ict.edu03.chat.dto.RequestDTO.AlarmCheckRequestDTO;
import com.ict.edu03.chat.dto.RequestDTO.InvitationRequestDTO;
import com.ict.edu03.chat.dto.RequestDTO.MessageRequestDTO;
import com.ict.edu03.chat.entity.ChatLog;
import com.ict.edu03.chat.entity.Message;
import com.ict.edu03.chat.entity.MessageReads;
import com.ict.edu03.chat.entity.Room;
import com.ict.edu03.chat.entity.RoomParticipants;
import com.ict.edu03.chat.repository.ChatLogRepository;
import com.ict.edu03.chat.repository.MessageReadsRepository;
import com.ict.edu03.chat.repository.MessageRepository;
import com.ict.edu03.chat.repository.RoomParticipantsRepository;
import com.ict.edu03.chat.repository.RoomRepository;

import jakarta.transaction.Transactional;
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
                                .map(roomParticipant -> new SearchResponseDTO(
                                                String.valueOf(roomParticipant.getRoomindex()),
                                                roomParticipant.getRoom().getRoomname(),
                                                roomParticipant.getJoinedat().toString(),
                                                roomParticipant.getLeftat() != null
                                                                ? roomParticipant.getLeftat().toString()
                                                                : null,
                                                roomParticipant.getNotificationsenabled() ? "true" : "false"))
                                .collect(Collectors.toList());
        }

        /**
         * Send Message
         */
        @Transactional
        public ResponseDTO<?> sendMessage(MessageRequestDTO messageRequestDTO, List<MultipartFile> files) {
                try {
                        if (messageRequestDTO.getRoom_index() == null || !roomRepository.existsById(Long.parseLong(messageRequestDTO.getRoom_index()))) {
                                // 방 생성
                                Room savedRoom = roomRepository.save(Room.builder()
                                                .roomname(messageRequestDTO.getRoom_name() != null
                                                                ? messageRequestDTO.getRoom_name()
                                                                : messageRequestDTO.getUser_id())
                                                .createdat(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                                                .createdby(null)
                                                .build());
                                log.info("방 생성 완료");
                                // 방 참여자 저장
                                for (String participantId : messageRequestDTO.getParticipants()) {
                                        roomParticipantsRepository.save(RoomParticipants.builder()
                                                        .joinedat(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                                                        .leftat(null)
                                                        .notificationsenabled(true)
                                                        .userid(participantId)
                                                        .roomindex(savedRoom.getRoomindex())
                                                        .build());
                                        log.info("방 참여자 누구{} 저장 완료", participantId);

                                        // 방 참여자 로그 저장
                                        chatLogRepository.save(
                                                        ChatLog.builder()
                                                                        .message("관리자 : 방생성")
                                                                        .logtype("RoomCreate")
                                                                        .sentat(LocalDateTime
                                                                                        .now(ZoneId.of("Asia/Seoul")))
                                                                        .roomindex(savedRoom.getRoomindex())
                                                                        .userid(messageRequestDTO.getUser_id() != null
                                                                                        ? messageRequestDTO.getUser_id()
                                                                                        : "7c43ea93-44ed-4999-9eec-77f4af8c6025")
                                                                        .build());
                                        log.info("{}의 방생성 완료", participantId);
                                }
                                roomParticipantsRepository.save(RoomParticipants.builder()
                                .joinedat(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                                .leftat(null)
                                .notificationsenabled(true)
                                .userid(messageRequestDTO.getUser_id())
                                .roomindex(savedRoom.getRoomindex())
                                .build());
                                chatLogRepository.save(ChatLog.builder()
                                .message("관리자 : 방생성")
                                .logtype("RoomCreate")
                                .sentat(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                                .roomindex(savedRoom.getRoomindex())
                                .userid(messageRequestDTO.getUser_id() != null
                                ? messageRequestDTO.getUser_id()
                                : "7c43ea93-44ed-4999-9eec-77f4af8c6025")
                                .build());
                                // 메시지 저장
                                Message savedMessage = messageRepository.save(Message.builder()
                                                .userid(messageRequestDTO.getUser_id() != null
                                                                ? messageRequestDTO.getUser_id()
                                                                : "7c43ea93-44ed-4999-9eec-77f4af8c6025")
                                                .sentat(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                                                .message(messageRequestDTO.getMessage())
                                                .roomindex(savedRoom.getRoomindex())
                                                .active(true)
                                                .build());
                                log.info("메시지 저장 완료");
                                // 메세지 읽었는지 확인
                                for (String participantId : messageRequestDTO.getParticipants()) {
                                        messageReadRepository.save(MessageReads.builder()
                                                        .messageindex(savedMessage.getMessageindex())
                                                        .userid(participantId)
                                                        .readat(null)
                                                        .build());
                                        log.info("{}의 메세지 읽었는지 확인", participantId);
                                }
                                messageReadRepository.save(MessageReads.builder()
                                .messageindex(savedMessage.getMessageindex())
                                .userid(messageRequestDTO.getUser_id())
                                .readat(null)
                                .build());
                                // 메세지 보낸 본인은 읽음 처리
                                MessageReads messageReads = messageReadRepository.findByMessageindexAndUserid(
                                                savedMessage.getMessageindex(), messageRequestDTO.getUser_id());
                                if (messageReads != null && messageReads.getReadat() == null) {
                                        messageReads.setReadat(LocalDateTime.now(ZoneId.of("Asia/Seoul")));
                                        messageReadRepository.save(messageReads);
                                        log.info("{}의 메세지 읽음 처리 완료", messageRequestDTO.getUser_id());
                                }
                                // 메시지 로그 저장
                                chatLogRepository.save(ChatLog.builder()
                                                .message(messageRequestDTO.getMessage())
                                                .logtype("Message")
                                                .sentat(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                                                .roomindex(savedRoom.getRoomindex())
                                                .userid(messageRequestDTO.getUser_id() != null
                                                                ? messageRequestDTO.getUser_id()
                                                                : "7c43ea93-44ed-4999-9eec-77f4af8c6025")
                                                .build());
                                log.info("채팅 로그 저장 완료");
                                return ResponseDTO.createSuccessResponse("방 생성 및 메세지 전송 성공", null);
                        } else {
                                // 방에 메세지를 보낼때
                                Message savedMessage = messageRepository.save(Message.builder()
                                                .userid(messageRequestDTO.getUser_id() != null
                                                                ? messageRequestDTO.getUser_id()
                                                                : "7c43ea93-44ed-4999-9eec-77f4af8c6025")
                                                .sentat(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                                                .message(messageRequestDTO.getMessage())
                                                .active(true)
                                                .roomindex(Long.parseLong(messageRequestDTO.getRoom_index()))
                                                .build());
                                log.info("채팅 저장 완료");
                                chatLogRepository.save(ChatLog.builder()
                                                .message(messageRequestDTO.getMessage())
                                                .logtype("Message")
                                                .sentat(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                                                .roomindex(Long.parseLong(messageRequestDTO.getRoom_index()))
                                                .userid(messageRequestDTO.getUser_id() != null
                                                                ? messageRequestDTO.getUser_id()
                                                                : "7c43ea93-44ed-4999-9eec-77f4af8c6025")
                                                .build());
                                log.info("채팅 로그 저장 완료");
                                for (RoomParticipants participantId : roomParticipantsRepository
                                                .findByRoomindex(Long.parseLong(messageRequestDTO.getRoom_index()))) {
                                        messageReadRepository.save(MessageReads.builder()
                                                        .messageindex(savedMessage.getMessageindex())
                                                        .userid(participantId.getUserid())
                                                        .readat(participantId.getUserid().equals(messageRequestDTO.getUser_id())
                                                                ? LocalDateTime.now(ZoneId.of("Asia/Seoul"))
                                                                : null)
                                                        .build());

                                        log.info("{}의 메세지 읽었는지 확인", participantId.getUserid());
                                }
                                return ResponseDTO.createSuccessResponse("메세지 전송 성공", null);
                        }
                } catch (Exception e) {
                        log.error("RoomCreate and SendMessage Error: {}", e.getMessage());
                        return ResponseDTO.createErrorResponse(500, "서버오류" + e.getMessage());
                }
        }

        /**
         * user invitation
         */
        @Transactional
        public ResponseEntity<?> userInvitation(String room, InvitationRequestDTO invitationRequestDTO) {
                if (!roomRepository.existsById(Long.parseLong(room))) {
                        throw new RuntimeException("존재하지 않는 방입니다.");
                }
                // 방 참여자 저장
                for (String userid : invitationRequestDTO.getUserid()) {
                        roomParticipantsRepository.save(RoomParticipants.builder()
                                        .userid(userid)
                                        .roomindex(Long.parseLong(room))
                                        .joinedat(LocalDateTime.now())
                                        .leftat(null)
                                        .notificationsenabled(true)
                                        .build());
                        log.info("{}의 방 참여자 저장 완료", userid);
                        // 방 참여자 로그 저장
                        chatLogRepository.save(ChatLog.builder()
                                        .message(String.format("%s님이 %s님을 방에 초대하였습니다.",
                                                        invitationRequestDTO.getInviter(), userid))
                                        .logtype("사용자 초대")
                                        .sentat(LocalDateTime.now())
                                        .roomindex(Long.parseLong(room))
                                        .userid(invitationRequestDTO.getInviter())
                                        .build());
                        log.info("{}의 방 참여자 로그 저장 완료", userid);
                }
                return ResponseEntity.ok(ResponseDTO.createSuccessResponse("유저 초대 성공", null));
        }

        /**
         * check alram
         */
        @Transactional
        public ResponseEntity<?> checkAlram(AlarmCheckRequestDTO alarmCheck) {
                try {
                        if (!roomRepository.existsById(Long.parseLong(alarmCheck.getRoom_index()))) {
                                throw new RuntimeException("존재하지 않는 방입니다.");
                        }
                        // 기존데이터 조회
                        RoomParticipants roomParticipants = roomParticipantsRepository
                                        .findByUseridAndRoomindex(alarmCheck.getUser_id(),
                                                        Long.parseLong(alarmCheck.getRoom_index()));
                        if (roomParticipants != null) {
                                roomParticipants.setNotificationsenabled(
                                                alarmCheck.getAlarm_index().equals("true") ? true : false);
                                roomParticipantsRepository.save(roomParticipants);
                                log.info("{}의 알림 저장 성공", alarmCheck.getUser_id());
                        }
                        return ResponseEntity.ok(ResponseDTO.createSuccessResponse("알림 저장 성공", null));
                } catch (Exception e) {
                        log.error("CheckAlram Error: {}", e.getMessage());
                        return ResponseEntity.ok(ResponseDTO.createErrorResponse(404, e.getMessage()));
                }
        }

        /**
         * Chat List
         */
        public List<Message> ChatList(String room, String userid, int page, int size) {
                try {
                        Page<Message> messages = messageRepository.findByRoomindexOrderBySentatDesc(
                                        Long.parseLong(room),
                                        PageRequest.of(page, size));
                        // 메세지 읽음 처리
                        // 사용자 읽음 상태 조회
                        List<MessageReads> messageReads = messageReadRepository.findMessageReadsByRoomAndUser(
                                        Long.parseLong(room), userid);

                        // 읽지않은 메세지 수집
                        List<MessageReads> unreadMessages = new ArrayList<>();
                        for (Message message : messages.getContent()) {
                                MessageReads messageRead = messageReads.stream()
                                                .filter(read -> read.getMessageindex()
                                                                .equals(message.getMessageindex()))
                                                .findFirst()
                                                .orElse(null);
                                // 읽지않은 메세지 시간 넣어주고 배열로 담기
                                if (messageRead != null && messageRead.getReadat() == null) {
                                        messageRead.setReadat(LocalDateTime.now(ZoneId.of("Asia/Seoul")));
                                        unreadMessages.add(messageRead);
                                }
                        }
                        // 읽지 않은 메세지 한번에 업데이트
                        if (!unreadMessages.isEmpty()) {
                                messageReadRepository.saveAll(unreadMessages);
                        }
                        log.info("채팅 내용 조회 성공2 : {}", messages.getContent().size());
                        return messages.getContent();
                } catch (Exception e) {
                        log.error("ChatList Error: {}", e.getMessage());
                        return null;
                }
        }

        /**
         * Message Read
         */
        public ResponseEntity<?> MessageRead(String room, String messageid, String userid) {
                try {
                        List<MessageReads> messageReads = messageReadRepository
                                        .findMessageReadsByRoomAndUser(Long.parseLong(room), userid);
                        if (messageReads.isEmpty()) {
                                return ResponseEntity.ok(ResponseDTO.createErrorResponse(404, "읽음 처리 실패"));
                        }
                        for (MessageReads messageRead : messageReads) {
                                if (messageRead.getReadat() == null) {
                                        messageRead.setReadat(LocalDateTime.now(ZoneId.of("Asia/Seoul")));
                                        messageReadRepository.save(messageRead);
                                }
                        }
                        return ResponseEntity.ok(ResponseDTO.createSuccessResponse("읽음 처리 성공", null));
                } catch (Exception e) {
                        log.error("MessageRead Error: {}", e.getMessage());
                        return ResponseEntity.ok(ResponseDTO.createErrorResponse(404, e.getMessage()));
                }
        }

        /**
         * 방 퇴장시 나간 사람
         */
        public ResponseDTO<?> Leave(String room, String userid) {
                try {
                        RoomParticipants roomParticipants = roomParticipantsRepository.findByUseridAndRoomindex(userid,
                                        Long.parseLong(room));
                        if (roomParticipants != null && roomParticipants.getLeftat() == null) {
                                roomParticipants.setLeftat(LocalDateTime.now(ZoneId.of("Asia/Seoul")));
                                roomParticipantsRepository.save(roomParticipants);
                                log.info("{}의 방 퇴장 처리 성공", userid);
                        }
                        // 방에 몇명이 남았는지 체크
                        if (roomParticipantsRepository.findByRoomindexAndLeftatIsNull(Long.parseLong(room))
                                        .size() == 0) {
                                Room rooms = roomRepository.findById(Long.parseLong(room)).orElse(null);
                                if (rooms != null) {
                                        rooms.setCreatedby(LocalDateTime.now(ZoneId.of("Asia/Seoul")));
                                        roomRepository.save(rooms);
                                        log.info("{}의 방 삭제처리 완료", room);
                                }
                        }
                        return ResponseDTO.createSuccessResponse("정상적으로 방에서 나가셨습니다.", null);
                } catch (Exception e) {
                        log.error("Leave Error: {}", e.getMessage());
                        return ResponseDTO.createErrorResponse(404, e.getMessage());
                }
        }
}
