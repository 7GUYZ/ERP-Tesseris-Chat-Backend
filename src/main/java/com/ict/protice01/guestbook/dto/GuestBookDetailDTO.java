package com.ict.protice01.guestbook.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuestBookDetailDTO {
    private Long gb_idx;
    private String gb_name;
    private String gb_subject;
    private String gb_content;
    private String gb_regdate;
    private String gb_f_name;
    private String gb_old_file_name;
}
