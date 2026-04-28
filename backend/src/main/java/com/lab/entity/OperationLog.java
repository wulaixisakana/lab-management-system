package com.lab.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OperationLog {
    private Integer id;
    private Integer userId;
    private String userName;
    private String module;
    private String action;
    private String detail;
    private String ip;
    private LocalDateTime createTime;
}
