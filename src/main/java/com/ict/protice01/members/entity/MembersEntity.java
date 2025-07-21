package com.ict.protice01.members.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "members")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MembersEntity {
    @Id
    @GeneratedValue
    private Long m_idx;
    @Column(name="m_id")
    @JsonProperty("m_id")
    private String mid;
    @Column(name="m_pw")
    @JsonProperty("m_pw")
    private String mpw;
    private String m_name;
    private String m_addr;
    private String m_addr2;
    private String m_phone;
    private String m_email;
    private LocalDateTime m_reg;
    private String m_active;
    private LocalDateTime m_active_reg;
    private String sns_email_naver;
    private String sns_email_kakao;
    private String sns_provider;
}
