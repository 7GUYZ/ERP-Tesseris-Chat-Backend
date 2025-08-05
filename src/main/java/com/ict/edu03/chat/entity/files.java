package com.ict.edu03.chat.entity;

import java.time.LocalDateTime;

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
@Table(name = "files")
@Builder
public class files {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_index", nullable = false)
    private Long fileindex;    // 파일 인덱스
    @Column(name = "file_name", nullable = false)
    private String filename;   // 파일 이름
    @Column(name = "file_path", nullable = false)
    private String filepath;   // 파일 경로
    @Column(name = "file_type", nullable = false)
    private String filetype;   // 파일 타입
    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedat;   // 업로드 시간
    @Column(name = "room_index", nullable = false)
    private Long roomindex;   // 방 인덱스
    @Column(name = "user_id", nullable = false)
    private String userid;   // 사용자 아이디
    
    @Column(name = "message_index", nullable = false)
    private Long messageindex;   // 메시지 인덱스
    
    // FK 관계 설정 - 순환참조 방지
    @ManyToOne
    @JoinColumn(name = "room_index", insertable = false, updatable = false)
    @JsonIgnore
    private Room room;
    
    // FK 관계 설정 - 순환참조 방지
    @ManyToOne
    @JoinColumn(name = "message_index", insertable = false, updatable = false)
    @JsonIgnore
    private Message message;
}
