package com.lab.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Equipment {
    private Integer id;
    private String name;
    private String code;
    private String category;
    private String brand;
    private String model;
    private String location;
    private String status;
    private BigDecimal price;
    private String description;
    private LocalDateTime purchaseDate;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
