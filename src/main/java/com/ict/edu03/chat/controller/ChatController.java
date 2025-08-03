package com.ict.edu03.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ict.edu03.chat.dto.ResponseDTO;
import com.ict.edu03.chat.dto.SearchResponseDTO;
import com.ict.edu03.chat.dto.RequestDTO.AlarmCheckRequestDTO;
import com.ict.edu03.chat.dto.RequestDTO.InvitationRequestDTO;
import com.ict.edu03.chat.dto.RequestDTO.MessageRequestDTO;
import com.ict.edu03.chat.service.ChatService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/adminchat")
public class ChatController {
    private final ChatService chatService;

    /**
     * Search Room
     * 
     * @param userid
     * @return
     */
    @GetMapping("/{userid}")
    public ResponseDTO<?> SearchRoom(@PathVariable("userid") String userid) {
        try {
            return chatService.SearchRoom(userid);
        } catch (RuntimeException e) {
            log.error("SearchRoom Error: {}", e.getMessage());
            return ResponseDTO.createErrorResponse(404, e.getMessage());
        } catch (Exception e) {
            log.error("SearchRoom Error: {}", e.getMessage());
            return ResponseDTO.createErrorResponse(400, "서버오류");
        }
    }

    /**
     * Check Room - 1:1 채팅방 존재 여부 확인
     * 
     * @param messageRequestDTO
     * @return
     */
    @PostMapping(value = "/checkroom")
    public ResponseDTO<?> CheckRoom(@RequestBody MessageRequestDTO messageRequestDTO) {
        try {
            return chatService.checkRoom(messageRequestDTO);
        } catch (Exception e) {
            log.error("CheckRoom Error: {}", e.getMessage());
            return ResponseDTO.createErrorResponse(404, e.getMessage());
        }
    }

    /**
     * Send Message
     * 
     * @param messageRequestDTO
     * @return
     */
    @PostMapping(value = "/sendmessage")
    public ResponseDTO<?> SendMessage(@RequestBody MessageRequestDTO messageRequestDTO) {
        try {
            return chatService.sendMessage(messageRequestDTO);
        } catch (Exception e) {
            log.error("SendMessage Error: {}", e.getMessage());
            return ResponseDTO.createErrorResponse(404, e.getMessage());
        }
    }

    /**
     * user invitation
     * 
     * @param roomindex
     * @return
     */
    @PostMapping("/{room}/invitation")
    public ResponseEntity<?> UserInvitation(@PathVariable("room") String room,
            @RequestBody InvitationRequestDTO invitationRequestDTO) {
        try {
            chatService.userInvitation(room, invitationRequestDTO);
            return ResponseEntity.ok(ResponseDTO.createSuccessResponse("유저 초대 성공", null));
        } catch (Exception e) {
            log.error("UserInvitation Error: {}", e.getMessage());
            return ResponseEntity.ok(ResponseDTO.createErrorResponse(404, e.getMessage()));
        }
    }

    /**
     * check alram
     */
    @PutMapping("/alarm")
    public ResponseEntity<?> CheckAlram(@RequestBody AlarmCheckRequestDTO alarmCheck) {
        try {
            log.info("CheckAlram: {}", alarmCheck);
            return ResponseEntity.ok(chatService.checkAlram(alarmCheck));
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return ResponseEntity.ok(ResponseDTO.createErrorResponse(404, e.getMessage()));
        }
    }

    /**
     * 채팅방 채팅 내용 조회
     * 
     * @param room
     * @return
     */
    @GetMapping("/{room}/chatlist/{userid}")
    public ResponseEntity<ResponseDTO<?>> ChatList(@PathVariable("room") String room,
            @PathVariable("userid") String userid,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "25") int size) {
        try {
            log.info("ChatList: {}", room);
            log.info("ChatList: {}", page);
            log.info("ChatList: {}", size);
            return ResponseEntity.ok(
                    ResponseDTO.createSuccessResponse("채팅 내용 조회 성공", chatService.ChatList(room, userid, page, size)));
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return ResponseEntity.ok(ResponseDTO.createErrorResponse(404, e.getMessage()));
        }
    }

    /**
     * 위치에 따른 사용자 읽음 처리
     * 고려사항
     */
    @PostMapping("/{room}/read/{messageid}/{userid}")
    public ResponseEntity<?> MessageRead(@PathVariable("room") String room, @PathVariable("messageid") String messageid,
            @PathVariable("userid") String userid) {
        try {
            return ResponseEntity.ok(chatService.MessageRead(room, messageid, userid));
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return ResponseEntity.ok(chatService.MessageRead(room, messageid, userid));
        }
    }

    /**
     * 방 퇴장시 나간 사람 읽음처리 구분을 위한 나간 시간체크
     */
    @PutMapping("/{room}/leave/{userid}")
    public ResponseEntity<ResponseDTO<?>> Leave(@PathVariable("room") String room,
            @PathVariable("userid") String userid) {
        try {
            return ResponseEntity.ok(chatService.Leave(room, userid));
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return ResponseEntity.ok(ResponseDTO.createErrorResponse(404, e.getMessage()));
        }
    }
}
