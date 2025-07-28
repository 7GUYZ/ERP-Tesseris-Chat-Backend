package com.ict.edu03.chat.dto.RequestDTO;

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
}
