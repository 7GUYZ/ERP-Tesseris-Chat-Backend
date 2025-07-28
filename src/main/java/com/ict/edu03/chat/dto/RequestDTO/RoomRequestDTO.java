package com.ict.edu03.chat.dto.RequestDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomRequestDTO {
    private String room_index;
    private String room_name;
    private String created_at;
    private String created_by;
}
