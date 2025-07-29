package com.ict.edu03.chat.dto.RequestDTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequestDTO {
    private String message_index;
    private String user_id; 
    private String sent_at;
    private String message;
    private String room_index;
    private String active;

    // 방 생성에 필요한 데이터;
    private String room_name;        // 방 이름 (첫 메시지 시 필요)
    private String created_at;       // 방 생성 시간
    private String created_by;       // 방 생성자
    private List<String> participants;     // 참여자 목록 (JSON 문자열)
}
