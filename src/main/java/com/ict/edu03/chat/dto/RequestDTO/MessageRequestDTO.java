package com.ict.edu03.chat.dto.RequestDTO;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequestDTO {
    private String message; // 메시지 내용
    private String room_index; // 방 인덱스
    private String room_name; // 방 이름
    private String user_id; // 사용자 ID (프론트엔드에서 보내는 필드)
    private String timestamp; // 타임스탬프 (프론트엔드에서 보내는 필드)
    private List<String> participants; // 참여자 목록
    private List<MultipartFile> files; // 파일 목록
    private List<String> uploadFiles; // 업로드된 파일 목록
}
