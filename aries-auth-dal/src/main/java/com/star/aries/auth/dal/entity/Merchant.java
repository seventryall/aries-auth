package com.star.aries.auth.dal.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Merchant {
    private Integer id;
    private String name;
    private String phone;
    private String address;
    private String description;
    private Integer creator;
    private Integer modifier;
    private Date createTime;
    private Date modifyTime;
}
