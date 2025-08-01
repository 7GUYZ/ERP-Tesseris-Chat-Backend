package com.ict.edu03.chat.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "room")
@Entity
@Builder
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_index", nullable = false)
    private Long roomindex;
    
    @Column(name = "room_name", nullable = true)
    private String roomname;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdat;
    
    @Column(name = "created_by", nullable = true)
    private LocalDateTime createdby;
    
    // 양방향 관계 설정 - 순환참조 방지
    @OneToMany(mappedBy = "room")
    @JsonIgnore
    private List<Message> messages;
    
    @OneToMany(mappedBy = "room")
    private List<RoomParticipants> roomParticipants;
}
