package com.ict.edu03.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchResponseDTO {
    private String room_index;
    private String room_name;
    private String joined_at;
    private String left_at;
    private String notifications_enabled;
}
