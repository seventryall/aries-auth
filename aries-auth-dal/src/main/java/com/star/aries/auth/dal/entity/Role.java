package com.star.aries.auth.dal.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    private Integer id;
    private String code;
    private String name;
    private String description;
    private String clientId;
    private String clientName;
    private Integer merchantId;
    private Date createTime;
    private Date modifyTime;
    private Integer creator;
    private Integer modifier;
    private List<Url> urls;
    private List<SysUser> users;
}
