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
} 