package com.example.redisdemo.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order implements Serializable {
    private static final long serialVersionUID = 2L;
    
    private Long id;
    private Long userId;
    private String orderNo;
    private BigDecimal amount;
    private String status;
    private String paymentMethod;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}