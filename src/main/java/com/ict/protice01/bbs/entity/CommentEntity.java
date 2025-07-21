package com.ict.protice01.bbs.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "comment_t")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class CommentEntity {
    @Id
    @GeneratedValue
    private Long c_idx;

    private String writer;
    private String content;
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime write_date;

    @ManyToOne
    @JoinColumn(name = "b_idx")
    @JsonBackReference
    private BbsEntity guestBookEntity;
    
}
