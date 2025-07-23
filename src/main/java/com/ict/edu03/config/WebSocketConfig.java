package com.ict.edu03.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.context.event.EventListener;

import com.ict.edu03.chat.service.ChatRoomService;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Autowired
    private ChatRoomService chatRoomService;
    
    /**
     * 클라이언트가 WebSocket으로 접속할 수 있는 엔드포인트를 등록합니다.
     * setAllowedOriginPatterns("*")로 모든 도메인에서의 접속을 허용하며,
     * withSockJS()를 통해 SockJS(웹소켓 미지원 브라우저 호환)를 활성화합니다.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    /**
     * 메시지 브로커를 설정합니다.
     * setApplicationDestinationPrefixes("/app")는 클라이언트가 서버로 메시지를 보낼 때 사용하는 prefix입니다.
     * enableSimpleBroker("/topic")는 서버가 클라이언트에게 메시지를 보낼 때 사용하는 prefix입니다.
     * 즉, /app으로 시작하는 메시지는 @MessageMapping으로, /topic으로 시작하는 메시지는 브로커를 통해 전달됩니다.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic");
    }
    
    /**
     * WebSocket 연결이 해제(끊김)될 때 호출되는 이벤트 리스너입니다.
     * 세션ID를 추출하여 해당 사용자를 채팅방 세션 목록에서 제거합니다.
     * (브라우저 닫기, 새로고침, 네트워크 끊김 등)
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        
        if (sessionId != null) {
            // 사용자가 채팅방에서 퇴장 처리
            chatRoomService.leaveRoom(sessionId);
            System.out.println("사용자 연결 해제 - 세션: " + sessionId);
        }
    }

    /**
     * 클라이언트가 /topic/{roomId}로 구독(Subscribe)할 때마다 호출되는 이벤트 리스너입니다.
     * 세션ID와 destination(구독 주소)에서 roomId를 추출하여 해당 방에 세션을 등록합니다.
     * 이를 통해 실시간으로 채팅방 접속자 수 및 세션 목록을 관리할 수 있습니다.
     */
    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String destination = headerAccessor.getDestination(); // ex: /topic/{roomId}
        if (destination != null && destination.startsWith("/topic/")) {
            String roomId = destination.substring("/topic/".length());
            chatRoomService.joinRoom(roomId, sessionId);
            System.out.println("구독 이벤트: 방 " + roomId + ", 세션 " + sessionId);
        }
    }
}
