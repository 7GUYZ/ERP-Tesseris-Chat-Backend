package com.ict.protice01.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataVO {
    private boolean result;
    private Object data;
    private String message;
}
