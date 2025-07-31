package com.ict.edu03.chat.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    @Column(name = "file_index")
    private Long fileindex;    // 파일 인덱스
    @Column(name = "file_name")
    private String filename;   // 파일 이름
    @Column(name = "file_path")
    private String filepath;   // 파일 경로
    @Column(name = "file_type")
    private String filetype;   // 파일 타입
    @Column(name = "uploaded_at")
    private LocalDateTime uploadedat;   // 업로드 시간
    @Column(name = "room_index")
    private Long roomindex;   // 방 인덱스
    @Column(name = "user_id")
    private String userid;   // 사용자 아이디
}
