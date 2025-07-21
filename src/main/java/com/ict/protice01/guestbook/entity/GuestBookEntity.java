package com.ict.protice01.guestbook.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@DynamicUpdate
@Table(name = "guestbook")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class GuestBookEntity {
    @Id
    @GeneratedValue
    private Long gb_idx;
    private String gb_name;

    @Column(name = "gb_subject")
    @JsonProperty("gb_subject")
    private String gbsubject;

    @Column(name = "gb_content")
    @JsonProperty("gb_content")
    private String gbcontent;
    private String gb_email;

    @Column(name = "gb_f_name")
    @JsonProperty("gb_f_name")
    private String gbfname;

    @Column(name = "gb_regdate")
    @JsonProperty("gb_regdate")
    private LocalDateTime gbregdate;
    private String gb_pw;

    @Column(name = "gb_old_file_name")
    @JsonProperty("gb_old_file_name")
    private String gb_old_file_name;
}
