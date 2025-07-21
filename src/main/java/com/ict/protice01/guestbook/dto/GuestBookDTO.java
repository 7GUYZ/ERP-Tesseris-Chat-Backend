package com.ict.protice01.guestbook.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuestBookDTO {
    private Long gb_idx;
    private String gb_name;
    private String gb_subject;
    private String gb_regdate;
}
