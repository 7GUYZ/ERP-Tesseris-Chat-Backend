package com.ict.edu03.chat.entity;

import java.time.LocalDate;
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
@Table(name = "roomparticipants")
@Builder
public class RoomParticipants {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "roomparticipants_index", nullable = false)
    private Long roomparticipantsindex;
    
    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedat;
    
    @Column(name = "left_at", nullable = true)
    private LocalDateTime leftat;
    
    @Column(name = "notifications_enabled", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean notificationsenabled;
    
    @Column(name = "user_id", nullable = false)
    private String userid;
    
    @Column(name = "room_index", nullable = false)
    private Long roomindex;
    
    // FK 관계 설정 - 순환참조 방지
    @ManyToOne
    @JoinColumn(name = "room_index", insertable = false, updatable = false)
    @JsonIgnore
    private Room room;
}
