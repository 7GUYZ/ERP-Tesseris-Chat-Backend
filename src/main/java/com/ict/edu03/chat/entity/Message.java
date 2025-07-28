package com.ict.edu03.chat.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "message")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_index", nullable = false)
    private Long messageindex;
    
    @Column(name = "user_id", nullable = false)
    private String userid;
    
    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentat;
    
    @Column(name = "message", nullable = false)
    private String message;
    
    @Column(name = "room_index", nullable = false)
    private Long roomindex;
    
    @Column(name = "active", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean active;
    
    // FK 관계 설정 - 순환참조 방지
    @ManyToOne
    @JoinColumn(name = "room_index", insertable = false, updatable = false)
    @JsonIgnore
    private Room room;
    
    // 양방향 관계 설정 - 순환참조 방지
    @OneToMany(mappedBy = "message")
    @JsonIgnore
    private List<MessageReads> messageReads;
}
