package com.star.aries.auth.dal.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SysUser implements Serializable{

    private static final long serialVersionUID = 2670132280605789183L;
    private Integer id;

    private String account;

    private String name;

    private String phone;

    private String email;

    private Integer merchantId;

    private String merchantName;

    private String password;

    private String salt;

    private Integer type;

    private Integer status;

    private boolean isDeleted;

    private Date lastLogin;

    private Date createTime;

    private Date modifyTime;

    private Integer creator;

    private Integer modifier;

}
