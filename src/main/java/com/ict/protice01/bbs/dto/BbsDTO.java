package com.ict.protice01.bbs.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BbsDTO {
    private Long b_idx;
    private String subject;
    private String writer;
    private String content;
    private LocalDateTime write_date;
    private int hit;
}
