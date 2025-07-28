package com.ict.edu03.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ict.edu03.chat.dto.ResponseDTO;
import com.ict.edu03.chat.dto.SearchResponseDTO;
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

    @RequestMapping("/api/adminchat")
    public class AdminChatController {
        
        /**
         * New Create Room API
         */
        @PostMapping("/roomcreate")
        public ResponseEntity<RoomRequestDTO> RoomCreate(@RequestBody RoomRequestDTO roomResponseDTO) {
            if (roomResponseDTO.getRoom_index() == null) {
                log.info("RoomCreate {}", roomResponseDTO);
                return ResponseEntity.ok(roomResponseDTO);
            } else {
                log.info("SearchRoom {}", roomResponseDTO);
                return ResponseEntity.ok(roomResponseDTO);
            }
        }
        
        /**
         * Search Room
         * @param userid
         * @return
         */
        @GetMapping("/{userid}")
        public ResponseEntity<ResponseDTO<?>> SearchRoom(@PathVariable("userid") String userid) {
            try {
                List<SearchResponseDTO> searchResponseDTOList = chatService.SearchRoom(userid);
                return ResponseEntity.ok(ResponseDTO.createSuccessResponse(null ,searchResponseDTOList));
            }catch (RuntimeException e) {
                log.error("SearchRoom Error: {}", e.getMessage());
                return ResponseEntity.ok(ResponseDTO.createErrorResponse(404, e.getMessage()));
            }
            catch (Exception e) {
                log.error("SearchRoom Error: {}", e.getMessage());
                return ResponseEntity.ok(ResponseDTO.createErrorResponse(400, "서버오류"));
            }
        }
    }
}
