package com.ict.edu03.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomCheckResponseDTO {
    private String id;
    private String name;
    private String room_index;
    private Boolean isNewRoom;  // 새 방인지 여부를 나타내는 필드
} 