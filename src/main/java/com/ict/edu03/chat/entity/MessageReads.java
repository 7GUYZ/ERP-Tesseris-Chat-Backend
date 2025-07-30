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
@Table(name = "messagereads")
@Builder
public class MessageReads {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "messagereads_index", nullable = false)
    private Long messagereadsindex;
    
    @Column(name = "read_at", nullable = true)
    private LocalDateTime readat;
    
    @Column(name = "user_id", nullable = false)
    private String userid;
    
    @Column(name = "message_index", nullable = false)
    private Long messageindex;
    
    // FK 관계 설정 - 순환참조 방지
    @ManyToOne
    @JoinColumn(name = "message_index", insertable = false, updatable = false)
    @JsonIgnore
    private Message message;
}
