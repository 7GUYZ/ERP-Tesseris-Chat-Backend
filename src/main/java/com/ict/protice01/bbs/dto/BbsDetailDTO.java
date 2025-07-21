package com.ict.protice01.bbs.dto;

import java.util.List;

import com.ict.protice01.bbs.entity.CommentEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BbsDetailDTO {
    private String b_idx;
    private String subject;
    private String writer;
    private String content;
    private String f_name;
    private String write_date;
    private List<CommentEntity> comments;
}
