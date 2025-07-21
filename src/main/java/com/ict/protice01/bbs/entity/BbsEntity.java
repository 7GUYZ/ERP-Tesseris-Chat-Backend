package com.ict.protice01.bbs.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "bbs_t")
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
public class BbsEntity {
    @Id
    @GeneratedValue
    private Long b_idx;
    private String subject;
    private String writer;
    private String content;
    @Column(name = "f_name")
    @JsonProperty("f_name")
    private String f_name;
    private String pwd;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "write_date")
    private LocalDateTime writedate;
    private int hit;
    @Column(name = "active")
    private int active;
    
    @OneToMany(mappedBy = "guestBookEntity", cascade = CascadeType.ALL, orphanRemoval = true , fetch = FetchType.LAZY)
    private List<CommentEntity> commentEntities = new ArrayList<>();
}