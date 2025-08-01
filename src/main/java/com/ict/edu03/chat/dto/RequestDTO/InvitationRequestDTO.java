package com.ict.edu03.chat.dto.RequestDTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvitationRequestDTO {
    private String inviter;
    private List<String> userid;
}
