package com.ict.edu03.chat.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.ict.edu03.chat.dto.ResponseDTO;
import com.ict.edu03.chat.dto.SearchResponseDTO;
import com.ict.edu03.chat.dto.RoomCheckResponseDTO;
import com.ict.edu03.chat.dto.RequestDTO.AlarmCheckRequestDTO;
import com.ict.edu03.chat.dto.RequestDTO.InvitationRequestDTO;
import com.ict.edu03.chat.dto.RequestDTO.MessageRequestDTO;
import com.ict.edu03.chat.entity.ChatLog;
import com.ict.edu03.chat.entity.Message;
import com.ict.edu03.chat.entity.MessageReads;
import com.ict.edu03.chat.entity.Room;
import com.ict.edu03.chat.entity.RoomParticipants;
import com.ict.edu03.chat.entity.files;
import com.ict.edu03.chat.repository.ChatLogRepository;
import com.ict.edu03.chat.repository.FilesRepository;
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
        private final FilesRepository filesRepository;

        /**
         * 파일 정보를 DB에 저장하고 메시지와 연결
         */
        private void saveFilesToDatabase(Message savedMessage, List<String> uploadFiles, String userId, Long roomIndex) {
                if (uploadFiles == null || uploadFiles.isEmpty()) {
                        log.info("저장할 파일이 없습니다.");
                        return;
                }

                log.info("파일 저장 시작: {}개 파일", uploadFiles.size());
                
                // 모든 파일을 저장
                for (String fileUrl : uploadFiles) {
                        try {
                                log.info("파일 저장 중: {}", fileUrl);
                                
                                // 파일 정보 추출 (S3 URL에서 파일명 추출)
                                String fileName = extractFileNameFromUrl(fileUrl);
                                String fileType = extractFileTypeFromUrl(fileUrl);
                                
                                // files 테이블에 저장 (message_index 포함)
                                files savedFile = filesRepository.save(files.builder()
                                                .filename(fileName)
                                                .filepath(fileUrl)
                                                .filetype(fileType)
                                                .uploadedat(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                                                .roomindex(roomIndex)
                                                .userid(userId)
                                                .messageindex(savedMessage.getMessageindex())
                                                .build());
                                
                                log.info("파일 정보 저장 완료: file_index={}, filename={}, message_index={}", 
                                                savedFile.getFileindex(), fileName, savedMessage.getMessageindex());
                                
                                log.info("메시지-파일 연결 저장 완료: message_index={}, file_index={}", 
                                                savedMessage.getMessageindex(), savedFile.getFileindex());
                                
                                // 파일 업로드 로그 저장
                                chatLogRepository.save(ChatLog.builder()
                                                .message(String.format("파일 업로드: %s", fileName))
                                                .logtype("File")
                                                .sentat(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                                                .roomindex(roomIndex)
                                                .userid(userId)
                                                .build());
                                
                                log.info("파일 업로드 로그 저장 완료: filename={}", fileName);
                                
                        } catch (Exception e) {
                                log.error("파일 저장 중 오류 발생: fileUrl={}, error={}", fileUrl, e.getMessage());
                        }
                }
                
                log.info("모든 파일 저장 완료: {}개 파일", uploadFiles.size());
        }
        
        /**
         * S3 URL에서 파일명 추출
         */
        private String extractFileNameFromUrl(String fileUrl) {
                try {
                        String[] parts = fileUrl.split("/");
                        String fileNameWithExtension = parts[parts.length - 1];
                        // UUID와 타임스탬프 제거하고 원본 파일명만 추출
                        if (fileNameWithExtension.contains("_")) {
                                String[] nameParts = fileNameWithExtension.split("_");
                                if (nameParts.length >= 3) {
                                        // 원본 파일명이 있는 경우 (예: chat-files/1/20241201_123456_uuid.jpg)
                                        return nameParts[nameParts.length - 1];
                                }
                        }
                        return fileNameWithExtension;
                } catch (Exception e) {
                        log.warn("파일명 추출 실패: {}", fileUrl);
                        return "unknown_file";
                }
        }
        
        /**
         * S3 URL에서 파일 타입 추출
         */
        private String extractFileTypeFromUrl(String fileUrl) {
                try {
                        if (fileUrl.contains(".")) {
                                return fileUrl.substring(fileUrl.lastIndexOf(".") + 1).toLowerCase();
                        }
                        return "unknown";
                } catch (Exception e) {
                        log.warn("파일 타입 추출 실패: {}", fileUrl);
                        return "unknown";
                }
        }

        /**
         * Search Room
         */
        public ResponseDTO<?> SearchRoom(String userid) {
                // Entity 조회 및 dto 로 반환 (현재 참여 중인 채팅방만 조회)
                List<RoomParticipants> roomParticipantsList = roomParticipantsRepository
                                .findByUseridAndLeftatIsNull(userid);
                if (roomParticipantsList.isEmpty()) {
                        throw new RuntimeException("참여중인 채팅방이 없습니다.");
                }

                // 방 정보를 한 번에 조회하여 N+1 문제 해결
                List<SearchResponseDTO> searchResponseDTOList = roomParticipantsList.stream()
                                .map(roomParticipant -> {
                                        // 방 정보를 별도로 조회하지 않고 room_index만 사용
                                        String roomName = roomParticipant.getRoom() != null
                                                        ? roomParticipant.getRoom().getRoomname()
                                                        : "방 " + roomParticipant.getRoomindex();

                                        // 해당 방의 모든 참가자 조회
                                        List<RoomParticipants> allParticipants = roomParticipantsRepository
                                                        .findByRoomindexAndLeftatIsNull(roomParticipant.getRoomindex());
                                        List<String> participantUserIds = allParticipants.stream()
                                                        .map(RoomParticipants::getUserid)
                                                        .collect(Collectors.toList());

                                        return new SearchResponseDTO(
                                                        String.valueOf(roomParticipant.getRoomindex()),
                                                        roomName,
                                                        roomParticipant.getJoinedat().toString(),
                                                        roomParticipant.getLeftat() != null
                                                                        ? roomParticipant.getLeftat().toString()
                                                                        : null,
                                                        roomParticipant.getNotificationsenabled() ? "true" : "false",
                                                        participantUserIds);
                                })
                                .collect(Collectors.toList());
                return ResponseDTO.createSuccessResponse("참여중인 채팅방 조회 성공", searchResponseDTOList);
        }

        /**
         * Check Room - 1:1 채팅방 존재 여부 확인
         */
        public ResponseDTO<?> checkRoom(MessageRequestDTO messageRequestDTO) {
                try {
                        log.info("checkRoom 호출: participants={}", messageRequestDTO.getParticipants());

                        if (messageRequestDTO.getParticipants() == null
                                        || messageRequestDTO.getParticipants().size() != 2) {
                                log.warn("checkRoom: 1:1 채팅방이 아님 - participants size: {}",
                                                messageRequestDTO.getParticipants() != null
                                                                ? messageRequestDTO.getParticipants().size()
                                                                : "null");
                                return ResponseDTO.createErrorResponse(400, "1:1 채팅방만 확인 가능합니다.");
                        }

                        String user1 = messageRequestDTO.getParticipants().get(0);
                        String user2 = messageRequestDTO.getParticipants().get(1);

                        log.info("checkRoom: 사용자1={}, 사용자2={}", user1, user2);

                        // 두 사용자가 모두 참여하고 있는 1:1 채팅방 찾기
                        List<RoomParticipants> user1Rooms = roomParticipantsRepository
                                        .findByUseridAndLeftatIsNull(user1);
                        List<RoomParticipants> user2Rooms = roomParticipantsRepository
                                        .findByUseridAndLeftatIsNull(user2);

                        log.info("checkRoom: 사용자1 참여 방 수={}, 사용자2 참여 방 수={}", user1Rooms.size(), user2Rooms.size());

                        // 각 사용자의 방 정보 로깅
                        log.info("checkRoom: 사용자1의 방 목록:");
                        for (RoomParticipants rp : user1Rooms) {
                                log.info("  - room_index={}, userid={}", rp.getRoomindex(), rp.getUserid());
                        }
                        log.info("checkRoom: 사용자2의 방 목록:");
                        for (RoomParticipants rp : user2Rooms) {
                                log.info("  - room_index={}, userid={}", rp.getRoomindex(), rp.getUserid());
                        }

                        // 두 사용자가 공통으로 참여하고 있는 방 찾기
                        Set<Long> user1RoomIndexes = user1Rooms.stream()
                                        .map(RoomParticipants::getRoomindex)
                                        .collect(Collectors.toSet());

                        Set<Long> user2RoomIndexes = user2Rooms.stream()
                                        .map(RoomParticipants::getRoomindex)
                                        .collect(Collectors.toSet());

                        log.info("checkRoom: 사용자1 방 인덱스={}, 사용자2 방 인덱스={}", user1RoomIndexes, user2RoomIndexes);

                        // 공통 방 찾기
                        Set<Long> commonRooms = new HashSet<>(user1RoomIndexes);
                        commonRooms.retainAll(user2RoomIndexes);

                        log.info("checkRoom: 공통 방 인덱스={}", commonRooms);

                        if (!commonRooms.isEmpty()) {
                                // 1:1 채팅방이 존재하는 경우
                                Long existingRoomIndex = commonRooms.iterator().next();

                                // 해당 방의 참여자 수 확인 (1:1 채팅방인지 확인)
                                List<RoomParticipants> roomParticipants = roomParticipantsRepository
                                                .findByRoomindexAndLeftatIsNull(existingRoomIndex);

                                log.info("checkRoom: 방 {}의 참여자 수={}", existingRoomIndex, roomParticipants.size());

                                if (roomParticipants.size() == 2) {
                                        // 1:1 채팅방이 맞음
                                        Room room = roomRepository.findById(existingRoomIndex).orElse(null);
                                        if (room != null) {
                                                log.info("checkRoom: 기존 1:1 채팅방 발견 - room_index={}, room_name={}",
                                                                existingRoomIndex, room.getRoomname());

                                                // 프론트엔드가 기대하는 형태로 데이터 구성
                                                RoomCheckResponseDTO roomData = RoomCheckResponseDTO.builder()
                                                                .id(String.valueOf(room.getRoomindex()))
                                                                .name(room.getRoomname())
                                                                .room_index(String.valueOf(room.getRoomindex()))
                                                                .build();

                                                log.info("checkRoom: 반환할 방 데이터={}", roomData);
                                                log.info("checkRoom: roomData.getId()={}", roomData.getId());
                                                log.info("checkRoom: roomData.getName()={}", roomData.getName());
                                                log.info("checkRoom: roomData.getRoom_index()={}",
                                                                roomData.getRoom_index());

                                                return ResponseDTO.createSuccessResponse("기존 1:1 채팅방이 존재합니다.",
                                                                roomData);
                                        } else {
                                                log.warn("checkRoom: 방 정보를 찾을 수 없음 - room_index={}", existingRoomIndex);
                                        }
                                } else {
                                        log.warn("checkRoom: 1:1 채팅방이 아님 - 참여자 수={}", roomParticipants.size());
                                }
                        }

                        // 기존 방이 없는 경우, 새로운 room_index 생성
                        log.info("checkRoom: 기존 1:1 채팅방이 존재하지 않습니다. 새로운 room_index 생성 중...");
                        
                        // 현재 DB에 있는 최대 room_index 조회
                        Long maxRoomIndex = roomRepository.findMaxRoomIndex();
                        Long newRoomIndex = (maxRoomIndex != null) ? maxRoomIndex + 1 : 1L;
                        
                        log.info("checkRoom: 새로운 room_index 생성 - maxRoomIndex={}, newRoomIndex={}", maxRoomIndex, newRoomIndex);
                        
                        // 새로운 방 데이터 생성 (실제 방은 sendMessage에서 생성됨)
                        RoomCheckResponseDTO newRoomData = RoomCheckResponseDTO.builder()
                                        .id(String.valueOf(newRoomIndex))
                                        .name("새로운 채팅방")
                                        .room_index(String.valueOf(newRoomIndex))
                                        .isNewRoom(true)  // 새 방임을 표시
                                        .build();
                        
                        log.info("checkRoom: 새로운 방 데이터 반환={}", newRoomData);
                        return ResponseDTO.createSuccessResponse("새로운 채팅방을 생성할 수 있습니다.", newRoomData);

                } catch (Exception e) {
                        log.error("checkRoom Error: {}", e.getMessage(), e);
                        return ResponseDTO.createErrorResponse(500, "채팅방 확인 중 오류가 발생했습니다.");
                }
        }

        /**
         * Send Message
         */
        @Transactional
        public ResponseDTO<?> sendMessage(MessageRequestDTO messageRequestDTO) {
                try {
                        log.info("sendMessage 호출: room_index={}, room_name={}, user_id={}",
                                        messageRequestDTO.getRoom_index(), messageRequestDTO.getRoom_name(),
                                        messageRequestDTO.getUser_id());

                        // room_index가 null이거나 "null"이거나 빈 문자열이면 새 방 생성
                        boolean shouldCreateNewRoom = messageRequestDTO.getRoom_index() == null ||
                                        "null".equals(messageRequestDTO.getRoom_index()) ||
                                        messageRequestDTO.getRoom_index().trim().isEmpty();

                        if (shouldCreateNewRoom) {
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
                                                                        .userid(messageRequestDTO.getUser_id())
                                                                        .build());
                                        log.info("{}의 방생성 완료", participantId);
                                }
                                Message savedMessage = messageRepository.save(Message.builder()
                                                .userid(messageRequestDTO.getUser_id())
                                                .sentat(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                                                .message(messageRequestDTO.getMessage())
                                                .roomindex(savedRoom.getRoomindex())
                                                .active(true)
                                                .build());
                                log.info("메시지 저장 완료");
                                
                                // 파일 정보 저장
                                saveFilesToDatabase(savedMessage, messageRequestDTO.getUploadFiles(), 
                                                messageRequestDTO.getUser_id(), savedRoom.getRoomindex());
                                // 메세지 읽었는지 확인
                                for (String participantId : messageRequestDTO.getParticipants()) {
                                        messageReadRepository.save(MessageReads.builder()
                                                        .messageindex(savedMessage.getMessageindex())
                                                        .userid(participantId)
                                                        .readat(participantId.equals(messageRequestDTO.getUser_id())
                                                                        ? LocalDateTime.now(ZoneId.of("Asia/Seoul"))
                                                                        : null)
                                                        .build());
                                        log.info("{}의 메세지 읽었는지 확인", participantId);
                                }
                                // 메시지 로그 저장
                                chatLogRepository.save(ChatLog.builder()
                                                .message(messageRequestDTO.getMessage())
                                                .logtype("Message")
                                                .sentat(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                                                .roomindex(savedRoom.getRoomindex())
                                                .userid(messageRequestDTO.getUser_id())
                                                .build());
                                log.info("채팅 로그 저장 완료 [반환값] : {}", savedRoom.getRoomindex());
                                log.info("방 생성 완료 - 방 ID: {}, 방 이름: {}", savedRoom.getRoomindex(),
                                                savedRoom.getRoomname());
                                
                                messageRequestDTO.setRoom_index(String.valueOf(savedRoom.getRoomindex()));
                                
                                // messageindex와 room_index를 모두 포함한 Map 반환
                                Map<String, Object> responseData = new HashMap<>();
                                responseData.put("room_index", savedRoom.getRoomindex());
                                responseData.put("messageindex", savedMessage.getMessageindex());
                                
                                return ResponseDTO.createSuccessResponse("방 생성 및 메세지 전송 성공", responseData);
                        } else {
                                // 기존 방에 메세지를 보낼때
                                Long roomIndex = Long.parseLong(messageRequestDTO.getRoom_index());
                                log.info("기존 방에 메시지 전송: room_index={}", roomIndex);

                                // 해당 room_index가 실제로 존재하는지 확인
                                Room existingRoom = roomRepository.findById(roomIndex).orElse(null);
                                
                                if (existingRoom == null) {
                                        log.info("room_index={}인 방이 존재하지 않습니다. 방을 먼저 생성합니다.", roomIndex);
                                        
                                        // 방 생성
                                        Room savedRoom = roomRepository.save(Room.builder()
                                                        .roomname(messageRequestDTO.getRoom_name() != null
                                                                        ? messageRequestDTO.getRoom_name()
                                                                        : messageRequestDTO.getUser_id())
                                                        .createdat(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                                                        .createdby(null)
                                                        .build());
                                        
                                        // 방 참여자 저장
                                        for (String participantId : messageRequestDTO.getParticipants()) {
                                                roomParticipantsRepository.save(RoomParticipants.builder()
                                                                .joinedat(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                                                                .leftat(null)
                                                                .notificationsenabled(true)
                                                                .userid(participantId)
                                                                .roomindex(savedRoom.getRoomindex())
                                                                .build());
                                                log.info("방 참여자 {} 저장 완료", participantId);
                                        }
                                        
                                        log.info("방 생성 완료 - 방 ID: {}, 방 이름: {}", savedRoom.getRoomindex(), savedRoom.getRoomname());
                                        
                                        // 생성된 방의 room_index로 메시지 저장
                                        roomIndex = savedRoom.getRoomindex();
                                }

                                Message savedMessage = messageRepository.save(Message.builder()
                                                .userid(messageRequestDTO.getUser_id())
                                                .sentat(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                                                .message(messageRequestDTO.getMessage())
                                                .active(true)
                                                .roomindex(roomIndex)
                                                .build());
                                log.info("채팅 저장 완료");
                                
                                // 파일 정보 저장
                                saveFilesToDatabase(savedMessage, messageRequestDTO.getUploadFiles(), 
                                                messageRequestDTO.getUser_id(), roomIndex);
                                chatLogRepository.save(ChatLog.builder()
                                                .message(messageRequestDTO.getMessage())
                                                .logtype("Message")
                                                .sentat(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                                                .roomindex(roomIndex)
                                                .userid(messageRequestDTO.getUser_id())
                                                .build());
                                log.info("채팅 로그 저장 완료");
                                for (RoomParticipants participantId : roomParticipantsRepository
                                                .findByRoomindex(roomIndex)) {
                                        messageReadRepository.save(MessageReads.builder()
                                                        .messageindex(savedMessage.getMessageindex())
                                                        .userid(participantId.getUserid())
                                                        .readat(participantId.getUserid()
                                                                        .equals(messageRequestDTO.getUser_id())
                                                                                        ? LocalDateTime.now(ZoneId.of(
                                                                                                        "Asia/Seoul"))
                                                                                        : null)
                                                        .build());

                                        log.info("{}의 메세지 읽었는지 확인", participantId.getUserid());
                                }
                                log.info("기존 방에 메시지 전송 - 방 ID: {}", roomIndex);
                                
                                // messageindex와 room_index를 모두 포함한 Map 반환
                                Map<String, Object> responseData = new HashMap<>();
                                responseData.put("room_index", roomIndex);
                                responseData.put("messageindex", savedMessage.getMessageindex());
                                
                                return ResponseDTO.createSuccessResponse("메세지 전송 성공", responseData);
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
                        // 이미 방에 참여하고 있는지 확인
                        RoomParticipants existingParticipant = roomParticipantsRepository
                                        .findByUseridAndRoomindex(userid, Long.parseLong(room));

                        if (existingParticipant != null) {
                                log.info("{}는 이미 방에 참여하고 있습니다.", userid);
                                continue; // 이미 참여 중이면 건너뛰기
                        }

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
         * 메시지의 파일 정보 조회
         */
        private List<Map<String, Object>> getMessageFiles(Long messageIndex) {
                try {
                        log.info("🔍 파일 조회 시작: messageIndex={}", messageIndex);
                        
                        List<files> filesList = filesRepository.findByMessageindex(messageIndex);
                        log.info("🔍 파일 조회 결과: messageIndex={}, 파일 수={}", messageIndex, filesList.size());
                        
                        List<Map<String, Object>> fileInfoList = new ArrayList<>();
                        for (files file : filesList) {
                                Map<String, Object> fileInfo = new HashMap<>();
                                fileInfo.put("name", file.getFilename());
                                fileInfo.put("type", file.getFiletype());
                                fileInfo.put("url", file.getFilepath());
                                fileInfo.put("size", 0); // S3에서 직접 조회하지 않으므로 0으로 설정
                                fileInfoList.add(fileInfo);
                                
                                log.info("🔍 파일 정보: name={}, type={}, url={}", 
                                                file.getFilename(), file.getFiletype(), file.getFilepath());
                        }
                        
                        log.info("🔍 파일 정보 반환: messageIndex={}, 파일 정보 수={}", messageIndex, fileInfoList.size());
                        return fileInfoList;
                } catch (Exception e) {
                        log.error("파일 정보 조회 중 오류: messageIndex={}, error={}", messageIndex, e.getMessage(), e);
                        return new ArrayList<>();
                }
        }

        /**
         * Chat List
         */
        public List<Map<String, Object>> ChatList(String room, String userid, int page, int size) {
                try {
                        log.info("ChatList 호출: room='{}', userid='{}', page={}, size={}", room, userid, page, size);
                        log.info("ChatList: room 타입={}, 값='{}'",
                                        room != null ? room.getClass().getSimpleName() : "null", room);

                        if (room == null || "undefined".equals(room) || "null".equals(room) || room.trim().isEmpty()) {
                                log.error("ChatList Error: 유효하지 않은 room 파라미터: '{}'", room);
                                return null;
                        }

                        Long roomIndex;
                        try {
                                roomIndex = Long.parseLong(room);
                                log.info("ChatList: 파싱된 room_index={}", roomIndex);
                        } catch (NumberFormatException e) {
                                log.error("ChatList Error: room을 Long으로 파싱할 수 없음: '{}'", room);
                                return null;
                        }

                        Page<Message> messages = messageRepository.findByRoomindexOrderBySentatDesc(
                                        roomIndex,
                                        PageRequest.of(page, size));

                        log.info("ChatList: 조회된 메시지 수={}", messages.getContent().size());
                        
                        // active 상태 로깅 추가
                        for (Message msg : messages.getContent()) {
                            log.debug("메시지 active 상태: messageIndex={}, active={}", msg.getMessageindex(), msg.getActive());
                        }

                        // 메세지 읽음 처리
                        // 사용자 읽음 상태 조회
                        List<MessageReads> messageReads = messageReadRepository.findMessageReadsByRoomAndUser(
                                        roomIndex, userid);

                        log.info("ChatList: 사용자 {}의 읽음 상태 조회 완료", userid);

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
                                log.info("ChatList: 읽지 않은 메시지 {}개 업데이트 완료", unreadMessages.size());
                        }

                        // 메시지에 파일 정보 추가
                        List<Map<String, Object>> messagesWithFiles = new ArrayList<>();
                        for (Message message : messages.getContent()) {
                                Map<String, Object> messageMap = new HashMap<>();
                                messageMap.put("messageindex", message.getMessageindex());
                                messageMap.put("userid", message.getUserid());
                                messageMap.put("message", message.getMessage());
                                messageMap.put("sentat", message.getSentat());
                                messageMap.put("roomindex", message.getRoomindex());
                                messageMap.put("active", message.getActive());
                                
                                // 파일 정보 추가
                                List<Map<String, Object>> files = getMessageFiles(message.getMessageindex());
                                messageMap.put("files", files);
                                
                                log.info("📝 메시지 파일 정보: messageIndex={}, 파일 수={}", 
                                                message.getMessageindex(), files.size());
                                
                                messagesWithFiles.add(messageMap);
                        }
                        
                        log.info("ChatList: 채팅 내용 조회 성공 - 메시지 수={}", messagesWithFiles.size());
                        return messagesWithFiles;
                } catch (Exception e) {
                        log.error("ChatList Error: {}", e.getMessage(), e);
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

        /**
         * 메세지 삭제 (Soft Delete)
         */
        public ResponseDTO<?> DeleteMessage(String room_index, String message_index) {
                try {
                        // 메시지 조회
                        Message message = messageRepository.findById(Long.parseLong(message_index))
                                .orElseThrow(() -> new RuntimeException("메시지를 찾을 수 없습니다."));
                        
                        // Soft Delete - active를 0으로 설정
                        message.setActive(false);
                        messageRepository.save(message);
                        
                        return ResponseDTO.createSuccessResponse("메세지 삭제 성공", null);
                } catch (Exception e) {
                        log.error("DeleteMessage Error: {}", e.getMessage());
                        return ResponseDTO.createErrorResponse(404, e.getMessage());
                }
        }
}
