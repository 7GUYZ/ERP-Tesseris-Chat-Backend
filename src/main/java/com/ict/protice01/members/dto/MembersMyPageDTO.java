package com.ict.protice01.members.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MembersMyPageDTO {
    private String m_id;
    private String m_name;
    private String m_phone;
    private String m_addr;
    private String m_addr2;
    private String m_email;
    private String m_reg;
}
