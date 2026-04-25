package com.lab.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Reservation {
    private Integer id;
    private Integer equipmentId;
    private Integer userId;
    private String userName;
    private String equipmentName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String purpose;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
