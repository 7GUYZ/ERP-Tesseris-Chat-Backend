package com.ict.protice01.common.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "refresh_token")
@Entity
public class RefreshEntity {
    @Id
    @Column(name = "m_id")
    @JsonProperty("m_id")
    private String mid;
    private String refresh_token;
    private Date expiry_date;
}
