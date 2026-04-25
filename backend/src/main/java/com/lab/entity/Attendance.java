package com.lab.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Attendance {
    private Integer id;
    private Integer userId;
    private String userName;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private String status;
    private Integer duration;
    private LocalDateTime createTime;
}
