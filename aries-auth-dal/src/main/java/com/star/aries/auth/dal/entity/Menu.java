package com.star.aries.auth.dal.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Menu implements Serializable {
    private Integer id;
    private Integer pid;
    private Boolean isParent;
    private String code;
    private String name;
    private String uri;
    private String icon;
    private Integer status;
    private Integer type;
    private Integer sort;
    private String description;
    private Integer clientId;
    private Integer merchantId;
    private Integer creator;
    private Integer modifier;
    private Date createTime;
    private Date modifyTime;
    private List<MenuAction> menuActions;
    private List<Menu> subMenus;
    private List<Role> roles;
}
