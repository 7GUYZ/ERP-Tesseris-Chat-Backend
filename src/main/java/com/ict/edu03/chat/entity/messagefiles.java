package com.ict.edu03.chat.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "messagefiles")
@Builder
public class messagefiles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_index")
    private Long messageindex; // 메시지 인덱스
    @Column(name = "file_index")
    private Long fileindex; // 파일 인덱스
    
    // FK 관계 설정 - 순환참조 방지
    @ManyToOne
    @JoinColumn(name = "message_index", insertable = false, updatable = false)
    @JsonIgnore
    private Message message;

    // FK 관계 설정 - 순환참조 방지
    @ManyToOne
    @JoinColumn(name = "file_index", insertable = false, updatable = false)
    @JsonIgnore
    private files file;
}