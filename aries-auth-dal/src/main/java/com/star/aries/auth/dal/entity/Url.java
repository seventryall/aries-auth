package com.star.aries.auth.dal.entity;

import lombok.Data;

import java.util.Date;


@Data
public class Url {
    private Integer id;
    private String url;
    private String name;
    private String description;
    private String moduleName;
    private Integer clientId;
    private String  clientName;
    private Integer status;
    private Date createTime;
    private Date modifyTime;
    private Integer creator;
    private Integer modifier;
    private Boolean isSelected;
    private Integer merchantId;

}
