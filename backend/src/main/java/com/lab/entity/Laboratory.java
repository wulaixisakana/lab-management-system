package com.lab.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Laboratory {
    private Integer id;
    private String name;
    private String code;
    private String building;
    private String floor;
    private String roomNumber;
    private BigDecimal area;
    private Integer capacity;
    private String supervisor;
    private String phone;
    private String status;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
