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
@Table(name = "chatlog")
@Builder
public class ChatLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_index", nullable = false)
    private Long logindex;
    @Column(name = "message", nullable = false)
    private String message;
    @Column(name = "log_type", nullable = false)
    private String logtype;
    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentat;
    @Column(name = "room_index", nullable = false)
    private Long roomindex;
    @Column(name = "user_id", nullable = false)
    private String userid;
}
