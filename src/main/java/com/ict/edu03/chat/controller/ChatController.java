package com.ict.edu03.chat.controller;

import java.util.List;
import java.util.Map;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ict.edu03.chat.dto.ChatMessage;
import com.ict.edu03.chat.dto.ChatRoom;
import com.ict.edu03.chat.service.ChatRoomService;

import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatRoomService chatRoomService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 채팅방을 생성하는 REST API입니다.
     * 프론트엔드에서 채팅방 이름을 받아 새로운 ChatRoom 객체를 생성하고,
     * 생성된 방 정보를 모든 구독자에게 /topic/rooms로 브로드캐스트합니다.
     */
    @PostMapping("/room")
    public ChatRoom createRoom(@RequestBody ChatRoom request) {
        ChatRoom newRoom = chatRoomService.createRoom(request.getName());
        messagingTemplate.convertAndSend("/topic/rooms", newRoom);
        return newRoom;
    }

    /**
     * 모든 채팅방 목록을 조회하는 REST API입니다.
     * ChatRoomService에서 관리하는 모든 방 정보를 반환합니다.
     */
    @GetMapping("/rooms")
    public List<ChatRoom> getRoomList() {
        return chatRoomService.findAllRooms();
    }

    /**
     * 특정 채팅방의 정보를 조회하는 REST API입니다.
     * roomId로 해당 방의 정보를 반환합니다.
     */
    @GetMapping("/room/{roomId}")
    public ChatRoom getRoom(@PathVariable String roomId) {
        return chatRoomService.findRoomByid(roomId);
    }
    
    /**
     * 특정 채팅방의 접속자 수를 조회하는 REST API입니다.
     * roomId로 해당 방의 현재 세션(접속자) 수를 반환합니다.
     */
    @GetMapping("/room/{roomId}/users/count")
    public ResponseEntity<Map<String, Object>> getRoomUserCount(@PathVariable String roomId) {
        int userCount = chatRoomService.getRoomUserCount(roomId);
        Map<String, Object> response = Map.of(
            "roomId", roomId,
            "userCount", userCount
        );
        return ResponseEntity.ok(response);
    }
    
    /**
     * 모든 채팅방의 접속자 수를 조회하는 REST API입니다.
     * 각 방의 roomId와 접속자 수를 Map 형태로 반환합니다.
     */
    @GetMapping("/rooms/users/count")
    public ResponseEntity<Map<String, Integer>> getAllRoomUserCounts() {
        Map<String, Integer> userCounts = chatRoomService.getAllRoomUserCounts();
        return ResponseEntity.ok(userCounts);
    }
    
    /**
     * 특정 채팅방의 세션(접속자) 목록을 조회하는 REST API입니다.
     * roomId로 해당 방에 접속한 세션ID 목록과 총 세션 수를 반환합니다.
     */
    @GetMapping("/room/{roomId}/sessions")
    public ResponseEntity<Map<String, Object>> getRoomSessions(@PathVariable String roomId) {
        List<String> sessions = chatRoomService.getRoomSessions(roomId);
        Map<String, Object> response = Map.of(
            "roomId", roomId,
            "sessions", sessions,
            "count", sessions.size()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * 클라이언트가 /app/chat.sendMessage/{roomId}로 메시지를 전송하면,
     * 해당 메시지를 /topic/{roomId}로 브로드캐스트하는 WebSocket 핸들러입니다.
     * 즉, 같은 방을 구독한 모든 사용자에게 메시지가 전달됩니다.
     */
    @MessageMapping("/chat.sendMessage/{roomId}")
    @SendTo("/topic/{roomId}")
    public ChatMessage sendMessage(@DestinationVariable("roomId") String roomId, ChatMessage message) {
        message.setRoom(roomId);
        return message;
    }
    
    /**
     * (참고용) 채팅방 구독 시 호출되는 핸들러입니다.
     * 실제 세션 관리는 WebSocketConfig의 SessionSubscribeEvent에서 처리하므로,
     * 이 메서드는 참고용으로 남겨둡니다.
     */
    @SubscribeMapping("/topic/{roomId}")
    public void handleUserJoined(@DestinationVariable("roomId") String roomId, 
                                SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        if (sessionId != null) {
            chatRoomService.joinRoom(roomId, sessionId);
            log.info("사용자 입장 - 방: {}, 세션: {}, 현재 접속자 수: {}", 
                    roomId, sessionId, chatRoomService.getRoomUserCount(roomId));
            
            // 접속자 수 업데이트를 모든 구독자에게 전송
            int userCount = chatRoomService.getRoomUserCount(roomId);
            messagingTemplate.convertAndSend("/topic/" + roomId + "/users/count", 
                    Map.of("userCount", userCount));
        }
    }
    
    /**
     * (참고용) WebSocket 연결 해제 시 호출되는 핸들러입니다.
     * 실제 세션 관리는 WebSocketConfig의 SessionDisconnectEvent에서 처리하므로,
     * 이 메서드는 참고용으로 남겨둡니다.
     */
    public void handleUserLeft(String sessionId) {
        chatRoomService.leaveRoom(sessionId);
        log.info("사용자 퇴장 - 세션: {}", sessionId);
    }
}
