package com.ict.edu03.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ict.edu03.chat.dto.ResponseDTO;
import com.ict.edu03.chat.dto.SearchResponseDTO;
import com.ict.edu03.chat.dto.RequestDTO.InvitationRequestDTO;
import com.ict.edu03.chat.dto.RequestDTO.MessageRequestDTO;
import com.ict.edu03.chat.dto.RequestDTO.RoomRequestDTO;
import com.ict.edu03.chat.service.ChatService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @GetMapping("/")
    public String home() {
        return "Chat Service is running!";
    }

    @GetMapping("/hello")
    public String hello() {
        return "하위요";
    }

    /**
     * Search Room
     * 
     * @param userid
     * @return
     */
    @GetMapping("/{userid}")
    public ResponseEntity<ResponseDTO<?>> SearchRoom(@PathVariable("userid") String userid) {
        try {
            List<SearchResponseDTO> searchResponseDTOList = chatService.SearchRoom(userid);
            return ResponseEntity.ok(ResponseDTO.createSuccessResponse(null, searchResponseDTOList));
        } catch (RuntimeException e) {
            log.error("SearchRoom Error: {}", e.getMessage());
            return ResponseEntity.ok(ResponseDTO.createErrorResponse(404, e.getMessage()));
        } catch (Exception e) {
            log.error("SearchRoom Error: {}", e.getMessage());
            return ResponseEntity.ok(ResponseDTO.createErrorResponse(400, "서버오류"));
        }
    }

    /**
     * Send Message
     * 
     * @param messageRequestDTO
     * @return
     */
    @PostMapping(value = "/sendmessage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDTO<?>> SendMessage(@RequestPart("message") String messageRequestDTOs,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            MessageRequestDTO messageRequestDTO = objectMapper.readValue(messageRequestDTOs, MessageRequestDTO.class);

            if (messageRequestDTO.getRoom_index() == null) {
                chatService.sendMessage(messageRequestDTO, files);
                return ResponseEntity.ok(ResponseDTO.createSuccessResponse("방 생성 및 메세지 전송 성공", null));
            } else {
                chatService.sendMessage(messageRequestDTO, files);
                return ResponseEntity.ok(ResponseDTO.createSuccessResponse("메세지 전송 성공", null));
            }
        } catch (Exception e) {
            log.error("SendMessage Error: {}", e.getMessage());
            return ResponseEntity.ok(ResponseDTO.createErrorResponse(404, e.getMessage()));
        }
    }

    /**
     * user invitation
     * 
     * @param roomindex
     * @return
     */
    @PostMapping("/{room}/invitation")
    public ResponseEntity<?> UserInvitation(@PathVariable("room") String room, @RequestBody InvitationRequestDTO invitationRequestDTO) {
        try {
            chatService.userInvitation(room, invitationRequestDTO);
            return ResponseEntity.ok(ResponseDTO.createSuccessResponse("유저 초대 성공", null));
        } catch (Exception e) {
            log.error("UserInvitation Error: {}", e.getMessage());
            return ResponseEntity.ok(ResponseDTO.createErrorResponse(404, e.getMessage()));
        }
    }
}
