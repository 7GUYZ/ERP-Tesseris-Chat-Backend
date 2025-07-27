package com.ict.edu03.chat.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import org.springframework.stereotype.Service;
import com.ict.edu03.chat.dto.ChatRoom;

@Service
public class ChatRoomService {
    // 채팅방 목록을 저장하는 맵 (roomId -> ChatRoom)
    private final Map<String, ChatRoom> chatRooms = new LinkedHashMap<>();
    
    // 채팅방별 세션 관리: roomId -> Set<sessionId>
    // 각 채팅방에 접속한 세션ID(사용자)를 저장합니다.
    private final Map<String, CopyOnWriteArraySet<String>> roomSessions = new ConcurrentHashMap<>();
    
    // 세션별 채팅방 관리: sessionId -> roomId
    // 각 세션이 어느 채팅방에 접속해 있는지 저장합니다.
    private final Map<String, String> sessionRooms = new ConcurrentHashMap<>();

    /**
     * 채팅방을 생성하고, 내부 맵에 저장합니다.
     * @param name 채팅방 이름
     * @return 생성된 ChatRoom 객체
     */
    public ChatRoom createRoom(String name) {
        ChatRoom room = ChatRoom.create(name);
        chatRooms.put(room.getRoomId(), room);
        roomSessions.put(room.getRoomId(), new CopyOnWriteArraySet<>());
        return room;
    }

    /**
     * 모든 채팅방 목록을 반환합니다.
     */
    public List<ChatRoom> findAllRooms() {
        return new ArrayList<>(chatRooms.values());
    }

    /**
     * roomId로 특정 채팅방 정보를 반환합니다.
     */
    public ChatRoom findRoomByid(String roomId) {
        return chatRooms.get(roomId);
    }
    
    /**
     * 사용자가 채팅방에 입장할 때 세션ID를 해당 방에 등록합니다.
     * @param roomId 방ID
     * @param sessionId 세션ID
     */
    public void joinRoom(String roomId, String sessionId) {
        roomSessions.computeIfAbsent(roomId, k -> new CopyOnWriteArraySet<>()).add(sessionId);
        sessionRooms.put(sessionId, roomId);
    }
    
    /**
     * 사용자가 채팅방에서 퇴장할 때 세션ID를 해당 방에서 제거합니다.
     * @param sessionId 세션ID
     */
    public void leaveRoom(String sessionId) {
        String roomId = sessionRooms.remove(sessionId);
        if (roomId != null) {
            CopyOnWriteArraySet<String> sessions = roomSessions.get(roomId);
            if (sessions != null) {
                sessions.remove(sessionId);
                // 빈 채팅방이면 세션 목록도 제거
                if (sessions.isEmpty()) {
                    roomSessions.remove(roomId);
                }
            }
        }
    }
    
    /**
     * 특정 채팅방의 현재 접속자(세션) 수를 반환합니다.
     * @param roomId 방ID
     * @return 세션 수
     */
    public int getRoomUserCount(String roomId) {
        CopyOnWriteArraySet<String> sessions = roomSessions.get(roomId);
        return sessions != null ? sessions.size() : 0;
    }
    
    /**
     * 특정 채팅방의 세션ID 목록을 반환합니다.
     * @param roomId 방ID
     * @return 세션ID 목록
     */
    public List<String> getRoomSessions(String roomId) {
        CopyOnWriteArraySet<String> sessions = roomSessions.get(roomId);
        return sessions != null ? new ArrayList<>(sessions) : new ArrayList<>();
    }
    
    /**
     * 모든 채팅방의 접속자 수 정보를 Map(roomId -> count)로 반환합니다.
     */
    public Map<String, Integer> getAllRoomUserCounts() {
        Map<String, Integer> userCounts = new LinkedHashMap<>();
        for (String roomId : chatRooms.keySet()) {
            userCounts.put(roomId, getRoomUserCount(roomId));
        }
        return userCounts;
    }
}
